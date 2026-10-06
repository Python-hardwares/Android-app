package com.hardware.scanner.app.viewmodels

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardware.scanner.app.repositories.PhotoRepository
import com.hardware.scanner.app.services.PhotoService
import com.hardware.scanner.app.uistates.HardwareFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(
  private val repository: PhotoRepository,
  private val photoService: PhotoService
) : ViewModel() {

  fun tirarFoto(context: Context, imageCapture: ImageCapture) {
    val photoDir = File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
      "hardware-scanner"
    )
    photoDir.mkdirs()

    val photoFile = File.createTempFile(
      "hardware_scan_",
      ".jpg",
      photoDir
    )

    val outputOptions =
      ImageCapture.OutputFileOptions.Builder(photoFile)
        .build()

    imageCapture.takePicture(
      outputOptions,
      ContextCompat.getMainExecutor(context),

      object : ImageCapture.OnImageSavedCallback {

        override fun onImageSaved(
          outputFileResults: ImageCapture.OutputFileResults
        ) {
          Log.d(
            "Camera",
            "Foto salva: ${photoFile.absolutePath}"
          )
        }

        override fun onError(
          exception: ImageCaptureException
        ) {
          Log.e(
            "Camera",
            "Erro ao tirar foto",
            exception
          )
        }
      }
    )
  }
}