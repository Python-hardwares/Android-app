package com.hardware.scanner.app.view.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardware.scanner.app.model.repositories.ApiRepository
import com.hardware.scanner.app.model.repositories.ServerConfigStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServerConnectionUiState(
  val serverUrl: String = "",
  val checking: Boolean = false,
  val connected: Boolean = false,
  val errorMessage: String? = null
)

@HiltViewModel
class ServerConnectionViewModel @Inject constructor(
  private val apiRepository: ApiRepository,
  private val serverConfigStore: ServerConfigStore
) : ViewModel() {
  private val _uiState = MutableStateFlow(
    ServerConnectionUiState(serverUrl = serverConfigStore.getServerUrl().orEmpty())
  )
  val uiState: StateFlow<ServerConnectionUiState> = _uiState.asStateFlow()

  init {
    serverConfigStore.getServerUrl()?.let(::checkServer)
  }

  fun updateServerUrl(url: String) {
    _uiState.value = _uiState.value.copy(serverUrl = url, errorMessage = null)
  }

  fun checkServer() {
    checkServer(_uiState.value.serverUrl)
  }

  private fun checkServer(url: String) {
    val normalizedUrl = url.trim()
    if (normalizedUrl.isBlank()) {
      _uiState.value = _uiState.value.copy(
        checking = false,
        connected = false,
        errorMessage = "Informe o IP ou hostname do servidor."
      )
      return
    }

    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(
        serverUrl = normalizedUrl,
        checking = true,
        connected = false,
        errorMessage = null
      )
      try {
        if (apiRepository.checkConnection(normalizedUrl)) {
          serverConfigStore.saveServerUrl(normalizedUrl)
          _uiState.value = _uiState.value.copy(checking = false, connected = true)
        } else {
          showConnectionError()
        }
      } catch (_: Exception) {
        showConnectionError()
      }
    }
  }

  private fun showConnectionError() {
    _uiState.value = _uiState.value.copy(
      checking = false,
      connected = false,
      errorMessage = "Não foi possível acessar o servidor. Verifique o IP/hostname e a porta."
    )
  }
}
