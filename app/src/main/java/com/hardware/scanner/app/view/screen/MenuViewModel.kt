package com.hardware.scanner.app.view.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardware.scanner.app.model.entity.Motherboard
import com.hardware.scanner.app.model.entity.Scan
import com.hardware.scanner.app.model.entity.ScanStatus
import com.hardware.scanner.app.model.entity.isMotherboard
import com.hardware.scanner.app.model.repositories.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class MenuUiState(
  val id: String = "",
  val brand: String = "",
  val model: String = "",
  val imageUrl: String = "",
  val chipset: String = "",
  val socket: String = "",
  val formFactor: String = "",
  val color: String = "",
  val memoryType: String = "",
  val memoryChannels: String = "",
  val maxMemoryGb: Int = 0,
  val memorySlots: Int = 0,
  val sataSlots: Int = 0,
  val pcieSlots: Int = 0,
  val isLoading: Boolean = false,
  val isListing: Boolean = false,
  val options: List<Motherboard> = emptyList()
)

sealed interface MenuEvent {
  data class NavigateToSpecs(val motherboard: Motherboard) : MenuEvent
  data class ShowError(val message: String) : MenuEvent
}

@HiltViewModel
class MenuViewModel @Inject constructor(
  private val repository: ApiRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(MenuUiState())
  val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

  private val _events = Channel<MenuEvent>(Channel.BUFFERED)
  val events = _events.receiveAsFlow()

  fun searchMotherboardByName(name: String) {
    if (name.isBlank() || _uiState.value.isLoading) return
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true, isListing = false, options = emptyList()) }
      try {
        val scan: Scan = repository.searchMotherboardByName(name.trim())
        val motherboardMatches = scan.matches.filter(Motherboard::isMotherboard)

        when (scan.status) {
          ScanStatus.NOT_FOUND -> {
            Log.w(
              "PreviewViewModel", "Nenhuma placa-mãe correspondente foi encontrada."
            )
            _uiState.update { it.copy(isLoading = false) }
            _events.send(MenuEvent.ShowError("Nenhuma placa-mãe encontrada."))
          }

          ScanStatus.SUGGESTIONS -> {
            Log.i(
              "PreviewViewModel", "Foram encontradas sugestões de placas-mãe."
            )
            if (motherboardMatches.isEmpty()) {
              _events.send(MenuEvent.ShowError("Nenhuma placa-mãe encontrada."))
            }
            _uiState.update {
              it.copy(
                isLoading = false,
                isListing = motherboardMatches.isNotEmpty(),
                options = motherboardMatches
              )
            }
          }

          ScanStatus.MATCHED -> {
            Log.i(
              "PreviewViewModel", "Placa-mãe correspondente encontrada; exibindo resultado para seleção."
            )
            if (motherboardMatches.isEmpty()) {
              _events.send(MenuEvent.ShowError("Nenhuma placa-mãe encontrada."))
            }
            _uiState.update {
              it.copy(
                isLoading = false,
                isListing = motherboardMatches.isNotEmpty(),
                options = motherboardMatches
              )
            }
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e("CameraViewModel", "Erro ao buscar", e)
        _events.send(MenuEvent.ShowError("Erro de conexão ao buscar placa-mãe."))
      } finally {
        _uiState.update { it.copy(isLoading = false) }
      }
    }
  }
}