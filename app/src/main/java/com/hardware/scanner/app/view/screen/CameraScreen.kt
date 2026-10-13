package com.hardware.scanner.app.view.screen

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hardware.scanner.app.model.entity.Motherboard

@Composable
fun CameraScreen(
  onBackScreen: () -> Unit,
  onNavigateToSpecs: (Motherboard) -> Unit,
  viewModel: CameraViewModel = hiltViewModel()
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  var camera by remember { mutableStateOf<Camera?>(null) }
  var isTorchOn by remember { mutableStateOf(false) }

  val imageCapture = remember {
    ImageCapture.Builder()
      .setJpegQuality(80)
      .build()
  }

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      when (event) {
        is CameraEvent.NavigateToSpecs -> {
          onNavigateToSpecs(event.motherboard)
        }
        is CameraEvent.NotFound -> {
          snackbarHostState.showSnackbar("Placa-mãe não encontrada.")
        }
        is CameraEvent.UploadError -> {
          snackbarHostState.showSnackbar(event.message)
        }
      }
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    containerColor = Color.Black
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Camera Preview
      AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
          val previewView = PreviewView(ctx)
          val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

          cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            preview.surfaceProvider = previewView.surfaceProvider

            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
              lifecycleOwner,
              cameraSelector,
              preview,
              imageCapture
            )
          }, ContextCompat.getMainExecutor(ctx))

          previewView
        }
      )

      // Top Header Overlay Guide
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 24.dp),
        contentAlignment = Alignment.TopCenter
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Text(
            text = "Aponte para a placa-mãe",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
          )
        }
      }

      // Bottom Camera Control Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .background(Color.Black)
          .align(Alignment.BottomCenter),
        contentAlignment = Alignment.Center
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Lanterna / Torch Button
          IconButton(
            onClick = {
              isTorchOn = !isTorchOn
              camera?.cameraControl?.enableTorch(isTorchOn)
            },
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(if (isTorchOn) Color(0xFFFFD600) else Color.White.copy(alpha = 0.2f))
          ) {
            Icon(
              imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
              contentDescription = "Alternar Lanterna",
              tint = if (isTorchOn) Color.Black else Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          // Camera Shutter Button
          Box(
            modifier = Modifier
              .size(76.dp)
              .clip(CircleShape)
              .border(4.dp, Color.White, CircleShape)
              .padding(6.dp)
              .clip(CircleShape)
              .background(if (uiState.loading) Color.Gray else Color.White)
              .clickable(enabled = !uiState.loading) {
                viewModel.tirarFoto(
                  context = context,
                  imageCapture = imageCapture
                )
              },
            contentAlignment = Alignment.Center
          ) {
            if (uiState.loading) {
              CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Color.Black,
                strokeWidth = 3.dp
              )
            }
          }

          // Voltar / Back Button
          IconButton(
            onClick = onBackScreen,
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.2f))
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Voltar",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }

      // Full screen loading indicator overlay
      if (uiState.loading) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(Color.Black.copy(alpha = 0.8f))
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = "Analisando foto...",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }
  }
}