package musicpractice.com.coeeter.clicktoeat.data.repositories

import musicpractice.com.coeeter.clicktoeat.data.network.services.RestaurantService

class RestaurantRepository(private val restaurantService: RestaurantService) {

    suspend fun getRestaurantList() =
        restaurantService.getRestaurants()

}