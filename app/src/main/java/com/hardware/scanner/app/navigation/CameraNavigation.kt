package com.hardware.scanner.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hardware.scanner.app.view.screen.CameraScreen
import com.hardware.scanner.app.view.screen.MenuScreen
import com.hardware.scanner.app.view.screen.MotherboardSpecs
import com.hardware.scanner.app.view.screen.MotherboardSpecsScreen
import com.hardware.scanner.app.view.screen.toRoute
import kotlinx.serialization.Serializable

@Serializable
internal data object Menu

@Serializable
internal data object Preview

fun NavGraphBuilder.cameraGraph(navController: NavController) {
  composable<Menu> {
    MenuScreen(
      onNavigateToPreview = { navController.navigate(Preview) },
      onNavigateToSpecs = { motherboard ->
        navController.navigate(motherboard.toRoute())
      }
    )
  }

  composable<Preview> {
    CameraScreen(
      onBackScreen = { navController.popBackStack() },
      onNavigateToSpecs = { motherboard ->
        navController.navigate(motherboard.toRoute()) {
          popUpTo<Preview> { inclusive = true }
        }
      }
    )
  }

  composable<MotherboardSpecs> {
    MotherboardSpecsScreen(onBackScreen = { navController.popBackStack() })
  }
}