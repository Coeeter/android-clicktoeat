package musicpractice.com.coeeter.clicktoeat.data.viewmodels

import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.data.repositories.RestaurantRepository

class RestaurantViewModel constructor(private val restaurantRepository: RestaurantRepository) :
    ViewModel() {

    fun getAllRestaurants() = restaurantRepository.getAllRestaurants()
    fun getRestaurantList() = restaurantRepository.getRestaurantList()

}