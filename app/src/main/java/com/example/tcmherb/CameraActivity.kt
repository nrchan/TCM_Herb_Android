package com.example.tcmherb

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import android.provider.Settings
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun CameraScreen(navController: NavController){
    Box (
        modifier = Modifier.fillMaxSize()
    ) {
        CameraView()
        FilledTonalButton(
            onClick = { navController.navigate("home"){ popUpTo("home") {inclusive = true} } },
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(Icons.Rounded.ArrowBack, "back button")
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

    LaunchedEffect(CameraSelector.LENS_FACING_BACK){
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, preview)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    if(cameraPermissionState.status.isGranted){
        //If permission granted
        Surface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            AndroidView(
                {previewView},
                Modifier.fillMaxSize()
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