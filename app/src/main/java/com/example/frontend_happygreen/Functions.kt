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

suspend fun getGroupNameById(api: ApiService, id: Int): String? {
    return try {
        val group = api.getGroup(id)
        group.name
    } catch (e: Exception) {
        Log.e("API", "Error fetching group: ${e.message}")
        null
    }
}

suspend fun getPostTextById(api: ApiService, id: Int): String? {
    return try {
        val post = api.getPost(id)
        post?.text
    } catch (e: Exception) {
        Log.e("API", "Error fetching post: ${e.message}")
        null
    }
}

suspend fun getEcoProductNameById(api: ApiService, id: Int): String? {
    return try {
        val product = api.getEcoProduct(id)
        product?.name
    } catch (e: Exception) {
        Log.e("API", "Error fetching eco product: ${e.message}")
        null
    }
}

suspend fun getQuizTitleById(api: ApiService, id: Int): String? {
    return try {
        val quiz = api.getQuiz(id)
        quiz?.title
    } catch (e: Exception) {
        Log.e("API", "Error fetching quiz: ${e.message}")
        null
    }
}
