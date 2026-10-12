package com.hardware.scanner.app.model.entity



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Motherboard(
  val id: String,
  val category: String,
  val brand: String,
  val model: String,

  @SerialName("image_url")
  val imageUrl: String? = null,

  val socket: String? = null,

  @SerialName("form_factor")
  val formFactor: String? = null,

  @SerialName("max_memory_gb")
  val maxMemoryGb: Int? = null,

  @SerialName("memory_slots")
  val memorySlots: Int? = null,

  @SerialName("memory_type")
  val memoryType: String? = null,

  @SerialName("memory_slot_type")
  val memorySlotType: String? = null,

  @SerialName("memory_channels")
  val memoryChannels: Int? = null,

  @SerialName("max_memory_standard_mhz")
  val maxMemoryStandardMhz: Int? = null,

  @SerialName("max_memory_oc_mhz")
  val maxMemoryOcMhz: Int? = null,

  val color: String? = null
)
