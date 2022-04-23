package musicpractice.com.coeeter.clicktoeat.repository.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserViewModel {
    private val profile by lazy {
        MutableLiveData<UserModel?>()
    }

    private val userList by lazy {
        MutableLiveData<ArrayList<UserModel>>()
    }

    fun getProfile(token: String): LiveData<UserModel?> {
        RetrofitClient.userService.getProfile(token)
            .enqueue(object : Callback<ArrayList<UserModel>?> {
                override fun onResponse(
                    call: Call<ArrayList<UserModel>?>,
                    response: Response<ArrayList<UserModel>?>
                ) {
                    if (response.body() == null || response.body()!!.size == 0) return profile.postValue(
                        null
                    )
                    profile.postValue(response.body()!![0])
                }

                override fun onFailure(call: Call<ArrayList<UserModel>?>, t: Throwable) {
                    Log.d("poly", t.message.toString())
                }
            })
        return profile
    }

    fun getUserList(): LiveData<ArrayList<UserModel>> {
        RetrofitClient.userService.getAllUsers().enqueue(object : Callback<ArrayList<UserModel>?> {
            override fun onResponse(
                call: Call<ArrayList<UserModel>?>,
                response: Response<ArrayList<UserModel>?>
            ) {
                if (response.body() == null) return
                userList.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<ArrayList<UserModel>?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
        return userList
    }

}