package musicpractice.com.coeeter.clicktoeat.data.network.services

import musicpractice.com.coeeter.clicktoeat.data.models.RestaurantModel
import retrofit2.Call
import retrofit2.http.GET

interface RestaurantService {
    //get all restaurants
    @GET("restaurants?d=mobile")
    fun getRestaurants(): Call<ArrayList<RestaurantModel>>
}