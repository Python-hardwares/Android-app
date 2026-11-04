package com.hardware.scanner.app.model.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerConfigStore @Inject constructor(
  @ApplicationContext context: Context
) {
  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

  fun getServerUrl(): String? = preferences.getString(SERVER_URL_KEY, null)

  fun saveServerUrl(url: String) {
    preferences.edit().putString(SERVER_URL_KEY, url).apply()
  }

  private companion object {
    const val PREFERENCES_NAME = "server_configuration"
    const val SERVER_URL_KEY = "server_url"
  }
}
