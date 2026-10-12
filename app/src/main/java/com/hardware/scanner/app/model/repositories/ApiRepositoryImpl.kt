package com.hardware.scanner.app.model.repositories

import android.util.Log
import com.hardware.scanner.app.model.entity.Scan
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class LookupRequest(val text: String)

class ApiRepositoryImpl(
  private val httpClient: HttpClient
) : ApiRepository {

  override suspend fun searchMotherboardByPhoto(file: File): Scan {
    try {
      Log.i("photoRepository", "UPLOAD: iniciando")
      Log.i("photoRepository", "UPLOAD: ${file.absolutePath}")
      Log.i("photoRepository", "UPLOAD: existe=${file.exists()}")
      Log.i("photoRepository", "UPLOAD: tamanho=${file.length()}")

      val response = httpClient.submitFormWithBinaryData(
        url = "http://192.168.20.6:8000/scan",
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

      val scanObject = Json.decodeFromString<Scan>(response.bodyAsText())

      Log.i("photoRepository", "UPLOAD HTTP: ${response.status}")

      return scanObject
    } catch (e: Exception) {
      Log.e("photoRepository", "UPLOAD ERRO: ${e.message}")
      e.printStackTrace()
      throw e
    }
  }

  override suspend fun searchMotherboardByName(name: String): Scan {
    try {

      val response: HttpResponse = httpClient.post("http://192.168.20.6:8000/lookup") {
        contentType(ContentType.Application.Json)
        setBody(LookupRequest(text = name))
      }


      Log.i("ApiRepository", response.body())
      val scanObject: Scan = Json.decodeFromString<Scan>(response.bodyAsText())
      return scanObject
    } catch (e: Exception) {
      Log.e("photoRepository", "UPLOAD ERRO: ${e.message}")
      e.printStackTrace()
      throw e
    }
  }
}