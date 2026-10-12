package com.hardware.scanner.app.model.repositories

import com.hardware.scanner.app.model.entity.Scan
import java.io.File

interface ApiRepository {
  suspend fun searchMotherboardByPhoto(file: File): Scan
  suspend fun searchMotherboardByName(name: String): Scan
}



