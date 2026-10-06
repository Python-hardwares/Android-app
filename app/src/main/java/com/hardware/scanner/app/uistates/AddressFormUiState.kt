package com.hardware.scanner.app.uistates

data class AddressFormUiState(
    val logradouro: String = "",
    val bairro: String = "",
    val cidade: String = "",
    val estado: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

data class HardwareFormUiState(
    val cpu: String = "",
    val ram: String = "",
    val gpu: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false
)
