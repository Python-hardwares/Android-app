package com.hardware.scanner.app.view.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.hardware.scanner.app.model.entity.Cpu
import com.hardware.scanner.app.model.entity.Factor
import com.hardware.scanner.app.model.entity.Motherboard
import com.hardware.scanner.app.model.entity.TypeCpu
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

@Serializable
data class MotherboardSpecs(
  val id: String,
  val category: String = "",
  val brand: String,
  val model: String,
  val imageUrl: String,
  val priceStatus: String = "",
  val referencePriceAmount: Double? = null,
  val referencePriceCurrency: String = "",
  val referencePriceContext: String = "",
  val referencePriceSourceUrl: String = "",
  val referencePriceDate: String = "",
  val socket: String,
  val formFactor: String,
  val maxMemoryGb: Int,
  val memorySlots: Int,
  val memoryType: String = "",
  val memorySlotType: String = "",
  val memoryChannels: Int = 0,
  val maxMemoryStandardMhz: Int = 0,
  val maxMemoryOcMhz: Int = 0,
  val color: String,
  val colorSource: String = "",
  val chipset: String = ""
)

fun Motherboard.toRoute() = MotherboardSpecs(
  id = id,
  category = category,
  brand = brand,
  model = model,
  imageUrl = imageUrl ?: "",
  priceStatus = priceStatus ?: "",
  referencePriceAmount = referencePriceAmount,
  referencePriceCurrency = referencePriceCurrency ?: "",
  referencePriceContext = referencePriceContext ?: "",
  referencePriceSourceUrl = referencePriceSourceUrl ?: "",
  referencePriceDate = referencePriceDate ?: "",
  socket = socket ?: "",
  formFactor = formFactor ?: "",
  maxMemoryGb = maxMemoryGb ?: 0,
  memorySlots = memorySlots ?: 0,
  memoryType = memoryType ?: "",
  memorySlotType = memorySlotType ?: "",
  memoryChannels = memoryChannels ?: 0,
  maxMemoryStandardMhz = maxMemoryStandardMhz ?: 0,
  maxMemoryOcMhz = maxMemoryOcMhz ?: 0,
  color = color ?: "",
  colorSource = colorSource ?: "",
  chipset = chipset ?: ""
)

data class MotherboardSpecsUiState(
  val id: String = "",
  val category: String = "",
  val brand: String = "",
  val model: String = "",
  val imageUrl: String = "",
  val chipset: String = "",
  val socket: String = "",
  val formFactor: String = "",
  val color: String = "",
  val colorSource: String = "",
  val memoryType: String = "",
  val memorySlotType: String = "",
  val memoryChannels: String = "",
  val maxMemoryStandardMhz: Int = 0,
  val maxMemoryOcMhz: Int = 0,
  val maxMemoryGb: Int = 0,
  val memorySlots: Int = 0,
  val priceStatus: String = "",
  val referencePriceAmount: Double? = null,
  val referencePriceCurrency: String = "",
  val referencePriceContext: String = "",
  val referencePriceSourceUrl: String = "",
  val referencePriceDate: String = "",
  val sataSlots: Int = 0,
  val pcieSlots: Int = 0,
  val supportedCpus: List<Cpu> = emptyList(),
  val datasheetUrl: String = "https://www.google.com"
)

@HiltViewModel
class MotherboardSpecsViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val route = savedStateHandle.toRoute<MotherboardSpecs>()

  private val _uiState = MutableStateFlow(
    MotherboardSpecsUiState(
      id = route.id,
      category = route.category,
      brand = route.brand,
      model = route.model,
      imageUrl = route.imageUrl,
      priceStatus = route.priceStatus,
      referencePriceAmount = route.referencePriceAmount,
      referencePriceCurrency = route.referencePriceCurrency,
      referencePriceContext = route.referencePriceContext,
      referencePriceSourceUrl = route.referencePriceSourceUrl,
      referencePriceDate = route.referencePriceDate,
      chipset = route.chipset,
      socket = route.socket,
      formFactor = route.formFactor,
      color = route.color,
      colorSource = route.colorSource,
      memoryType = route.memoryType,
      memorySlotType = route.memorySlotType,
      memoryChannels = route.memoryChannels.toString(),
      maxMemoryStandardMhz = route.maxMemoryStandardMhz,
      maxMemoryOcMhz = route.maxMemoryOcMhz,
      maxMemoryGb = route.maxMemoryGb,
      memorySlots = route.memorySlots,
      supportedCpus = getSampleCpus(route.socket),
      datasheetUrl = "https://www.google.com/search?q=${route.brand}+${route.model}+datasheet"
    )
  )
  val uiState: StateFlow<MotherboardSpecsUiState> = _uiState.asStateFlow()

  private fun getSampleCpus(socket: String): List<Cpu> {
    val s = socket.ifBlank { "LGA1700" }
    return listOf(
      Cpu(
        name = "Intel Core i9-14900K",
        socket = s,
        cores = 24,
        threads = 32,
        typeCpu = TypeCpu.DESKTOP,
        factor = Factor.INTEL
      ),
      Cpu(
        name = "Intel Core i7-14700K",
        socket = s,
        cores = 20,
        threads = 28,
        typeCpu = TypeCpu.DESKTOP,
        factor = Factor.INTEL
      ),
      Cpu(
        name = "Intel Core i5-14600K",
        socket = s,
        cores = 14,
        threads = 20,
        typeCpu = TypeCpu.DESKTOP,
        factor = Factor.INTEL
      ),
      Cpu(
        name = "AMD Ryzen 7 7800X3D",
        socket = if (s.contains("AM", ignoreCase = true)) s else "AM5",
        cores = 8,
        threads = 16,
        typeCpu = TypeCpu.DESKTOP,
        factor = Factor.AMD
      ),
      Cpu(
        name = "AMD Ryzen 5 7600X",
        socket = if (s.contains("AM", ignoreCase = true)) s else "AM5",
        cores = 6,
        threads = 12,
        typeCpu = TypeCpu.DESKTOP,
        factor = Factor.AMD
      )
    )
  }
}