package com.hardware.scanner.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.hardware.scanner.app.screen.CameraScreen
import com.hardware.scanner.app.viewmodels.CameraViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
  private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          CameraScreen()
        }
      }

    }
  }
}
