package ru.hqr.tinywms.biometric

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import ru.hqr.tinywms.conf.dataStore
import ru.hqr.tinywms.util.PREF_BIOMETRIC
import javax.inject.Inject
import javax.inject.Named

class BiometricPreferences(
    private val context: Context
) {
    object PreferencesKey {
        val KEY_TOKEN = stringPreferencesKey("user_token")
        val KEY_USERNAME = stringPreferencesKey("user_name")
        val KEY_PASSWORD = stringPreferencesKey("user_password")
        val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }

    suspend fun setToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.KEY_TOKEN] = token }
//        preferencesDataStore.edit { preferences ->
//            preferences[PreferencesKey.KEY_TOKEN] = token
//        }
    }

    suspend fun getUserName(): String? {
        return context.dataStore.data.first()[PreferencesKey.KEY_USERNAME]
//        return preferencesDataStore.data.first()[PreferencesKey.KEY_USERNAME]
    }

    suspend fun setUserName(userName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.KEY_USERNAME] = userName
        }
//        preferencesDataStore.edit { preferences ->
//            preferences[PreferencesKey.KEY_USERNAME] = userName
//        }
    }

    suspend fun getPassword(): String? {
        return context.dataStore.data.first()[PreferencesKey.KEY_PASSWORD]
//        return preferencesDataStore.data.first()[PreferencesKey.KEY_PASSWORD]
    }

    suspend fun setPassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.KEY_PASSWORD] = password
        }
//        preferencesDataStore.edit { preferences ->
//            preferences[PreferencesKey.KEY_PASSWORD] = password
//        }
    }

    suspend fun isBiometricEnabled(): Boolean {
        return context.dataStore.data.first()[PreferencesKey.KEY_BIOMETRIC_ENABLED] ?: false
//        return preferencesDataStore.data.first()[PreferencesKey.KEY_BIOMETRIC_ENABLED] ?: false
    }

    suspend fun setBiometricEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.KEY_BIOMETRIC_ENABLED] = isEnabled
        }
//        preferencesDataStore.edit { preferences ->
//            preferences[PreferencesKey.KEY_BIOMETRIC_ENABLED] = isEnabled
//        }
    }
}