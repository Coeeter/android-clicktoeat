package musicpractice.com.coeeter.clicktoeat.data.api

import musicpractice.com.coeeter.clicktoeat.data.models.Restaurant
import retrofit2.Response
import retrofit2.http.GET

interface RestaurantService {
    //get all restaurants
    @GET("restaurants?d=mobile")
    suspend fun getRestaurants(): Response<ArrayList<Restaurant>>
}