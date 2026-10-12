package com.hardware.scanner.app.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ScanStatus {
  @SerialName("not_found")
  NOT_FOUND,

  @SerialName("matched")
  MATCHED,

  @SerialName("suggestions")
  SUGGESTIONS
}
@Serializable
data class Scan(
  val status: ScanStatus,
  val matches: List<Motherboard>
)
