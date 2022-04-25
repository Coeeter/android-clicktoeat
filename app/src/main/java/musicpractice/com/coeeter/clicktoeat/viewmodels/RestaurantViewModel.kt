package musicpractice.com.coeeter.clicktoeat.viewmodels

import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.repositories.RestaurantRepository

class RestaurantViewModel constructor(private val restaurantRepository: RestaurantRepository) :
    ViewModel() {

    fun getAllRestaurants() = restaurantRepository.getAllRestaurants()
    fun getRestaurantList() = restaurantRepository.getRestaurantList()

}