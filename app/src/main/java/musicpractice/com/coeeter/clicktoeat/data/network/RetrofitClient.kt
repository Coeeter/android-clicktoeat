package musicpractice.com.coeeter.clicktoeat.data.network

import musicpractice.com.coeeter.clicktoeat.data.network.services.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val baseUrl = "http://10.0.2.2:8080"

    private val retrofitService: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    val restaurantService: RestaurantService by lazy {
        retrofitService.create(RestaurantService::class.java)
    }

    val userService: UserService by lazy {
        retrofitService.create(UserService::class.java)
    }

    val commentService: CommentService by lazy {
        retrofitService.create(CommentService::class.java)
    }

    val favoriteService: FavoriteService by lazy {
        retrofitService.create(FavoriteService::class.java)
    }

    val likeService: LikeService by lazy {
        retrofitService.create(LikeService::class.java)
    }
}