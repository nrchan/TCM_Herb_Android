package com.example.tcmherb

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import android.provider.Settings
import android.util.Log
import android.util.Rational
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun CameraScreen(navController: NavController){
    Column(Modifier.fillMaxSize()) {
        FilledTonalButton(
            onClick = { navController.navigate("home"){ popUpTo("home") {inclusive = true} } },
            modifier = Modifier.padding(top = 32.dp, start = 32.dp)
        ) {
            Icon(Icons.Rounded.ArrowBack, "back button")
        }
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CameraView()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraView(){
    Modifier.fillMaxSize()

    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val imageBlurryAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
    imageBlurryAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), ImageAnalysis.Analyzer { imageProxy ->
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        // insert your code here.
        Log.d("Debug", imageProxy.cropRect.flattenToString())
        // after done, release the ImageProxy object
        imageProxy.close()
    })

    val imageCapture = ImageCapture.Builder()
        .setTargetRotation(LocalConfiguration.current.layoutDirection)
        .build()

    val viewPort =  ViewPort.Builder(Rational(1, 1), LocalConfiguration.current.layoutDirection).build()
    val useCaseGroup = UseCaseGroup.Builder()
        .addUseCase(preview)
        .addUseCase(imageBlurryAnalysis)
        .addUseCase(imageCapture)
        .setViewPort(viewPort)
        .build()

    LaunchedEffect(CameraSelector.LENS_FACING_BACK){
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, useCaseGroup)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    if(cameraPermissionState.status.isGranted){
        //If permission granted
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(32.dp)
                .wrapContentSize()
        ) {
            AndroidView(
                {previewView}
            )
        }
    }else{
        //If no permission granted
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("(◍°̧̧̧o°̧̧̧◍)", textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))
            Text("No camera permission granted.", textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                context.startActivity(intent)
            }) {
                Text("Grant Permission", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}