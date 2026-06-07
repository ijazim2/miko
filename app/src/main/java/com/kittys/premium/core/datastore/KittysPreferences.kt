package com.kittys.premium.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════
//   DATASTORE  — lightweight key-value persistence
//   Used for: onboarding seen, theme, language, user prefs
// ═══════════════════════════════════════════════════════

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "kittys_preferences"
)

@Singleton
class KittysPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // ── Keys ──
    companion object {
        val ONBOARDING_SEEN     = booleanPreferencesKey("onboarding_seen")
        val SAVED_USER_ID       = stringPreferencesKey("saved_user_id")
        val SAVED_USER_NAME     = stringPreferencesKey("saved_user_name")
        val SELECTED_LANGUAGE   = stringPreferencesKey("selected_language")
        val NOTIFICATIONS_ON    = booleanPreferencesKey("notifications_on")
        val AI_SUGGESTIONS_ON   = booleanPreferencesKey("ai_suggestions_on")
        val LAST_SEEN_CATEGORY  = stringPreferencesKey("last_seen_category")
        val FCM_TOKEN           = stringPreferencesKey("fcm_token")
        val APP_VERSION_CODE    = intPreferencesKey("app_version_code")
    }

    // ── Onboarding ──
    val hasSeenOnboarding: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[ONBOARDING_SEEN] ?: false }

    suspend fun setOnboardingSeen() {
        dataStore.edit { it[ONBOARDING_SEEN] = true }
    }

    // ── User session (lightweight) ──
    val savedUserId: Flow<String?> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[SAVED_USER_ID] }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { it[SAVED_USER_ID] = userId }
    }

    suspend fun clearUser() {
        dataStore.edit {
            it.remove(SAVED_USER_ID)
            it.remove(SAVED_USER_NAME)
        }
    }

    // ── Language ──
    val selectedLanguage: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[SELECTED_LANGUAGE] ?: "en" }

    suspend fun setLanguage(code: String) {
        dataStore.edit { it[SELECTED_LANGUAGE] = code }
    }

    // ── Notification preference ──
    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[NOTIFICATIONS_ON] ?: true }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ON] = enabled }
    }

    // ── AI suggestions ──
    val aiSuggestionsEnabled: Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[AI_SUGGESTIONS_ON] ?: true }

    suspend fun setAISuggestionsEnabled(enabled: Boolean) {
        dataStore.edit { it[AI_SUGGESTIONS_ON] = enabled }
    }

    // ── FCM token ──
    val fcmToken: Flow<String?> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[FCM_TOKEN] }

    suspend fun saveFcmToken(token: String) {
        dataStore.edit { it[FCM_TOKEN] = token }
    }
}
