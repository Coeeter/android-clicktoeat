package musicpractice.com.coeeter.clicktoeat.repository.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.repository.models.FavoriteModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FavoriteViewModel : ViewModel() {
    private val favoriteList by lazy {
        MutableLiveData<ArrayList<FavoriteModel>>()
    }

    fun getAllFavorites(token: String): LiveData<ArrayList<FavoriteModel>> {
        RetrofitClient.favoriteService.getUserFavorites(token)
            .enqueue(object : Callback<ArrayList<FavoriteModel>?> {
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
        return favoriteList
    }

    fun createFavorite(token: String, restaurantId: Int) {
        RetrofitClient.favoriteService.createFavorites(token, restaurantId).enqueue(object :
            Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null || response.body()!!.affectedRows != 1) return
                getAllFavorites(token)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun deleteFavorite(token: String, favoriteId: Int) {
        RetrofitClient.favoriteService.deleteFavorites(token, favoriteId).enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null || response.body()!!.affectedRows != 1) return
                getAllFavorites(token)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }
}