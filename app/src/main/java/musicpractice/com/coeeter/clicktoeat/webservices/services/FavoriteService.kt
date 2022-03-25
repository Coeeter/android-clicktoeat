package musicpractice.com.coeeter.clicktoeat.webservices.services

import musicpractice.com.coeeter.clicktoeat.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.models.FavoriteModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface FavoriteService {
    //get favorites of user
    @GET("favorites/{token}")
    fun getUserFavorites(@Path("token") token: String): Call<ArrayList<FavoriteModel>>

    //create favorite of user
    @POST("favorites/{token}")
    fun createFavorites(@Path("token") token: String, @Body requestBody: RequestBody): Call<DefaultResponseModel>

    //delete favorite of user
    @HTTP(method = "DELETE", path = "favorites/{token}", hasBody = true)
    fun deleteFavorites(@Path("token") token: String, @Body requestBody: RequestBody): Call<DefaultResponseModel>
}
