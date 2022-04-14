package musicpractice.com.coeeter.clicktoeat.repository.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.RestaurantModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RestaurantViewModel : ViewModel() {
    private val restaurantList = MutableLiveData<ArrayList<RestaurantModel>>()

    fun getAllRestaurants(): LiveData<ArrayList<RestaurantModel>> {
        val response = RetrofitClient.restaurantService.getRestaurants()
        response.enqueue(object : Callback<ArrayList<RestaurantModel>?> {
            override fun onResponse(
                call: Call<ArrayList<RestaurantModel>?>,
                response: Response<ArrayList<RestaurantModel>?>
            ) {
                restaurantList.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<ArrayList<RestaurantModel>?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
        return restaurantList
    }
}