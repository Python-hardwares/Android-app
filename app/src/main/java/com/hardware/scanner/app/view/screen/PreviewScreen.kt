package com.hardware.scanner.app.view.screen

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PreviewScreen(
  onBackScreen: () -> Unit,
  viewModel: PreviewViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val imageCapture = remember {
    ImageCapture.Builder().build()
  }

  LaunchedEffect(uiState.uploadFinished) {
    if (uiState.uploadFinished) {
      onBackScreen()
    }
  }

  Box(
    modifier = Modifier.fillMaxSize()
  ) {

    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { context ->

        val previewView = PreviewView(context)

        val cameraProviderFuture =
          ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

          val cameraProvider =
            cameraProviderFuture.get()

          val preview =
            Preview.Builder().build()

          val cameraSelector =
            CameraSelector.DEFAULT_BACK_CAMERA

          preview.surfaceProvider =
            previewView.surfaceProvider

          cameraProvider.unbindAll()

          cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
          )

        }, ContextCompat.getMainExecutor(context))

        previewView
      }
    )

    IconButton(
      enabled = !uiState.loading,
      onClick = {
        viewModel.tirarFoto(
          context = context,
          imageCapture = imageCapture
        )
      },
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 32.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Camera,
        contentDescription = "Tirar foto"
      )
    }

    if (uiState.loading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Color.Black.copy(alpha = 0.5f)
          ),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }
  }
}