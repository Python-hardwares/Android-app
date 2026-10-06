package com.hardware.scanner.app.di

import com.hardware.scanner.app.repositories.PhotoRepository
import com.hardware.scanner.app.services.PhotoService
import com.hardware.scanner.app.viewmodels.CameraViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {
  singleOf(::PhotoService)
  singleOf(::PhotoRepository)
  viewModelOf(::CameraViewModel)
}

val networkModule = module {
  single {
    HttpClient(Android) {
      install(HttpTimeout) {
        connectTimeoutMillis = 5000
        requestTimeoutMillis = 10000
      }
      install(Logging) {
        level = LogLevel.ALL
      }
      install(ContentNegotiation) {
        json(Json {
          ignoreUnknownKeys = true
          prettyPrint = true
          isLenient = true
        })
      }
    }
  }
}
