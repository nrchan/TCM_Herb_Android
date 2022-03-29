package com.example.tcmherb

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val serverAgent = ServerAgent()


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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