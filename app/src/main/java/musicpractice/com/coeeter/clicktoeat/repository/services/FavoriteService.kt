package musicpractice.com.coeeter.clicktoeat.repository.services

import musicpractice.com.coeeter.clicktoeat.repository.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.repository.models.FavoriteModel
import retrofit2.Call
import retrofit2.http.*

interface FavoriteService {
    //get favorites of user
    @GET("favorites/{token}")
    fun getUserFavorites(@Path("token") token: String): Call<ArrayList<FavoriteModel>>

    //create favorite of user
    @FormUrlEncoded
    @POST("favorites/{token}")
    fun createFavorites(
        @Path("token") token: String,
        @Field("restaurantId") restaurantId: Int
    ): Call<DefaultResponseModel>

    //delete favorite of user
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "favorites/{token}", hasBody = true)
    fun deleteFavorites(
        @Path("token") token: String,
        @Field("favoriteId") favoriteId: Int
    ): Call<DefaultResponseModel>
}
