package com.hardware.scanner.app.view.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.hardware.scanner.app.model.entity.Cpu
import com.hardware.scanner.app.model.entity.Factor

private const val NO_DATA_TEXT = "Não informado na base"

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
          fun formatShareValue(value: String?, unit: String? = null): String {
            return if (value.isNullOrBlank() || value == "0") NO_DATA_TEXT
            else if (unit != null) "$value $unit" else value
          }

          val shareText = """
            📋 Especificações Técnicas - Placa-Mãe

            • Marca: ${formatShareValue(uiState.brand)}
            • Modelo: ${formatShareValue(uiState.model)}
            • Socket: ${formatShareValue(uiState.socket)}
            • Formato: ${formatShareValue(uiState.formFactor)}
            • Cor: ${formatShareValue(uiState.color)}
            • Memória Máxima: ${formatShareValue(if (uiState.maxMemoryGb > 0) uiState.maxMemoryGb.toString() else null, "GB")} (${formatShareValue(uiState.memoryType)})
            • Slots de Memória: ${formatShareValue(if (uiState.memorySlots > 0) uiState.memorySlots.toString() else null)}

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

      val titleText = remember(uiState.brand, uiState.model) {
        listOf(uiState.brand, uiState.model)
          .filter { it.isNotBlank() }
          .joinToString(" ")
          .ifBlank { "Placa-mãe sem identificação" }
      }

      Text(
        text = titleText,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(Modifier.height(16.dp))

      if (uiState.imageUrl.isNotBlank()) {
        AsyncImage(
          model = uiState.imageUrl,
          contentDescription = "Imagem da placa-mãe",
          contentScale = ContentScale.Fit,
          modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { showImageViewer = true }
        )
      } else {
        Box(
          modifier = Modifier
            .size(width = 200.dp, height = 130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Image,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
              text = "Imagem não disponível na base",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(Modifier.height(20.dp))

      SectionTitle("IDENTIFICAÇÃO")
      SpecRow("Categoria", uiState.category.toCategoryLabel())
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
      SpecRow("Slots de memória", uiState.memorySlots)
      SpecRow("Memória máxima", uiState.maxMemoryGb, unit = "GB")
      SpecRow("Velocidade padrão máxima", uiState.maxMemoryStandardMhz, unit = "MHz")
      SpecRow("Velocidade máxima em overclock", uiState.maxMemoryOcMhz, unit = "MHz")

      Spacer(Modifier.height(16.dp))

      SectionTitle("PREÇO DE REFERÊNCIA")
      val referencePrice = uiState.referencePriceAmount?.let { amount ->
        val currency = uiState.referencePriceCurrency.toCurrencyLabel()
        if (currency.isNotBlank()) "$currency $amount" else "$amount"
      }
      SpecRow("Valor", referencePrice)
      SpecRow("Data", uiState.referencePriceDate)
      SpecRow("Fonte", uiState.referencePriceSourceUrl)

      Spacer(Modifier.height(16.dp))

      SectionTitle("EXPANSÃO")
      SpecRow("Slots SATA", uiState.sataSlots)
      SpecRow("Slots PCIe", uiState.pcieSlots)

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
          text = "Socket: ${uiState.socket.ifBlank { NO_DATA_TEXT }}",
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
  var scale by remember { mutableFloatStateOf(1f) }
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
private fun SpecRow(
  label: String,
  value: Int,
  unit: String? = null
) {
  SpecRow(
    label = label,
    value = if (value > 0) value.toString() else null,
    unit = unit
  )
}

@Composable
private fun SpecRow(
  label: String,
  value: String?,
  unit: String? = null
) {
  val isMissing = value.isNullOrBlank() ||
      value.trim() == "0" ||
      value.trim() == "Informação não informada" ||
      value.trim() == "Não informado"

  val displayValue = when {
    isMissing -> NO_DATA_TEXT
    unit != null && !value.endsWith(unit, ignoreCase = true) -> "$value $unit"
    else -> value
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontSize = 13.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = displayValue,
      fontSize = 13.sp,
      fontWeight = if (isMissing) FontWeight.Normal else FontWeight.SemiBold,
      fontStyle = if (isMissing) FontStyle.Italic else FontStyle.Normal,
      color = if (isMissing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.End
    )
  }
}

private fun String?.toCategoryLabel(): String = when (this?.lowercase()?.trim()) {
  "motherboard" -> "Placa-mãe"
  null, "" -> ""
  else -> this
}

private fun String?.toCurrencyLabel(): String = when (this?.uppercase()?.trim()) {
  "USD" -> "US$"
  "BRL" -> "R$"
  "EUR" -> "€"
  null, "" -> ""
  else -> this
}

private fun String?.toPriceStatusLabel(): String = when (this?.lowercase()?.trim()) {
  "reference_only" -> "Somente referência"
  "market_price" -> "Preço de mercado"
  null, "" -> ""
  else -> this
}

private fun String?.toPriceContextLabel(): String = when (this?.lowercase()?.trim()) {
  "historical_review" -> "Análise histórica"
  "current_market" -> "Mercado atual"
  null, "" -> ""
  else -> this
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
            text = when (cpu.factor) {
              Factor.INTEL -> "Intel"
              Factor.AMD -> "AMD"
            },
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
          text = "Socket: ${cpu.socket.ifBlank { NO_DATA_TEXT }}",
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