package com.hardware.scanner.app.view.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.gestures.rememberTransformableState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hardware.scanner.app.model.entity.Cpu
import com.hardware.scanner.app.model.entity.Factor

private val DarkButton = Color(0xFF1C1B1F)
private val PendingColor = Color(0xFF9A5B00)
private val CardBackground = Color(0xFFF1F1F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotherboardSpecsScreen(
  onBackScreen: () -> Unit,
  viewModel: MotherboardSpecsViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  var showCpuListSheet by remember { mutableStateOf(false) }
  var showImageViewer by remember { mutableStateOf(false) }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      PreviewTopBar(
        onBackScreen = onBackScreen,
        onShare = {
          val shareText = """
            📋 Especificações Técnicas - Placa-Mãe

            • Marca: ${uiState.brand}
            • Modelo: ${uiState.model}
            • Socket: ${uiState.socket.ifBlank { "Não informado" }}
            • Formato: ${uiState.formFactor.ifBlank { "Não informado" }}
            • Cor: ${uiState.color.ifBlank { "Não informado" }}
            • Memória Máxima: ${uiState.maxMemoryGb} GB (${uiState.memoryType.ifBlank { "DDR" }})
            • Slots de Memória: ${uiState.memorySlots}

            Enviado via Hardware Scanner App
          """.trimIndent()

          val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
          }
          val shareIntent = Intent.createChooser(sendIntent, "Compartilhar especificações")
          context.startActivity(shareIntent)
        }
      )
    }
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
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "${uiState.brand} ${uiState.model}",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(Modifier.height(16.dp))

      AsyncImage(
        model = uiState.imageUrl,
        contentDescription = "Abrir imagem da placa-mãe",
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .size(200.dp)
          .clip(RoundedCornerShape(12.dp))
          .clickable(enabled = !uiState.imageUrl.isNullOrBlank()) {
            showImageViewer = true
          }
      )

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
      SpecRow("Canais de Comunicação", uiState.memoryChannels)
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
        onClick = {
          if (uiState.datasheetUrl.isNotBlank()) {
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.datasheetUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              }
              context.startActivity(intent)
            } catch (e: Exception) {
              Log.e("MotherboardSpecsScreen", "Erro ao abrir datasheet", e)
            }
          }
        }
      )
      Spacer(Modifier.height(12.dp))
      ActionButton(
        text = "Lista de Processadores Suportados",
        icon = Icons.Default.Memory,
        onClick = { showCpuListSheet = true }
      )

      Spacer(Modifier.height(24.dp))
    }
  }

  if (showCpuListSheet) {
    ModalBottomSheet(
      onDismissRequest = { showCpuListSheet = false },
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Text(
          text = "PROCESSADORES SUPORTADOS",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Socket: ${uiState.socket.ifBlank { "Não informado" }}",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.supportedCpus.isEmpty()) {
          Text(
            text = "Nenhum processador encontrado.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 16.dp)
          )
        } else {
          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
          ) {
            items(uiState.supportedCpus) { cpu ->
              CpuItemCard(cpu = cpu)
            }
          }
        }
      }
    }
  }

  if (showImageViewer) {
    ImageViewerDialog(
      imageUrl = uiState.imageUrl,
      onDismiss = { showImageViewer = false }
    )
  }
}

@Composable
private fun ImageViewerDialog(
  imageUrl: String?,
  onDismiss: () -> Unit
) {
  var scale by remember { mutableStateOf(1f) }
  var offset by remember { mutableStateOf(Offset.Zero) }
  val transformState = rememberTransformableState { zoomChange, panChange, _ ->
    scale = (scale * zoomChange).coerceIn(1f, 5f)
    offset += panChange
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.94f))
        .clickable { onDismiss() }
    ) {
      AsyncImage(
        model = imageUrl,
        contentDescription = "Imagem ampliada da placa-mãe",
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .fillMaxSize()
          .graphicsLayer {
            scaleX = scale
            scaleY = scale
            translationX = offset.x
            translationY = offset.y
          }
          .transformable(transformState)
          .clickable { }
      )

      IconButton(
        onClick = onDismiss,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(16.dp)
          .size(44.dp)
          .background(Color.Black.copy(alpha = 0.55f), CircleShape)
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Fechar imagem",
          tint = Color.White
        )
      }
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
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    },
    navigationIcon = {
      IconButton(onClick = onBackScreen) {
        Icon(
          Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Voltar",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }
    },
    actions = {
      IconButton(
        onClick = onShare,
        modifier = Modifier
          .padding(end = 8.dp)
          .size(40.dp)
          .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
      ) {
        Icon(
          Icons.Default.Share,
          contentDescription = "Compartilhar",
          modifier = Modifier.size(18.dp),
          tint = MaterialTheme.colorScheme.onSurface
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background,
      titleContentColor = MaterialTheme.colorScheme.onSurface,
      navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
      actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )
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
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(bottom = 4.dp)
  )
  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
    Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    if (value != null) {
      Text(
        text = value,
        fontSize = 13.sp,
        fontWeight = if (pending || value != "DDR3") FontWeight.Bold else FontWeight.Normal,
        color = if (pending) PendingColor else if (value == "DDR3") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
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
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    )
  ) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
    Spacer(Modifier.width(8.dp))
    Text(text, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
private fun CpuItemCard(cpu: Cpu) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
      contentColor = MaterialTheme.colorScheme.onSurface
    )
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = cpu.name,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        val badgeColor = if (cpu.factor == Factor.INTEL) Color(0xFF0068B5) else Color(0xFFED1C24)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = cpu.factor.name,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = "Socket: ${cpu.socket}",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = "${cpu.cores} Núcleos / ${cpu.threads} Threads",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}