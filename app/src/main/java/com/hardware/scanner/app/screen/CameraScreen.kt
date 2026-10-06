package com.hardware.scanner.app.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import kotlinx.coroutines.delay

@Composable
fun CameraScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val imageCapture = remember {
    ImageCapture.Builder().build()
  }

  var onCamera by remember {
    mutableStateOf(false)
  }

  var processando by remember {
    mutableStateOf(false)
  }

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
      hasCameraPermission = granted

      if (granted) {
        onCamera = true
      }
    }

  Column(
    modifier = modifier.fillMaxSize()
  ) {

    if (onCamera && hasCameraPermission) {

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
          onClick = {
            processando = true

            tirarFoto(
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

        if (processando) {

          LaunchedEffect(processando) {
            delay(3000)

            processando = false
            onCamera = false
          }

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

    } else {

      IconButton(
        onClick = {

          if (
            ContextCompat.checkSelfPermission(
              context,
              Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
          ) {
            hasCameraPermission = true
            onCamera = true
          } else {
            permissionLauncher.launch(
              Manifest.permission.CAMERA
            )
          }
        }
      ) {
        Icon(
          imageVector = Icons.Default.Camera,
          contentDescription = "Abrir câmera"
        )
      }
    }
  }
}

