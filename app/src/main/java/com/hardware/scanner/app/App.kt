package com.hardware.scanner.app

import android.util.Log
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hardware.scanner.app.navigation.Menu
import com.hardware.scanner.app.navigation.cameraGraph

val LocalBackNavigation = staticCompositionLocalOf<() -> Unit> {
  error("No Back Navigation provided")
}

@Composable
fun App() {
  val navController = rememberNavController()

  CompositionLocalProvider(LocalBackNavigation provides { navController.popBackStack() }) {
    Surface {
      AppNavHost(navController)
    }
  }
}


@Composable
private fun AppNavHost(navController: NavHostController) {
  LaunchedEffect(navController) {
    navController.addOnDestinationChangedListener { _, destination, _ ->
      Log.d("Navigation", "navigation to ${destination.route}")
    }
  }

  NavHost(navController = navController, startDestination = Menu) {
    cameraGraph(navController)
  }
}