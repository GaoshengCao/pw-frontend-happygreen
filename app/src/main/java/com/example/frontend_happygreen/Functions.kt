package com.example.frontend_happygreen

import android.util.Log

suspend fun getUsernameById(api: ApiService, id: Int): String? {
    return try {
        val user = api.getUser(id)
        user.username
    } catch (e: Exception) {
        Log.e("API", "Errore durante la richiesta: ${e.message}")
        null
    }
}