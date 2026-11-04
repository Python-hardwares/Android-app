package com.hardware.scanner.app.model.entity



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Motherboard(
  val id: String,
  val category: String,
  val brand: String,
  val model: String,

  @SerialName("price_status")
  val priceStatus: String? = null,

  @SerialName("reference_price_amount")
  val referencePriceAmount: Double? = null,

  @SerialName("reference_price_currency")
  val referencePriceCurrency: String? = null,

  @SerialName("reference_price_context")
  val referencePriceContext: String? = null,

  @SerialName("reference_price_source_url")
  val referencePriceSourceUrl: String? = null,

  @SerialName("reference_price_date")
  val referencePriceDate: String? = null,

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

  val color: String? = null,

  @SerialName("color_source")
  val colorSource: String? = null,

  val chipset: String? = null
)

fun Motherboard.isMotherboard(): Boolean =
  category.trim().equals("motherboard", ignoreCase = true)
