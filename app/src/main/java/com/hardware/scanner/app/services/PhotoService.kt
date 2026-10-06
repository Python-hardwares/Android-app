package com.hardware.scanner.app.services

import android.content.Context
import java.io.File

class PhotoService(
  private val context: Context
) {

  fun createPhotoFile(): File {
    return File.createTempFile(
      "hardware_scan_",
      ".jpg",
      context.cacheDir
    )
  }
}