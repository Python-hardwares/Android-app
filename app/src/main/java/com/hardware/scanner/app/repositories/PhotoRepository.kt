package com.hardware.scanner.app.repositories

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File

class PhotoRepository(
  private val httpClient: HttpClient
) {

  suspend fun uploadPhoto(file: File) {

    val response = httpClient.post(
      "http://192.168.20.6:8000/api/scan"
    ) {

      setBody(
        MultiPartFormDataContent(
          formData {
            append(
              key = "image",
              value = file.readBytes(),
              headers = Headers.build {
                append(
                  HttpHeaders.ContentType,
                  "image/jpeg"
                )
                append(
                  HttpHeaders.ContentDisposition,
                  "filename=\"${file.name}\""
                )
              }
            )
          }
        )
      )
    }

    println("HTTP: ${response.status}")
    println("BODY: ${response.bodyAsText()}")
  }
}