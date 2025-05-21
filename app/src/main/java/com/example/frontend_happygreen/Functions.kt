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
        if (user.username == username){
            return user.id
        }
    }
    return 0
}

suspend fun createGroup(api: ApiService, creatorID: Int, groupName: String) : String{
    val newGroup = NewGroup(groupName,creatorID)
    return try {
        val response = api.createGroup(newGroup)
        if (response.isSuccessful) {
            val group = response.body()
            "Created Group: ${group?.name}"
        } else {
            "failed creation: ${response.errorBody()?.string()}"
        }
    }catch (e: Exception) {
        "Error: ${e.message}"
    }
}

suspend fun getPost(api: ApiService, groupID: Int): List<Post>? {
    return try {
        val response = api.getPosts() // Assuming this returns List<Post>
        val filteredPosts = response.filter { it.group == groupID }
        filteredPosts
    } catch (e: Exception) {
        Log.e("getPost", "Error: ${e.message}")
        null
    }
}

suspend fun getIDGroup(api: ApiService,groupName:String): Int{
    val groups = api.getAllUsers()

    for (group in groups) {
        if (group.username == groupName){
            return group.id
        }
    }
    return 0
}