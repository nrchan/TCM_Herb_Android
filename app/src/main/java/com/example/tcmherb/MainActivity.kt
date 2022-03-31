package com.example.tcmherb

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
        Box(modifier = Modifier.align(Alignment.BottomCenter)){
            Column() {
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
                    onClick = { /*TODO*/ },
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