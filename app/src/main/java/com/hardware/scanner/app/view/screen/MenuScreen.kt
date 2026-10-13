package com.hardware.scanner.app.view.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.hardware.scanner.app.model.entity.Motherboard

private val DarkButton = Color(0xFF1C1B1F)
private val CardBackground = Color(0xFFF1F1F4)
private val PrimaryAccent = Color(0xFF1E6FD9)

@Composable
fun MenuScreen(
  onNavigateToPreview: () -> Unit,
  onNavigateToSpecs: (Motherboard) -> Unit,
  viewModel: MenuViewModel = hiltViewModel()
) {
  var input by remember { mutableStateOf("") }
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      when (event) {
        is MenuEvent.NavigateToSpecs -> onNavigateToSpecs(event.motherboard)
        is MenuEvent.ShowError -> {
          snackbarHostState.showSnackbar(event.message)
        }
      }
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    containerColor = MaterialTheme.colorScheme.background
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 18.dp)
      ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Header Section
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Memory,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = "Hardware Scanner",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "Identificador de placas-mãe",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Camera Scanner Hero Card
        Card(
          onClick = onNavigateToPreview,
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "ESCANEAR COM A CÂMERA",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Escanear Placa-Mãe",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tire foto para identificar o modelo automaticamente",
                fontSize = 12.sp,
                color = Color.White
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Câmera",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Section Title
        Text(
          text = "PESQUISA MANUAL",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search Field
        OutlinedTextField(
          value = input,
          onValueChange = { input = it },
          placeholder = { Text("Digite o modelo (ex.: A785GM-M3)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Buscar",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          trailingIcon = {
            if (input.isNotEmpty()) {
              IconButton(onClick = { input = "" }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Limpar",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            } else {
              IconButton(
                onClick = {
                  if (input.isNotBlank()) {
                    viewModel.searchMotherboardByName(input)
                  }
                }
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = "Pesquisar",
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
          },
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(
            onSearch = {
              if (input.isNotBlank()) {
                viewModel.searchMotherboardByName(input)
              }
            }
          ),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Suggestions
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val suggestions = listOf("A785GM-M3", "B450M", "Z690", "H61M")
          items(suggestions) { item ->
            AssistChip(
              onClick = {
                input = item
                viewModel.searchMotherboardByName(item)
              },
              label = { Text(item, fontSize = 12.sp) },
              colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
              ),
              shape = RoundedCornerShape(50)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results Section
        if (uiState.isListing) {
          Text(
            text = "RESULTADOS ENCONTRADOS (${uiState.options.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(8.dp))

          LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(uiState.options) { motherboard ->
              MotherboardItemCard(
                motherboard = motherboard,
                onClick = { onNavigateToSpecs(motherboard) }
              )
            }
          }
        }
      }

      // Loading Overlay
      AnimatedVisibility(
        visible = uiState.isLoading,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) {},
          contentAlignment = Alignment.Center
        ) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              CircularProgressIndicator(color = DarkButton)
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Buscando placa-mãe...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MotherboardItemCard(
  motherboard: Motherboard,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (!motherboard.imageUrl.isNullOrBlank()) {
        AsyncImage(
          model = motherboard.imageUrl,
          contentDescription = motherboard.model,
          modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
        )
        Spacer(modifier = Modifier.width(12.dp))
      } else {
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFDADADF)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Memory,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = motherboard.brand.uppercase(),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = PrimaryAccent
        )
        Text(
          text = motherboard.model,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Socket: ${motherboard.socket ?: "N/I"}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Formato: ${motherboard.formFactor ?: "N/I"}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "Ver detalhes",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}