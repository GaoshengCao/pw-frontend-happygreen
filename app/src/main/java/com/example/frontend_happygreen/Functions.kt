package com.example.frontend_happygreen

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.common.api.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream

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

            val membership = newMembership(creatorID, getIDGroup(api,groupName), "admin")
            val result = api.joinGroup(membership)


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
    val groups = api.getAllGroups()

    for (group in groups) {
        if (group.name == groupName){
            return group.id
        }
    }
    return 0
}

suspend fun joinGroup(api: ApiService, user: Int, groupID: Int) : String{
    val membership = newMembership(user, groupID, "member")
    return try {
        val response = api.joinGroup(membership)
        if (response.isSuccessful) {
            val member = response.body()
            "Joined Group: ${member?.group}"
        } else {
            "failed joined: ${response.errorBody()?.string()}"
        }
    }catch (e: Exception) {
        "Error: ${e.message}"
    }
}

suspend fun getGroupsByUserID(api: ApiService, userId: Int): List<Group> {
    val groupIds = mutableListOf<Int>()
    val resultGroups = mutableListOf<Group>()

    try {
        val memberships = api.getMembership()
        for (membership in memberships) {
            if (membership.user == userId) {
                groupIds.add(membership.group)
            }
        }

        val allGroups = api.getAllGroups()
        for (group in allGroups) {
            if (group.id in groupIds) {
                resultGroups.add(group)
            }
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }

    return resultGroups
}

suspend fun createPost(api: ApiService, context: Context, newPostData: PostData): String {
    return try {
        // Helper to convert nullable Int to RequestBody or null
        fun intToRequestBody(value: Int?): RequestBody? =
            value?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

        val groupPart = intToRequestBody(newPostData.groupId)
        val authorPart = intToRequestBody(newPostData.authorId)
        val textPart = newPostData.text.toRequestBody("text/plain".toMediaTypeOrNull())
        val latPart = newPostData.locationLat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val lngPart = newPostData.locationLng?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

        // Convert Uri to MultipartBody.Part
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(newPostData.imageUri)
        if (inputStream == null) return "Failed to read image from Uri"

        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)

        // Call API
        val response = api.createPost(groupPart, authorPart, textPart, latPart, lngPart, imagePart)

        // Clean up temp file
        tempFile.delete()

        if (response.isSuccessful) {
            "Post created successfully! ID: ${response.body()?.postId ?: "unknown"}"
        } else {
            "Post creation failed: ${response.errorBody()?.string()}"
        }
    } catch (e: Exception) {
        "Error creating post: ${e.message}" //Ciao
    }
}


