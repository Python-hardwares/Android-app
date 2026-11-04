package com.hardware.scanner.app.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ServerConnectionScreen(
  viewModel: ServerConnectionViewModel = hiltViewModel()
) {
  val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Icon(
      imageVector = Icons.Default.CloudOff,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary
    )
    Text(
      text = if (uiState.errorMessage == null) "Conectar ao servidor" else "Servidor indisponível",
      style = MaterialTheme.typography.headlineSmall,
      modifier = Modifier.padding(top = 16.dp)
    )
    Text(
      text = uiState.errorMessage
        ?: "Informe o IP ou hostname do servidor auto-hospedado, incluindo a porta.",
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
    )
    OutlinedTextField(
      value = uiState.serverUrl,
      onValueChange = viewModel::updateServerUrl,
      label = { Text("IP ou hostname:porta") },
      placeholder = { Text("192.168.1.10:9090") },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
      modifier = Modifier.fillMaxWidth()
    )
    Button(
      onClick = viewModel::checkServer,
      enabled = !uiState.checking,
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
    ) {
      if (uiState.checking) {
        CircularProgressIndicator(
          modifier = Modifier.padding(2.dp),
          color = MaterialTheme.colorScheme.onPrimary
        )
      } else {
        Text("Conectar")
      }
    }
  }
}
