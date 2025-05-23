package com.example.frontend_happygreen

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import com.google.android.gms.common.api.Response
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
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

suspend fun getPostByGroupName(api: ApiService,nomeGruppo : String): List<Post>?{
    val groupID = getIDGroup(api,nomeGruppo)
    val resultPosts = mutableListOf<Post>()

    try {
        val allPosts = api.getPosts()
        for (post in allPosts) {
            if (post.group == groupID) {
                resultPosts.add(post)
            }
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }

    return resultPosts
}

suspend fun getGroupNameById(api: ApiService, id : Int): String{
    try {
        val allgroups = api.getAllGroups()
        for (group in allgroups){
            if(group.id == id){
                return group.name
            }
        }

    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
    return "Fallimento Tu sei"
}

suspend fun getUsernameById(api: ApiService,id: Int):String?{
    try {
        val user = api.getUser(id)
        return user.username

    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
    return null
}

suspend fun getMembersByGroupName(api: ApiService, groupName: String): List<User> {
    val groupID = getIDGroup(api, groupName)
    val memberships = api.getMembership()
    val users = mutableListOf<User>()

    for (member in memberships) {
        if (member.group == groupID) {
            val user = api.getUser(member.user)
            users.add(user)
        }
    }
    return users
}


suspend fun getPostById(api: ApiService,postId: Int): Post?{
    val posts = api.getPosts()

    for (post in posts) {
        if (post.id == postId){
            return post
        }
    }
    return null
}

suspend fun getCommentsByPostId(api: ApiService,postId: Int):List<Comment>{
    val comments = api.getComments()
    val resultComments = mutableListOf<Comment>()

    for (comment in comments){
        if (comment.post == postId){
            resultComments.add(comment)
        }
    }
    return resultComments
}

suspend fun addCommentToPost(api: ApiService,postId: Int,userId: Int,commentText: String):String{
    var returno = ""
    try {
        val newComment = NewComment(postId, userId,commentText)
        val responce = api.createComment(newComment)
        returno = responce.body().toString()
    }catch (e: Exception){
        println("Error: ${e.message}")
    }
    return returno
}

suspend fun getFiveQuizQuestion(api: ApiService): List<QuizQuestion> {
    val resultQuiz = mutableListOf<QuizQuestion>()

    val usedIds = mutableSetOf<Int>()
    val maxTries = 10  // avoid infinite loop in case of too many duplicates

    repeat(5) {
        var attempts = 0
        var quiz: QuizQuestion? = null

        while (quiz == null && attempts < maxTries) {
            val randomId = (1..40).random()  // Adjust range to your actual quiz ID range
            if (!usedIds.contains(randomId)) {
                try {
                    quiz = api.getQuiz(randomId)
                    usedIds.add(randomId)
                    resultQuiz.add(quiz)
                } catch (e: Exception) {
                    println("Error: ${e.message}")

                }
            }
            attempts++
        }
    }

    return resultQuiz
}

@SuppressLint("MissingPermission")
suspend fun getLastKnownLocation(context: Context): Location? {
    return try {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.lastLocation.await()
    } catch (e: Exception) {
        null
    }
}

suspend fun quitGroup(api: ApiService,userId: Int,groupName : String){
    var groupId = getIDGroup(api,groupName)

    val memberships = api.getMembership()

    for (membership in memberships){
        if ((membership.user == userId) && (membership.group == groupId)){
            api.quitGroup(membership.id)
            return
        }
    }
}

suspend fun caricaPunteggio(api: ApiService,userId: Int,password: String,point:Int){
    // Point è un read Only, non possiamo aggiornalo.
//    var user = api.getUser(userId)
//    user.points += point
//    if (user.points >= 10){
//        user.points -= 10
//        user.level += 1
//    }
//    user.password = password
//    api.gameUpdateUser(userId,user)

}

suspend fun caricaImmagineProfilo(api: ApiService, userId: Int,password: String, context: Context, user: UserData) {
    try {
        val userdat = api.getUser(userId)
        val username = userdat.username.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val password = password.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        if (username == null || password == null) throw IllegalArgumentException("Username and password are required")

        val inputStream = context.contentResolver.openInputStream(user.imageUri)
            ?: throw IllegalArgumentException("Image Uri is invalid")

        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }

        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("profile_pic", tempFile.name, requestFile)

        val response = api.updateUserImage(userId, username, password, imagePart)

        tempFile.delete()

        if (response.isSuccessful) {
            Log.d("Upload", "Success!")
        } else {
            Log.e("Upload", "Failed: ${response.errorBody()?.string()}")
        }

    } catch (e: Exception) {
        Log.e("Upload", "Error: ${e.message}")
    }
}




