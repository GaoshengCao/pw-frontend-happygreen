package com.example.frontend_happygreen

import android.util.Log

suspend fun loginUser(api: ApiService, username: String, password: String): String? {
    return try {
        val response = api.getToken(LoginRequest(username, password))
        response.access
    } catch (e: Exception) {
        null
    }
}

suspend fun registerUser(api: ApiService, username: String, password: String): String {

    val request = NewUser(username, password)
    return try {
        val response = api.register(request)
        if (response.isSuccessful) {
            val user = response.body()
            "Registered user: ${user?.username}"
        } else {
            "Registration failed: ${response.errorBody()?.string()}"
        }
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

suspend fun getId(api: ApiService, username: String): Int {
    val users = api.getAllUsers()

    for (user in users) {
        if (user.username == username) {
            return user.id
        }
    }
    return 0
}