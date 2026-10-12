package com.hardware.scanner.app.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hardware.scanner.app.model.entity.Motherboard

@Composable
fun MenuScreen(
  onNavigateToPreview: () -> Unit,
  onNavigateToSpecs: (Motherboard) -> Unit,
  viewModel: MenuViewModel = hiltViewModel()
) {
  var input by remember { mutableStateOf("A785GM-M3") }
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      when (event) {
        is MenuEvent.NavigateToSpecs -> onNavigateToSpecs(event.motherboard)
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      OutlinedTextField(
        value = input,
        onValueChange = { input = it },
        label = { Text("Pesquisar placa-mãe") },
        placeholder = { Text("Ex.: A785GM-M3") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
          IconButton(onClick = { viewModel.searchMotherboardByName(input) }) {
            Icon(Icons.Default.Search, contentDescription = "Pesquisar")
          }
        }
      )

      Spacer(modifier = Modifier.height(8.dp))

      IconButton(onClick = onNavigateToPreview) {
        Icon(Icons.Default.Camera, contentDescription = "Abrir câmera")
      }

      if (uiState.isListing) {
        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(uiState.options) { motherboard ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToSpecs(motherboard) },
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(motherboard.brand, style = MaterialTheme.typography.labelMedium)
                Text(motherboard.model, style = MaterialTheme.typography.titleMedium)
                Text("Socket: ${motherboard.socket ?: "Não informado"}")
                Text("Formato: ${motherboard.formFactor ?: "Não informado"}")
              }
            }
          }
        }
      }
    }

    if (uiState.isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.5f))
          .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }
  }
}