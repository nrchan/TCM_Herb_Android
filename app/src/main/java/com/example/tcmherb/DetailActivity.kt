package com.example.tcmherb

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.SystemUiController

@Composable
fun DetailScreen(navController: NavController, herbType: Int?) {
    val herbData = HerbData()
    val index = if(herbType!! > 0) herbType else 0

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        FilledTonalButton(
            onClick = { navController.navigate("home"){ popUpTo("home") {inclusive = true} } },
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(Icons.Rounded.ArrowBack, "back button")
        }
        Box() {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Column(
                    Modifier.padding(start = 24.dp, top = 36.dp)
                ){
                    Spacer(Modifier.height(24.dp))
                    Text(herbData.nameZHnl(index), style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(herbData.nameEN(index), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(Modifier.height(24.dp))
                }
            }
            Image(
                painterResource(LocalContext.current.resources.getIdentifier("x${herbType}r", "drawable", LocalContext.current.packageName)), "", contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .offset(16.dp, (-44).dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondaryContainer)
            )
            Image(
                painterResource(LocalContext.current.resources.getIdentifier("x${herbType}r", "drawable", LocalContext.current.packageName)), "", contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.TopEnd)
                    .offset(8.dp, (-36).dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.detail_title_scientific_name), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Text(herbData.nameSC(index), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.detail_title_taste), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Text(herbData.taste(index), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.detail_title_feature), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Text(herbData.feature(index), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
        }
    }

    BackHandler {
        navController.navigate("home") { popUpTo("home") {inclusive = true} }
    }
}