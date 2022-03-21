package com.example.tcmherb

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.provider.Settings
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
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
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfDouble
import org.opencv.imgproc.Imgproc
import java.lang.Math.pow
import java.nio.ByteBuffer

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

@SuppressLint("UnsafeOptInUsageError")
@OptIn(
    ExperimentalPermissionsApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class
)
@Composable
fun CameraView(){
    Modifier.fillMaxSize()

    var isBlur by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    //val imageCapture = ImageCapture.Builder().build()

    val imageBlurryAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    imageBlurryAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
        imageProxy.image?.let {
            val matImage = convertYUVtoMat(it)

            //use laplacian kernel to convolve in order to detect blurry image
            val destination = Mat()
            val matGray = Mat()
            Imgproc.cvtColor(matImage, matGray, Imgproc.COLOR_BGR2GRAY)
            Imgproc.Laplacian(matGray, destination, 3)
            val median = MatOfDouble()
            val std = MatOfDouble()
            Core.meanStdDev(destination, median, std)

            //setting 500 as threshold, the higher the number, the clearer the photo
            isBlur = pow(std[0, 0][0], 2.0) <= 500
        }

        imageProxy.close()
    }

    LaunchedEffect(CameraSelector.LENS_FACING_BACK){
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, preview, imageBlurryAnalysis)
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
            Box(contentAlignment = Alignment.BottomCenter){
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    border = BorderStroke(3.dp, color = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(45.dp),
                    modifier = Modifier.padding(32.dp).wrapContentSize().size(56.dp)
                ){}
            }
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

//Convert to something opencv can accept
private fun convertYUVtoMat(img: Image): Mat {
    val nv21: ByteArray
    val yBuffer: ByteBuffer = img.planes[0].buffer
    val uBuffer: ByteBuffer = img.planes[1].buffer
    val vBuffer: ByteBuffer = img.planes[2].buffer
    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()
    nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer[nv21, 0, ySize]
    vBuffer[nv21, ySize, vSize]
    uBuffer[nv21, ySize + vSize, uSize]
    val yuv = Mat(img.height + img.height / 2, img.width, CvType.CV_8UC1)
    yuv.put(0, 0, nv21)
    val rgb = Mat()
    Imgproc.cvtColor(yuv, rgb, Imgproc.COLOR_YUV2RGB_NV21, 3)
    Core.rotate(rgb, rgb, Core.ROTATE_90_CLOCKWISE)
    return rgb
}