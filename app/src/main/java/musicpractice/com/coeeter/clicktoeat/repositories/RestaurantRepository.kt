package musicpractice.com.coeeter.clicktoeat.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import musicpractice.com.coeeter.clicktoeat.data.api.RestaurantService
import musicpractice.com.coeeter.clicktoeat.data.models.Restaurant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantRepository @Inject constructor(private val restaurantService: RestaurantService) {

    private val _restaurantList = MutableLiveData<ArrayList<Restaurant>>()
    val restaurantList: LiveData<ArrayList<Restaurant>>
        get() = _restaurantList

    suspend fun getAllRestaurants() {
        withContext(Dispatchers.IO){
            val response = restaurantService.getRestaurants()
            if (response.body() == null || response.body()!!.size == 0) return@withContext
            _restaurantList.postValue(response.body()!!)
        }
    }

}