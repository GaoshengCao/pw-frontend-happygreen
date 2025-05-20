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

suspend fun registerUser(api: ApiService, username: String,password: String): String? {
    val request = LoginRequest(username,password)
    return try {
        val api = RetrofitInstance.api.register(request)
        api.toString() // TODO VEDERE COSA RITORNA
    }catch(e:Exception){
        return e.message
        //return null
    }

}