package com.hardware.scanner.app.view.screen

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardware.scanner.app.model.entity.Motherboard
import com.hardware.scanner.app.model.entity.ScanStatus
import com.hardware.scanner.app.model.repositories.ApiRepository
import com.hardware.scanner.app.services.PhotoService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.File

data class CameraUiState(
  val loading: Boolean = false,
  val uploadFinished: Boolean = false,
  val error: String? = null
)

sealed interface CameraEvent {
  data class NavigateToSpecs(val motherboard: Motherboard) : CameraEvent
  data object NotFound : CameraEvent
  data class UploadError(val message: String) : CameraEvent
}

@HiltViewModel
class CameraViewModel @Inject constructor(
  private val repository: ApiRepository,
  private val photoService: PhotoService
) : ViewModel() {

  private val _uiState = MutableStateFlow(CameraUiState())
  val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

  private val _events = Channel<CameraEvent>(Channel.BUFFERED)
  val events = _events.receiveAsFlow()

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
    val photoFile = photoService.createPhotoFile(directoryPhoto)
    val outputOptions = photoService.createOutputOptions(photoFile)

    imageCapture.takePicture(
      outputOptions,
      ContextCompat.getMainExecutor(context),
      object : ImageCapture.OnImageSavedCallback {

        override fun onImageSaved(
          outputFileResults: ImageCapture.OutputFileResults
        ) {
          val savedFile = photoFile
          Log.d(
            "Camera",
            "Foto salva com sucesso: ${savedFile.absolutePath}, existe=${savedFile.exists()}, tamanho=${savedFile.length()}"
          )

          enviarFoto(savedFile)
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
          viewModelScope.launch {
            _events.send(CameraEvent.UploadError("Não foi possível tirar a foto. Tente novamente."))
          }
        }
      }
    )
  }

  private fun enviarFoto(file: File) {
    viewModelScope.launch {
      try {
        Log.d("Upload", "Iniciando upload: ${file.absolutePath}")
        val scan = repository.searchMotherboardByPhoto(file)
        Log.i("Upload", "Upload concluído: $scan")

        val matchedMotherboard = scan.matches.firstOrNull()

        if (matchedMotherboard != null && (scan.status == ScanStatus.MATCHED || scan.status == ScanStatus.SUGGESTIONS)) {
          _events.send(CameraEvent.NavigateToSpecs(matchedMotherboard))
        } else {
          _events.send(CameraEvent.NotFound)
        }

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
        val message = if (e is IOException && e.message?.contains("unexpected end of stream") == true) {
          "O servidor encerrou a conexão ao processar a foto. Verifique o servidor e tente novamente."
        } else {
          "Não foi possível enviar a foto. Tente novamente."
        }
        _events.send(CameraEvent.UploadError(message))
      }
    }
  }
}