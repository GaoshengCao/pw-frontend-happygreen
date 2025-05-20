package com.example.frontend_happygreen
import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    @GET("users/{id}/")
    suspend fun getUser(@Path("id") id: Int): User

    @GET("groups/{id}/")
    fun getGroup(@Path("id") id: Int): Group

    @GET("posts/{id}/")
    fun getPost(@Path("id") id: Int): Post

    @GET("quizzes/{id}/")
    fun getQuiz(@Path("id") id: Int): Quiz

    @POST("token/")
    fun getToken(@Body tokenRequest: LoginRequest): LoginResponse

    @POST("users/")
    fun register(@Body loginRequest: LoginRequest) : Call<LoginRequest>

    @POST("token/refresh/")
    fun refreshToken(@Body refreshRequest: RefreshTokenRequest?): LoginResponse

}

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
data class Group(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("created_by") val created_by: Int,
    @SerializedName("created_at") val created_at: String?
)
data class Membership(
    @SerializedName("id") val id: Int,
    @SerializedName("user") val user: Int,
    @SerializedName("group") val group: Int,
    @SerializedName("role") val role: String?,
    @SerializedName("joined_at") val joined_at: String?
)
data class Post(
    @SerializedName("id") val id: Int,
    @SerializedName("group") val group: Int,
    @SerializedName("author") val author: Int,
    @SerializedName("text") val text: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("location_lat") val location_lat: Double?,
    @SerializedName("location_lng") val location_lng: Double?,
    @SerializedName("created_at") val created_at: String?
)
data class Comment(
    @SerializedName("id") val id: Int,
    @SerializedName("post") val post: Int,
    @SerializedName("author") val author: Int,
    @SerializedName("text") val text: String?,
    @SerializedName("created_at") val created_at: String?
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
data class Quiz(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?
)
data class QuizQuestion(
    @SerializedName("id") val id: Int,
    @SerializedName("quiz") val quiz: Int,
    @SerializedName("question_text") val question_text: String?,
    @SerializedName("correct_answer") val correct_answer: String?,
    @SerializedName("wrong_answers") val wrong_answers: List<String>?
)
data class QuizResult(
    @SerializedName("id") val id: Int,
    @SerializedName("quiz") val quiz: Int,
    @SerializedName("user") val user: Int,
    @SerializedName("score") val score: Int,
    @SerializedName("completed_at") val completed_at: String?
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
    private const val BASE_URL = "https://3cbd-45-13-91-6.ngrok-free.app/"

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



