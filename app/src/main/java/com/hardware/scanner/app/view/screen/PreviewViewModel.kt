package com.hardware.scanner.app.view.screen

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardware.scanner.app.model.repositories.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class PreviewUiState(
  val loading: Boolean = false,
  val uploadFinished: Boolean = false,
  val error: String? = null
)

@HiltViewModel
class PreviewViewModel @Inject constructor(
  private val repository: PhotoRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(PreviewUiState())
  val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

  fun tirarFoto(
    context: Context,
    imageCapture: ImageCapture
  ) {
    if (_uiState.value.loading) return

    _uiState.update {
      it.copy(
        loading = true,
        uploadFinished = false,
        error = null
      )
    }

    val photoDir = File(
      Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
      ),
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

          enviarFoto(photoFile)
        }

        override fun onError(
          exception: ImageCaptureException
        ) {
          Log.e(
            "Camera",
            "Erro ao tirar foto",
            exception
          )

          _uiState.update {
            it.copy(
              loading = false,
              error = exception.message
            )
          }
        }
      }
    )
  }

  private fun enviarFoto(file: File) {
    viewModelScope.launch {
      try {
        Log.d("Upload", "Iniciando upload: ${file.absolutePath}")

        val msg = repository.uploadPhoto(file)

        Log.i("Upload", "Upload concluído: $msg")

        _uiState.update {
          it.copy(
            loading = false,
            uploadFinished = true
          )
        }

      } catch (e: Exception) {
        Log.e(
          "Upload",
          "Erro ao enviar foto",
          e
        )

        _uiState.update {
          it.copy(
            loading = false,
            error = e.message
          )
        }
      }
    }
  }
}