package com.hardware.scanner.app.view.screen

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardware.scanner.app.model.repositories.ApiRepository
import com.hardware.scanner.app.services.PhotoService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class CameraUiState(
  val loading: Boolean = false,
  val uploadFinished: Boolean = false,
  val error: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
  private val repository: ApiRepository,
  private val photoService: PhotoService
) : ViewModel() {

  private val _uiState = MutableStateFlow(CameraUiState())
  val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

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
    val directoryPhoto = photoService.createDirectory("hardware-scanner")
    val outputOptions = photoService.createOutputOptions(directoryPhoto)

    imageCapture.takePicture(
      outputOptions,
      ContextCompat.getMainExecutor(context),
      object : ImageCapture.OnImageSavedCallback {

        override fun onImageSaved(
          outputFileResults: ImageCapture.OutputFileResults
        ) {
          Log.d(
            "Camera",
            "Foto salva: ${directoryPhoto.absolutePath}"
          )

          enviarFoto(directoryPhoto)
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
        val msg = repository.searchMotherboardByPhoto(file)
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