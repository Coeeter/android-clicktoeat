package musicpractice.com.coeeter.clicktoeat.data.daos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.data.models.FavoriteModel
import musicpractice.com.coeeter.clicktoeat.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteDao {
    private val favoriteList = MutableLiveData<ArrayList<FavoriteModel>>()
    private val createFavoriteResult = MutableLiveData<DefaultResponseModel>()
    private val deleteFavoriteResult = MutableLiveData<DefaultResponseModel>()

    fun getAllFavorites(token: String) {
        val response = RetrofitClient.favoriteService.getUserFavorites(token)
        response.enqueue(object : Callback<ArrayList<FavoriteModel>?> {
            override fun onResponse(
                call: Call<ArrayList<FavoriteModel>?>,
                response: Response<ArrayList<FavoriteModel>?>
            ) {
                if (response.body() == null) return
                favoriteList.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<ArrayList<FavoriteModel>?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun addFavorite(token: String, restaurantId: Int) {
        val response = RetrofitClient.favoriteService.createFavorites(token, restaurantId)
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                createFavoriteResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun removeFavorite(token: String, favoriteId: Int) {
        val response = RetrofitClient.favoriteService.deleteFavorites(token, favoriteId)
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                deleteFavoriteResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun getFavoriteList() = favoriteList as LiveData<ArrayList<FavoriteModel>>
    fun getCreateFavoriteResult() = createFavoriteResult as LiveData<DefaultResponseModel>
    fun getDeleteFavoriteResult() = deleteFavoriteResult as LiveData<DefaultResponseModel>
}