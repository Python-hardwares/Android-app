package com.hardware.scanner.app

import com.hardware.scanner.app.model.repositories.PhotoRepository
import com.hardware.scanner.app.model.repositories.PhotoRepositoryImpl
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Module
import dagger.Provides

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  @Provides
  fun provideAuthRepository(): PhotoRepository = PhotoRepositoryImpl()
}
