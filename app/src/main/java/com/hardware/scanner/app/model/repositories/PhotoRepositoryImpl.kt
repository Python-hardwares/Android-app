package com.hardware.scanner.app.model.repositories

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File

class PhotoRepositoryImpl(val httpClient: HttpClient = HttpClient()) : PhotoRepository {
  override suspend fun uploadPhoto(file: File): String {
    try {
      println("UPLOAD: iniciando")
      println("UPLOAD: ${file.absolutePath}")
      println("UPLOAD: existe=${file.exists()}")
      println("UPLOAD: tamanho=${file.length()}")

      val response = httpClient.submitFormWithBinaryData(
        url = "http://192.168.20.6:8000/api/scan",
        formData = formData {
          append(
            "image",
            file.readBytes(),
            Headers.build {
              append(HttpHeaders.ContentType, "image/jpeg")
              append(
                HttpHeaders.ContentDisposition,
                "form-data; name=\"image\"; filename=\"${file.name}\""
              )
            }
          )
        }
      )

      val body = response.bodyAsText()

      Log.i("photoRepository", "UPLOAD HTTP: ${response.status}")
      Log.i("photoRepository", "UPLOAD BODY: $body")

      return body

    } catch (e: Exception) {
      Log.e("photoRepository", "UPLOAD ERRO: ${e.message}")
      e.printStackTrace()
      throw e
    }
  }
}