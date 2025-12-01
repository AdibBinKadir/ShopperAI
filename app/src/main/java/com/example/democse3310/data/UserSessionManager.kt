package com.example.democse3310.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
private val CURRENT_USER_EMAIL = stringPreferencesKey("current_user_email")

suspend fun setCurrentUser(context: Context, userId: String, email: String) {
    context.dataStore.edit { prefs ->
        prefs[CURRENT_USER_ID] = userId
        prefs[CURRENT_USER_EMAIL] = email
    }
}

fun getCurrentUserIdFlow(context: Context) =
    context.dataStore.data.map { prefs -> prefs[CURRENT_USER_ID] ?: "" }

fun getCurrentUserEmailFlow(context: Context) =
    context.dataStore.data.map { prefs -> prefs[CURRENT_USER_EMAIL] ?: "" }

suspend fun clearCurrentUser(context: Context) {
    context.dataStore.edit { prefs ->
        prefs.remove(CURRENT_USER_ID)
        prefs.remove(CURRENT_USER_EMAIL)
    }
}
