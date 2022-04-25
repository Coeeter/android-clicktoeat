package musicpractice.com.coeeter.clicktoeat.repositories

import musicpractice.com.coeeter.clicktoeat.daos.RestaurantDao

class RestaurantRepository private constructor(private val restaurantDao: RestaurantDao) {

    fun getAllRestaurants() {
        restaurantDao.getAllRestaurants()
    }

    fun getRestaurantList() = restaurantDao.getRestaurantList()

    companion object {
        @Volatile
        private var instance: RestaurantRepository? = null

        fun getInstance(restaurantDao: RestaurantDao) = instance ?: synchronized(this) {
            instance ?: RestaurantRepository(restaurantDao).also { instance = it }
        }
    }
}