package com.example.frontend_happygreen
import android.net.Uri
import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiService {
    @GET("users/")
    suspend fun getAllUsers() : List<User>

    @GET("users/{id}/")
    suspend fun getUser(@Path("id") id: Int): User

    @GET("groups/{id}/")
    suspend fun getGroup(@Path("id") id: Int): Group

    @GET("groups/")
    suspend fun getAllGroups() : List<Group>

    @POST("groups/")
    suspend fun createGroup(@Body newGroup: NewGroup): retrofit2.Response<NewGroup>

    @GET("memberships/")
    suspend fun getMembership() : List<Membership>

    @POST("memberships/")
    suspend fun joinGroup(@Body newMembership: newMembership): retrofit2.Response<newMembership>

    @GET("posts/")
    suspend fun getPosts(): List<Post>

    @GET("posts/{id}/")
    suspend fun getPost(@Path("id") id: Int): Post

    @GET("comments/")
    suspend fun getComments() : List<Comment>

    @POST("comments/")
    suspend fun createComment(@Body newComment: NewComment): retrofit2.Response<NewComment>


    @Multipart
    @POST("posts/")  // adjust path if needed
    suspend fun createPost(
        @Part("group") group: RequestBody?,
        @Part("author") author: RequestBody?,
        @Part("text") text: RequestBody,
        @Part("location_lat") locationLat: RequestBody?,
        @Part("location_lng") locationLng: RequestBody?,
        @Part image: MultipartBody.Part
    ): retrofit2.Response<CreatePostResponse>



    @GET("quizzes/{id}/")
    suspend fun getQuiz(@Path("id") id: Int): QuizQuestion

    @POST("token/")
    suspend fun getToken(@Body tokenRequest: LoginRequest): LoginResponse

    @POST("users/")
    suspend fun register(@Body newUser: NewUser): retrofit2.Response<NewUser>

    @POST("token/refresh/")
    suspend fun refreshToken(@Body refreshRequest: RefreshTokenRequest?): LoginResponse


//Ciao
}

data class CreatePostResponse(
    val success: Boolean,
    val message: String,
    val postId: Int
)


data class LoginRequest(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
)
class RefreshTokenRequest (
    @SerializedName("refresh") val refresh: String?
)
data class LoginResponse(
    @SerializedName("access") val access: String?,
    @SerializedName("refresh") val refresh: String?
)
data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String?,
    @SerializedName("profile_pic") val profile_pic: String?,
    @SerializedName("points") val points: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("date_joined") val date_joined: String?
)
data class NewUser(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?,
)
data class NewGroup(
    @SerializedName("name") val name: String?,
    @SerializedName("created_by") val created_by: Int,
)
data class Group(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("created_by") val created_by: Int,
    @SerializedName("created_at") val created_at: String
)
data class Membership(
    @SerializedName("id") val id: Int,
    @SerializedName("user") val user: Int,
    @SerializedName("group") val group: Int,
    @SerializedName("role") val role: String?,
    @SerializedName("joined_at") val joined_at: String?
)
data class newMembership(
    @SerializedName("user") val user: Int,
    @SerializedName("group") val group: Int,
    @SerializedName("role") val role: String?
)
data class Post(
    @SerializedName("id") val id: Int,
    @SerializedName("group") val group: Int,
    @SerializedName("author") val author: Int,
    @SerializedName("text") val text: String,
    @SerializedName("image") val image: String?,
    @SerializedName("location_lat") val location_lat: Double?,
    @SerializedName("location_lng") val location_lng: Double?,
    @SerializedName("created_at") val created_at: String?
)
data class PostData(
    val groupId: Int?,
    val authorId: Int?,
    val text: String,
    val imageUri: Uri,
    val locationLat: Double?,
    val locationLng: Double?

)

data class Comment(
    @SerializedName("id") val id: Int,
    @SerializedName("post") val post: Int,
    @SerializedName("author") val author: Int,
    @SerializedName("text") val text: String?,
    @SerializedName("created_at") val created_at: String?
)

data class NewComment(
    @SerializedName("post") val post: Int,
    @SerializedName("author") val author: Int,
    @SerializedName("text") val text: String,
)
data class ScannedObject(
    @SerializedName("id") val id: Int,
    @SerializedName("user") val user: Int,
    @SerializedName("object_type") val object_type: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("recognized_at") val recognized_at: String?,
    @SerializedName("location_lat") val location_lat: Double?,
    @SerializedName("location_lng") val location_lng: Double?
)

data class QuizQuestion(
    @SerializedName("id") val id: Int,
    @SerializedName("question_text") val question_text: String,
    @SerializedName("correct_answer") val correct_answer: String,
    @SerializedName("wrong_answers") val wrong_answers: List<String>
)

data class Badge(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: String?
)
data class UserBadge(
    @SerializedName("id") val id: Int,
    @SerializedName("user") val user: Int,
    @SerializedName("badge") val badge: Int,
    @SerializedName("earned_at") val earned_at: String?
)
data class EcoProduct(
    @SerializedName("id") val id: Int,
    @SerializedName("barcode") val barcode: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("eco_score") val eco_score: String?,
    @SerializedName("sustainable_alt") val sustainable_alt: List<String>?
)
data class WasteClassification(
    @SerializedName("id") val id: Int,
    @SerializedName("material") val material: String?,
    @SerializedName("bin_color") val bin_color: String?,
    @SerializedName("instructions") val instructions: String?,
    @SerializedName("image") val image: String?
)

object RetrofitInstance {
    // Sostituiscilo con il tuo API KEY


    private const val BASE_URL = "https://910f-151-49-200-179.ngrok-free.app/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun create(token: String): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


}



