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
