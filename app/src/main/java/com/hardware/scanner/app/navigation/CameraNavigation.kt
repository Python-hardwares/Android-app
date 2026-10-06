package com.hardware.scanner.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hardware.scanner.app.view.screen.CameraScreen
import com.hardware.scanner.app.view.screen.PreviewScreen
import kotlinx.serialization.Serializable

@Serializable
object Camera

@Serializable
object Preview

fun NavGraphBuilder.cameraGraph(navController: NavController) {
  composable<Camera> {
    CameraScreen(onNavigateToPreview = { navController.navigate(Preview) })
  }

  composable<Preview> {
    PreviewScreen(onBackScreen = { navController.navigate(Camera) })
  }

}