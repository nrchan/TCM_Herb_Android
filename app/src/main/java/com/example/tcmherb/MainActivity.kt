package com.example.tcmherb

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {
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
                        ) { entry -> DetailScreen(navController, entry.arguments?.getInt("herbType")) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(navController: NavController){

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HerbCard(herbType = 2)
        Button(
            onClick = {
                if (!cameraPermissionState.status.isGranted){ cameraPermissionState.launchPermissionRequest() }
                navController.navigate("camera")
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Text("Identify", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun HerbCard(herbType: Int){
    val herbData = HerbData()
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.wrapContentSize()
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
            modifier = Modifier.size(80.dp).align(Alignment.TopStart).offset((-8).dp)
        )
        Text(
            herbData.nameZHnl(herbType),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.align(Alignment.BottomEnd).offset(0.dp, (-16).dp)
        )
    }
}