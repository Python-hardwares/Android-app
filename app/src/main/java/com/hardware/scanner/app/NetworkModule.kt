package com.hardware.scanner.app

import com.hardware.scanner.app.model.repositories.ApiRepository
import com.hardware.scanner.app.model.repositories.ApiRepositoryImpl
import com.hardware.scanner.app.model.repositories.ServerConfigStore
import com.hardware.scanner.app.services.PhotoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
  }

  @Provides
  @Singleton
  fun provideHttpClient(json: Json): HttpClient = HttpClient(OkHttp) {
    install(HttpTimeout) {
      requestTimeoutMillis = 120_000
      connectTimeoutMillis = 10_000
      socketTimeoutMillis = 120_000
    }

    install(ContentNegotiation) {
      json(json)
    }
  }

  @Provides
  @Singleton
  fun provideApiRepository(
    client: HttpClient,
    serverConfigStore: ServerConfigStore
  ): ApiRepository = ApiRepositoryImpl(client, serverConfigStore)

  @Provides
  fun providePhotoService(): PhotoService = PhotoService()
}