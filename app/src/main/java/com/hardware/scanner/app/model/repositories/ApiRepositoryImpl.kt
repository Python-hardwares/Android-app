package com.hardware.scanner.app.model.repositories

import android.util.Log
import com.hardware.scanner.app.model.entity.Scan
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
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

  private val responseJson = Json {
    ignoreUnknownKeys = true
  }

  override suspend fun searchMotherboardByPhoto(file: File): Scan {
    try {
      Log.i("photoRepository", "UPLOAD: iniciando ${file.absolutePath}, existe=${file.exists()}, tamanho=${file.length()}")

      val response: HttpResponse = httpClient.post("http://192.168.20.6:8000/scan") {
        setBody(
          MultiPartFormDataContent(
            formData {
              append(
                key = "image",
                value = file.readBytes(),
                headers = Headers.build {
                  append(HttpHeaders.ContentType, "image/jpeg")
                  append(
                    HttpHeaders.ContentDisposition,
                    "form-data; name=\"image\"; filename=\"${file.name}\""
                  )
                }
              )
            }
          )
        )
      }

      val responseText = response.bodyAsText()
      Log.i("photoRepository", "UPLOAD HTTP: ${response.status}, resposta: $responseText")

      return responseJson.decodeFromString<Scan>(responseText)
    } catch (e: Exception) {
      Log.e("photoRepository", "UPLOAD ERRO: ${e.message}", e)
      throw e
    }
  }

  override suspend fun searchMotherboardByName(name: String): Scan {
    try {
      val response: HttpResponse = httpClient.post("http://192.168.20.6:8000/lookup") {
        contentType(ContentType.Application.Json)
        setBody(LookupRequest(text = name))
      }

      val responseText = response.bodyAsText()
      Log.i("ApiRepository", "LOOKUP HTTP: ${response.status}, $responseText")
      return responseJson.decodeFromString<Scan>(responseText)
    } catch (e: Exception) {
      Log.e("photoRepository", "LOOKUP ERRO: ${e.message}", e)
      throw e
    }
  }
}