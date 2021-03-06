package com.example.tcmherb

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.media.Image
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfDouble
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min
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
                    Text(
                        stringResource(R.string.camera_alert_image_blur),
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
    androidx.compose.material.ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CameraView(navController: NavController, showBlurWarning: (Boolean) -> Unit){
    Modifier.fillMaxSize()

    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val coroutineScope = rememberCoroutineScope()

    val herbData = HerbData()

    val cameraProviderFuture = remember(context){ ProcessCameraProvider.getInstance(context) }
    var cameraProvider: ProcessCameraProvider
    var camera: Camera? by remember { mutableStateOf(null) }
    var cameraSelector: CameraSelector? by remember { mutableStateOf(null) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var imageAnalyzer: ImageAnalysis? by remember { mutableStateOf(null) }
    val executor = remember(context) { ContextCompat.getMainExecutor(context) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    var cameraControl : CameraControl? by remember { mutableStateOf(null) }
    var cameraInfo : CameraInfo? by remember { mutableStateOf(null) }
    var cameraScale by remember { mutableStateOf(1f) }

    var isSaved by remember { mutableStateOf(false) }
    var resultType by remember { mutableStateOf(-1) }

    var isTorchOn by remember { mutableStateOf(false) }

    val serverAgent = ServerAgent()

    if(cameraPermissionState.status.isGranted){
        val state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, confirmStateChange = {
            if(it == ModalBottomSheetValue.Hidden){
                isSaved = false
            }
            true
        })

        //If permission granted
        ModalBottomSheetLayout(
            sheetBackgroundColor = MaterialTheme.colorScheme.background,
            sheetShape = RoundedCornerShape(topStart = 45.dp, topEnd = 45.dp),
            sheetState = state,
            sheetContent = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth()
                ) {
                    Text(stringResource(R.string.camera_bottomsheet_lookslike), style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        herbData.nameZH(resultType),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Clip,
                        maxLines = 1,
                        modifier = Modifier
                            .placeholder(
                                visible = resultType==-1,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(24.dp),
                                highlight = PlaceholderHighlight.shimmer(),
                                placeholderFadeTransitionSpec = { TweenSpec(300) },
                                contentFadeTransitionSpec = { TweenSpec(300) }
                            )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        coroutineScope.launch(Dispatchers.Default) { state.hide() }
                        isSaved = false
                        navController.navigate("detail/$resultType")
                    }, enabled = isSaved) { Text(stringResource(R.string.camera_bottomsheet_learn)) }
                    Button(onClick = {
                        coroutineScope.launch(Dispatchers.Default) { state.hide() }
                        isSaved = false
                    }) { Text(stringResource(R.string.camera_bottomsheet_again)) }
                }
            }
        ){
            Surface(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxSize()
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)

                        cameraProviderFuture.addListener({
                            cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                .build()

                            imageCapture = ImageCapture.Builder()
                                .build()

                            imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            imageAnalyzer!!.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                                if(!state.isVisible){
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
                                        //Log.d("Blur", "$blur $isSaved")
                                        showBlurWarning(blur <= 60 && !isSaved)

                                    }
                                }

                                imageProxy.close()
                            }

                            cameraProvider = ProcessCameraProvider.getInstance(context).get()
                            cameraProvider.unbindAll()
                            camera = cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector!!, preview, imageCapture, imageAnalyzer)

                            cameraControl = camera?.cameraControl
                            cameraInfo = camera?.cameraInfo

                            coroutineScope.launch(Dispatchers.Default) { serverAgent.helloWorld() }
                        }, executor)

                        preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        previewView.setOnTouchListener { view, motionEvent ->
                            view.performClick()
                            val meteringPoint = previewView.meteringPointFactory
                                .createPoint(motionEvent.x, motionEvent.y)
                            val action = FocusMeteringAction.Builder(meteringPoint).build()
                            camera?.cameraControl?.startFocusAndMetering(action)
                            true
                        }

                        previewView
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, zoom, _ ->
                                cameraInfo?.let {
                                    cameraScale *= zoom
                                    cameraScale =
                                        min(cameraScale, it.zoomState.value!!.maxZoomRatio)
                                    cameraScale =
                                        max(cameraScale, it.zoomState.value!!.minZoomRatio)
                                    cameraControl?.setZoomRatio(cameraScale)
                                }
                            }
                        }
                )
                //shutter
                Box(contentAlignment = Alignment.BottomCenter){
                    Button(
                        onClick = {
                            imageCapture?.takePicture(ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                        super.onCaptureSuccess(imageProxy)
                                        imageProxy.image?.let { image ->
                                            coroutineScope.launch(Dispatchers.Default) { state.show() }
                                            resultType = -1
                                            val bitmap = image.toBitmap()

                                            coroutineScope.launch(Dispatchers.IO) {
                                                val result = serverAgent.testImage(bitmap)
                                                Log.d("Connection result", "May be X${result[0].first}")
                                                resultType = result[0].first
                                                isSaved = true
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
                //torch
                if(camera?.cameraInfo?.hasFlashUnit() == true) Box(
                    contentAlignment = Alignment.BottomStart,
                    modifier = Modifier.padding(start = 48.dp, bottom = 40.dp)
                ){
                    Surface(
                        onClick = {
                            camera?.let {
                                isTorchOn = !isTorchOn
                                it.cameraControl.enableTorch(isTorchOn)
                            }
                        },
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(45.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        if(isTorchOn) Image(painterResource(R.drawable.ic_round_highlight_24), "torch on icon", modifier = Modifier.size(12.dp), contentScale = ContentScale.Inside, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer), alpha = 0.7f)
                        else Image(painterResource(id = R.drawable.ic_round_highlight_off_24), "torch off icon", modifier = Modifier.size(12.dp), contentScale = ContentScale.Inside, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer), alpha = 0.7f)
                    }
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
            Text(stringResource(R.string.camera_permission_title), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.camera_permission_description), textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                context.startActivity(intent)
            }) {
                Text(stringResource(R.string.camera_permission_button), style = MaterialTheme.typography.bodyMedium)
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