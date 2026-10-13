package com.hardware.scanner.app.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TypeCpu {
  @SerialName("Desktop")
  DESKTOP,

  @SerialName("Embarcado")
  EMBARCADO,

  @SerialName("Servidor")
  SERVIDOR,

  @SerialName("Notebook")
  NOTEBOOK,

  @SerialName("null")
  UNKNOWN
}

@Serializable
enum class Factor {
  @SerialName("AMD")
  AMD,

  @SerialName("INTEL")
  INTEL
}

@Serializable
data class Cpu(
  val name: String,
  val socket: String,
  val cores: Int,
  val threads: Int,
  val typeCpu: TypeCpu,
  val factor: Factor
)