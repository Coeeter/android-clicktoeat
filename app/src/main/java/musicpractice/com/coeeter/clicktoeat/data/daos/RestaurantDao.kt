package musicpractice.com.coeeter.clicktoeat.data.daos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import musicpractice.com.coeeter.clicktoeat.data.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantDao {
    private val restaurantList = MutableLiveData<ArrayList<RestaurantModel>>()

    fun getAllRestaurants() {
        val response = RetrofitClient.restaurantService.getRestaurants()
        response.enqueue(object : Callback<ArrayList<RestaurantModel>?> {
            override fun onResponse(
                call: Call<ArrayList<RestaurantModel>?>,
                response: Response<ArrayList<RestaurantModel>?>
            ) {
                if (response.body() == null) return
                restaurantList.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<ArrayList<RestaurantModel>?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun getRestaurantList() = restaurantList as LiveData<ArrayList<RestaurantModel>>
}