package com.hardware.scanner.app.services

import android.os.Environment
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import kotlinx.coroutines.flow.update
import java.io.File

class PhotoService {
  fun createDirectory(directory: String): File {
    val photoDir = File(
      Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
      ),
      directory
    )

    photoDir.mkdirs()

    return photoDir
  }

  fun createOutputOptions(directoryPath: File): ImageCapture.OutputFileOptions {
    val photoFile = File.createTempFile(
      "hardware_scan_",
      ".jpg",
      directoryPath
    )

    val outputOptions =
      ImageCapture.OutputFileOptions.Builder(photoFile)
        .build()

    return outputOptions
  }
}