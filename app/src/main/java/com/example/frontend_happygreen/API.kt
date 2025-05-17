package com.example.frontend_happygreen
import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    @get:GET("users/")
    val users: Call<List<User?>?>?

    @POST("users/")
    suspend fun createUser(@Body user: User?): Call<User?>?

    @GET("users/{id}/")
    suspend fun getUser(@Path("id") id: Int): User

    @get:GET("groups/")
    val groups: Call<List<Any?>?>?

    @POST("groups/")
    fun createGroup(@Body group: Group?): Call<Group?>?

    @GET("groups/{id}/")
    fun getGroup(@Path("id") id: Int): Call<Group?>?

    @get:GET("memberships/")
    val memberships: Call<List<Membership?>?>?

    @POST("memberships/")
    fun createMembership(@Body membership: Membership?): Call<Membership?>?

    @GET("memberships/{id}/")
    fun getMembership(@Path("id") id: Int): Call<Membership?>?

    @get:GET("posts/")
    val posts: Call<List<Post?>?>?

    @POST("posts/")
    fun createPost(@Body post: Post?): Call<Post?>?

    @GET("posts/{id}/")
    fun getPost(@Path("id") id: Int): Call<Post?>?

    @get:GET("comments/")
    val comments: Call<List<Any?>?>?

    @POST("comments/")
    fun createComment(@Body comment: Comment?): Call<Comment?>?

    @GET("comments/{id}/")
    fun getComment(@Path("id") id: Int): Call<Comment?>?

    @get:GET("scanned-objects/")
    val scannedObjects: Call<List<ScannedObject?>?>?

    @POST("scanned-objects/")
    fun createScannedObject(@Body `object`: ScannedObject?): Call<ScannedObject?>?

    @GET("scanned-objects/{id}/")
    fun getScannedObject(@Path("id") id: Int): Call<ScannedObject?>?

    @get:GET("quizzes/")
    val quizzes: Call<List<Quiz?>?>?

    @POST("quizzes/")
    fun createQuiz(@Body quiz: Quiz?): Call<Quiz?>?

    @GET("quizzes/{id}/")
    fun getQuiz(@Path("id") id: Int): Call<Quiz?>?

    @get:GET("badges/")
    val badges: Call<List<Badge?>?>?

    @POST("badges/")
    fun createBadge(@Body badge: Badge?): Call<Badge?>?

    @GET("badges/{id}/")
    fun getBadge(@Path("id") id: Int): Call<Badge?>?

    @get:GET("eco-products/")
    val ecoProducts: Call<List<EcoProduct?>?>?

    @POST("eco-products/")
    fun createEcoProduct(@Body product: EcoProduct?): Call<EcoProduct?>?

    @GET("eco-products/{id}/")
    fun getEcoProduct(@Path("id") id: Int): Call<EcoProduct?>?

    @get:GET("waste-classifications/")
    val wasteClassifications: Call<List<WasteClassification?>?>?

    @POST("waste-classifications/")
    fun createWasteClassification(@Body waste: WasteClassification?): Call<WasteClassification?>?

    @GET("waste-classifications/{id}/")
    fun getWasteClassification(@Path("id") id: Int): Call<WasteClassification?>?

    // Token Auth
    @POST("token/")
    fun getToken(@Body tokenRequest: TokenRequest?): Call<TokenResponse?>?

    @POST("token/refresh/")
    fun refreshToken(@Body refreshRequest: RefreshTokenRequest?): Call<TokenResponse?>?





}



class TokenRequest {
    @SerializedName("username")
    var username: String? = null

    @SerializedName("password")
    var password: String? = null
}

class RefreshTokenRequest {
    @SerializedName("refresh")
    var refresh: String? = null
}

class TokenResponse {
    @SerializedName("access")
    var access: String? = null

    @SerializedName("refresh")
    var refresh: String? = null
}



data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String?,
    @SerializedName("profile_pic") val profile_pic: String?,
    @SerializedName("points") val points: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("date_joined") val date_joined: String?
)

class Group {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("created_by")
    var created_by: Int = 0

    @SerializedName("created_at")
    var created_at: String? = null
}

class Membership {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("user")
    var user: Int = 0

    @SerializedName("group")
    var group: Int = 0

    @SerializedName("role")
    var role: String? = null

    @SerializedName("joined_at")
    var joined_at: String? = null
}

class Post {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("group")
    var group: Int = 0

    @SerializedName("author")
    var author: Int = 0

    @SerializedName("text")
    var text: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("location_lat")
    var location_lat: Double? = null

    @SerializedName("location_lng")
    var location_lng: Double? = null

    @SerializedName("created_at")
    var created_at: String? = null
}

class Comment {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("post")
    var post: Int = 0

    @SerializedName("author")
    var author: Int = 0

    @SerializedName("text")
    var text: String? = null

    @SerializedName("created_at")
    var created_at: String? = null
}

class ScannedObject {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("user")
    var user: Int = 0

    @SerializedName("object_type")
    var object_type: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("recognized_at")
    var recognized_at: String? = null

    @SerializedName("location_lat")
    var location_lat: Double? = null

    @SerializedName("location_lng")
    var location_lng: Double? = null
}

class Quiz {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("title")
    var title: String? = null

    @SerializedName("description")
    var description: String? = null
}

class QuizQuestion {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("quiz")
    var quiz: Int = 0

    @SerializedName("question_text")
    var question_text: String? = null

    @SerializedName("correct_answer")
    var correct_answer: String? = null

    @SerializedName("wrong_answers")
    var wrong_answers: List<String>? = null
}

class QuizResult {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("quiz")
    var quiz: Int = 0

    @SerializedName("user")
    var user: Int = 0

    @SerializedName("score")
    var score: Int = 0

    @SerializedName("completed_at")
    var completed_at: String? = null
}

class Badge {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("icon")
    var icon: String? = null
}

class UserBadge {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("user")
    var user: Int = 0

    @SerializedName("badge")
    var badge: Int = 0

    @SerializedName("earned_at")
    var earned_at: String? = null
}

class EcoProduct {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("barcode")
    var barcode: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("brand")
    var brand: String? = null

    @SerializedName("eco_score")
    var eco_score: String? = null

    @SerializedName("sustainable_alt")
    var sustainable_alt: List<String>? = null
}

class WasteClassification {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("material")
    var material: String? = null

    @SerializedName("bin_color")
    var bin_color: String? = null

    @SerializedName("instructions")
    var instructions: String? = null

    @SerializedName("image")
    var image: String? = null
}


// Retrofit instance
object RetrofitInstance {
    // Sostituiscilo con il tuo API KEY
    private const val BASE_URL = "https://3cbd-45-13-91-6.ngrok-free.app/"

//    val api: ApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }

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



