package com.hardware.scanner.app.view.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.hardware.scanner.app.model.entity.Motherboard
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

@Serializable
data class MotherboardSpecs(
  val id: String,
  val brand: String,
  val model: String,
  val imageUrl: String,
  val socket: String,
  val formFactor: String,
  val maxMemoryGb: Int,
  val memorySlots: Int,
  val color: String
)

fun Motherboard.toRoute() = MotherboardSpecs(
  id = id,
  brand = brand,
  model = model,
  imageUrl = imageUrl ?: "",
  socket = socket ?: "",
  formFactor = formFactor ?: "",
  maxMemoryGb = maxMemoryGb ?: 0,
  memorySlots = memorySlots ?: 0,
  color = color ?: ""
)

@HiltViewModel
class MotherboardSpecsViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle
) : ViewModel() {

  private val route = savedStateHandle.toRoute<MotherboardSpecs>()

  private val _uiState = MutableStateFlow(
    MenuUiState(
      id = route.id,
      brand = route.brand,
      model = route.model,
      imageUrl = route.imageUrl,
      socket = route.socket,
      formFactor = route.formFactor,
      color = route.color,
      maxMemoryGb = route.maxMemoryGb,
      memorySlots = route.memorySlots
    )
  )
  val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()
}