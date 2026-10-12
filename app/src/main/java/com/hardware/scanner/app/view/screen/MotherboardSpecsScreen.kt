package com.hardware.scanner.app.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val DarkButton = Color(0xFF1C1B1F)
private val PendingColor = Color(0xFF9A5B00)
private val CardBackground = Color(0xFFF1F1F4)

@Composable
fun MotherboardSpecsScreen(
  onBackScreen: () -> Unit,
  viewModel: MotherboardSpecsViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
    containerColor = Color.White,
    topBar = { PreviewTopBar(onBackScreen = onBackScreen, onShare = { /* TODO */ }) }
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp)
    ) {
      Text(
        text = "PLACA-MÃE",
        fontSize = 11.sp,
        color = Color.Gray
      )
      Text(
        text = "${uiState.brand} ${uiState.model}",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(Modifier.height(16.dp))

      //ImageCard(imageUrl = uiState.imageUrl)

      Spacer(Modifier.height(20.dp))

      SectionTitle("IDENTIFICAÇÃO")
      SpecRow("Modelo", uiState.model)
      SpecRow("Fabricante", uiState.brand)
      SpecRow("Chipset", uiState.chipset)
      SpecRow("Socket", uiState.socket)
      SpecRow("Formato", uiState.formFactor)
      SpecRow("Cor", uiState.color)

      Spacer(Modifier.height(16.dp))

      SectionTitle("MEMÓRIA")
      SpecRow("Canais de Comunicação", uiState.memoryChannels.toString())
      SpecRow("Tipo de memória", uiState.memoryType)
      SpecRow("Slots de memória", uiState.memorySlots.toString())
      SpecRow("Memória máxima", uiState.maxMemoryGb.let { "$it GB" })

      Spacer(Modifier.height(16.dp))

      SectionTitle("EXPANSÃO")
      SpecRow("Slots SATA", uiState.sataSlots.toString())
      SpecRow("Slots PCIe", uiState.pcieSlots.toString())

      Spacer(Modifier.height(24.dp))

      ActionButton(
        text = "Baixar Datasheet",
        icon = Icons.Default.Download,
        onClick = { /* TODO */ }
      )
      Spacer(Modifier.height(12.dp))
      ActionButton(
        text = "Lista de Processadores Suportados",
        icon = Icons.Default.Memory,
        onClick = { /* TODO */ }
      )

      Spacer(Modifier.height(24.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewTopBar(onBackScreen: () -> Unit, onShare: () -> Unit) {
  TopAppBar(
    title = {
      Text(
        text = "ESPECIFICAÇÕES TÉCNICAS",
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = Color.DarkGray
      )
    },
    navigationIcon = {
      IconButton(onClick = onBackScreen) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
      }
    },
    actions = {
      IconButton(
        onClick = onShare,
        modifier = Modifier
          .padding(end = 8.dp)
          .size(40.dp)
          .background(CardBackground, CircleShape)
      ) {
        Icon(
          Icons.Default.Share,
          contentDescription = "Compartilhar",
          modifier = Modifier.size(18.dp)
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ImageCard() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(CardBackground)
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // TODO: trocar por AsyncImage (Coil) com a foto da placa
      Box(
        modifier = Modifier
          .weight(1f)
          .aspectRatio(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFDADADF)),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
      }
      Box(
        modifier = Modifier
          .size(width = 56.dp, height = 56.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFFDADADF)),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
      }
    }

    Spacer(Modifier.height(12.dp))

    Text(
      text = "DESTAQUES ILUSTRATIVOS · NÃO CONFIRMADOS",
      fontSize = 9.sp,
      color = Color.Gray
    )

    Spacer(Modifier.height(8.dp))

    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FeatureChip("Alto Desempenho", Icons.Default.Settings, Color(0xFFE08A00))
      FeatureChip("Suporta Overclock", Icons.Default.Settings, Color(0xFFE08A00))
      FeatureChip("mSATA", Icons.Default.Storage, Color(0xFF1E6FD9))
      FeatureChip("Botão Power", Icons.Default.PowerSettingsNew, Color(0xFF2E9E4F))
      FeatureChip("Display POST", Icons.Default.Settings, Color(0xFF8E3FC9))
    }
  }
}

@Composable
private fun FeatureChip(text: String, icon: ImageVector, color: Color) {
  Row(
    modifier = Modifier
      .clip(RoundedCornerShape(50))
      .background(color.copy(alpha = 0.12f))
      .padding(horizontal = 10.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
    Spacer(Modifier.width(4.dp))
    Text(text, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
  }
}

@Composable
private fun SectionTitle(text: String) {
  Text(
    text = text,
    fontSize = 12.sp,
    fontWeight = FontWeight.Bold,
    color = Color.DarkGray,
    modifier = Modifier.padding(bottom = 4.dp)
  )
  HorizontalDivider(color = Color(0xFFE6E6EA))
  Spacer(Modifier.height(4.dp))
}

@Composable
private fun SpecRow(label: String, value: String?, pending: Boolean = false) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, fontSize = 13.sp, color = Color.DarkGray)
    if (value != null) {
      Text(
        text = value,
        fontSize = 13.sp,
        fontWeight = if (pending || value != "DDR3") FontWeight.Bold else FontWeight.Normal,
        color = if (pending) PendingColor else if (value == "DDR3") Color.Gray else Color.Black,
        textAlign = TextAlign.End
      )
    }
  }
}

@Composable
private fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
  Button(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .height(52.dp),
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = DarkButton,
      contentColor = Color.White
    )
  ) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
    Spacer(Modifier.width(8.dp))
    Text(text, fontWeight = FontWeight.SemiBold)
  }
}