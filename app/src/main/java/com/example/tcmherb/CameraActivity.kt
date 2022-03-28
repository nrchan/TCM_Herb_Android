package com.example.tcmherb

import android.Manifest
import android.R.attr.x
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.media.Image
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfDouble
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.math.pow


@Composable
fun CameraScreen(navController: NavController){
    var showBlurWarning by remember { mutableStateOf(false) }

    Box (
        modifier = Modifier.fillMaxSize()
    ) {
        CameraView(navController) { showBlurWarning = it }
        Row(modifier = Modifier.padding(32.dp)){
            FilledTonalButton(
                onClick = { navController.navigate("home"){ popUpTo("home") {inclusive = true} } },
            ) {
                Icon(Icons.Rounded.ArrowBack, "back button")
            }
            AnimatedVisibility(
                visible = showBlurWarning,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FilledTonalButton(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    onClick = {},
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text("\uD83E\uDD75 Image is blurry!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@OptIn(
    ExperimentalPermissionsApi::class,
    androidx.compose.animation.ExperimentalAnimationApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class
)
@Composable
fun CameraView(navController: NavController, showBlurWarning: (Boolean) -> Unit){
    Modifier.fillMaxSize()

    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val coroutineScope = rememberCoroutineScope()

    var isSaved by remember { mutableStateOf(false) }
    var resultType by remember { mutableStateOf(-1) }

    val serverAgent = ServerAgent()

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    val imageCapture = ImageCapture.Builder()
        .build()

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

            //the higher the number, the clearer the photo
            val blur = (std[0, 0][0]).pow(2)
            showBlurWarning(blur <= 60 && !isSaved)
        }

        imageProxy.close()
    }

    LaunchedEffect(true){
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, preview, imageCapture, imageBlurryAnalysis)
        preview.setSurfaceProvider(previewView.surfaceProvider)

        launch(Dispatchers.IO) {
            serverAgent.helloWorld()
        }
    }

    if(cameraPermissionState.status.isGranted){
        val state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        //If permission granted
        ModalBottomSheetLayout(
            sheetState = state,
            sheetContent = {
                Text(resultType.toString())
            }
        ){
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
                //shutter
                Box(contentAlignment = Alignment.BottomCenter){
                    Button(
                        onClick = {
                            imageCapture.takePicture(ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                        super.onCaptureSuccess(imageProxy)
                                        imageProxy.image?.let { image ->
                                            var bitmap = image.toBitmap()
                                            val matrix = Matrix()
                                            matrix.postRotate(90f)
                                            bitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.width, bitmap.height, matrix, true)

                                            coroutineScope.launch(Dispatchers.IO) {
                                                val result = serverAgent.testImage(bitmap)
                                                Log.d("Connection result", "May be X${result[0].first}")
                                                resultType = result[0].first
                                                isSaved = true
                                                coroutineScope.launch(Dispatchers.Default) { state.show() }
                                            }
                                        }
                                        imageProxy.close()
                                    }
                                })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
                        border = BorderStroke(3.dp, color = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(45.dp),
                        modifier = Modifier
                            .padding(32.dp)
                            .wrapContentSize()
                            .size(56.dp)
                    ){}
                }
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

fun Image.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}