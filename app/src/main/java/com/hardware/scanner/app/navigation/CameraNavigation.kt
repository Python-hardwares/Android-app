package com.hardware.scanner.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.hardware.scanner.app.model.entity.Motherboard
import com.hardware.scanner.app.view.screen.CameraScreen
import com.hardware.scanner.app.view.screen.MenuScreen
import com.hardware.scanner.app.view.screen.MotherboardSpecsScreen
import com.hardware.scanner.app.view.screen.toRoute
import kotlinx.serialization.Serializable

@Serializable
internal data object Menu

@Serializable
internal data object Preview

@Serializable
internal data class MotherboardSpecs(val motherboard: Motherboard)

fun NavGraphBuilder.cameraGraph(navController: NavController) {
  composable<Menu> { backStackEntry ->
    //TODO precisa arruma o envio do objeto para a tela
    val motherboard = backStackEntry.toRoute<Motherboard>()

    MenuScreen(
      onNavigateToPreview = { navController.navigate(Preview) },
      onNavigateToSpecs = { motherboard ->
        navController.navigate(motherboard.toRoute())
      }
    )
  }

  composable<Preview> {
    CameraScreen(onBackScreen = { navController.popBackStack() })
  }

  composable<MotherboardSpecs> {
    MotherboardSpecsScreen(onBackScreen = { navController.popBackStack() })
  }
}