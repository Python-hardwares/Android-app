package com.hardware.scanner.app.services

import android.os.Environment
import androidx.camera.core.ImageCapture
import java.io.File

class PhotoService {
  fun createDirectory(directory: String): File {
    val photoDir = File(
      Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
      ),
      directory
    )

    if (!photoDir.exists()) {
      photoDir.mkdirs()
    }

    return photoDir
  }

  fun createPhotoFile(directoryPath: File): File {
    return File.createTempFile(
      "hardware_scan_",
      ".jpg",
      directoryPath
    )
  }

  fun createOutputOptions(photoFile: File): ImageCapture.OutputFileOptions {
    return ImageCapture.OutputFileOptions.Builder(photoFile).build()
  }
}