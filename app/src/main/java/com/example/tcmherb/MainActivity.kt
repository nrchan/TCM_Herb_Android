package com.example.tcmherb

import android.Manifest
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraXConfig
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tcmherb.ui.theme.TCMHerbTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader
import java.io.File


class MainActivity : ComponentActivity(), CameraXConfig.Provider {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OpenCVLoader.initDebug()
        setContent {
            TCMHerbTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { MainScreen(navController) }
                        composable("camera") { CameraScreen(navController) }
                        composable(
                            "detail/{herbType}",
                            arguments = listOf(navArgument(name = "herbType") {
                                type = NavType.IntType
                            })
                        ) { entry ->
                            DetailScreen(navController, entry.arguments?.getInt("herbType"))
                        }
                    }
                }
            }
        }
    }

    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setAvailableCamerasLimiter(CameraSelector.DEFAULT_BACK_CAMERA)
            .build()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(navController: NavController){

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val context = LocalContext.current

    var openDialog by remember { mutableStateOf(false) }
    var resultType by remember { mutableStateOf(-1) }
    val cacheFile = File.createTempFile("testImage", null, context.cacheDir)

    val serverAgent = ServerAgent()
    val herbData = HerbData()

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true){
        coroutineScope.launch(Dispatchers.Default) { serverAgent.helloWorld() }
    }

    val deviceCameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()){
        if(it){
            resultType = -2
            coroutineScope.launch(Dispatchers.IO){
                val bitmap = BitmapFactory.decodeFile(cacheFile.path)
                val result = serverAgent.testImage(bitmap)
                Log.d("Connection result", "May be X${result[0].first}")
                resultType = result[0].first
            }
        }
    }

    val deviceStorageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){
        it?.let{
            resultType = -2
            coroutineScope.launch(Dispatchers.IO){
                val source: ImageDecoder.Source = ImageDecoder.createSource(context.contentResolver, it)
                val bitmap = ImageDecoder.decodeBitmap(source)
                val result = serverAgent.testImage(bitmap)
                Log.d("Connection result", "May be X${result[0].first}")
                resultType = result[0].first
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if(openDialog){
            AlertDialog(
                onDismissRequest = { openDialog = false },
                icon = { Image(painterResource(R.drawable.ic_round_mood_24), "", modifier = Modifier.size(64.dp), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)) },
                title = { Text(stringResource(R.string.main_dialog_title), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Justify) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.main_dialog_description), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                        Box(modifier = Modifier.height(120.dp), contentAlignment = Alignment.Center){
                            when(resultType){
                                -1 -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        TextButton(
                                            onClick = { deviceCameraLauncher.launch(FileProvider.getUriForFile(context, "com.example.tcmherb.fileprovider", cacheFile)) }
                                        ) { Text(stringResource(R.string.main_dialog_option_device_camera), style = MaterialTheme.typography.bodyMedium) }
                                        TextButton(
                                            onClick = { deviceStorageLauncher.launch(arrayOf("image/jpeg")) }
                                        ) { Text(stringResource(R.string.main_dialog_option_device_storage), style = MaterialTheme.typography.bodyMedium) }
                                    }
                                }
                                -2 -> Text(stringResource(R.string.main_dialog_text_wait))
                                else -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                                        Text(stringResource(R.string.main_dialog_result).format(herbData.nameZH(resultType)), style = MaterialTheme.typography.bodyLarge)
                                        Spacer(Modifier.height(8.dp))
                                        Button(
                                            onClick = { navController.navigate("detail/$resultType") },
                                            modifier = Modifier.wrapContentSize()
                                        ) { Text(stringResource(R.string.main_dialog_button_learn_more)) }
                                    }
                                }
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog = false }) { Text(stringResource(R.string.main_dialog_button_cancel)) }
                },
                confirmButton = {}
            )
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)){
            Column {
                //Explore
                Text(
                    stringResource(R.string.main_title_explore),
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    items(41){ index ->
                        HerbCard(navController,index+1)
                    }
                }
                Spacer(Modifier.height(36.dp))

                //Identify
                Text(
                    stringResource(R.string.main_title_identify),
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = {
                        if (!cameraPermissionState.status.isGranted){ cameraPermissionState.launchPermissionRequest() }
                        navController.navigate("camera")
                    },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Icon(painterResource(id = R.drawable.ic_round_photo_camera_24), "camera icon")
                    Spacer(
                        Modifier
                            .width(8.dp)
                            .height(48.dp))
                    Text(stringResource(R.string.main_button_camera_app), style = MaterialTheme.typography.bodyMedium)
                }
                TextButton(
                    onClick = { openDialog = true },
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                ) {
                    Text(stringResource(R.string.main_button_more))
                }

                //Decoration
                Spacer(Modifier.height(36.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Dot(MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Dot(MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(16.dp))
                    Dot(MaterialTheme.colorScheme.tertiary)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HerbCard(navController: NavController, herbType: Int){
    val herbData = HerbData()
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .wrapContentSize()
            .clickable {
                navController.navigate("detail/$herbType")
            }
    ){
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
        ){}
        Image(
            painterResource(LocalContext.current.resources.getIdentifier("x${herbType}r", "drawable", LocalContext.current.packageName)), "", contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(85.dp)
                .align(Alignment.TopStart)
                .offset((-8).dp, 4.dp)
        )
        Text(
            herbData.nameZHnl(herbType),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset((-4).dp, (-16).dp)
        )
    }
}

@Composable
fun Dot(color: Color){
    Surface(
        shape = RoundedCornerShape(45.dp),
        color = color,
        modifier = Modifier.size(16.dp)
    ){}
}