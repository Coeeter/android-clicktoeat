package musicpractice.com.coeeter.clicktoeat.data.daos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.data.models.UserModel
import musicpractice.com.coeeter.clicktoeat.data.network.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDao {
    private val profile = MutableLiveData<UserModel?>()
    private val userList = MutableLiveData<ArrayList<UserModel>>()
    private val createUserResult = MutableLiveData<DefaultResponseModel>()
    private val loginResult = MutableLiveData<DefaultResponseModel>()
    private val removeUserResult = MutableLiveData<DefaultResponseModel>()
    private val forgetPasswordResult = MutableLiveData<DefaultResponseModel>()
    private val updateUserResult = MutableLiveData<DefaultResponseModel>()

    fun getUser(token: String) {
        val response = RetrofitClient.userService.getProfile(token)
        response.enqueue(object : Callback<ArrayList<UserModel>?> {
            override fun onResponse(
                call: Call<ArrayList<UserModel>?>,
                response: Response<ArrayList<UserModel>?>
            ) {
                if (response.body() == null || response.body()!!.size == 0)
                    return profile.postValue(null)
                profile.postValue(response.body()!![0])
            }

            override fun onFailure(call: Call<ArrayList<UserModel>?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun getAllUsers() {
        val response = RetrofitClient.userService.getAllUsers()
        response.enqueue(object : Callback<ArrayList<UserModel>?> {
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
    }

    fun addUser(
        username: RequestBody,
        password: RequestBody,
        email: RequestBody,
        phoneNum: RequestBody,
        firstName: RequestBody,
        lastName: RequestBody,
        gender: RequestBody,
        address: RequestBody,
        image: MultipartBody.Part? = null
    ) {
        val response = RetrofitClient.userService.createUser(
            username,
            password,
            email,
            phoneNum,
            firstName,
            lastName,
            gender,
            address,
            image
        )
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                createUserResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun login(username: String, password: String) {
        val response = RetrofitClient.userService.login(username, password)
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                loginResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun removeUser(token: String) {
        val response = RetrofitClient.userService.deleteUser(token)
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                removeUserResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun forgetPassword(email: String) {
        val response = RetrofitClient.userService.forgotPassword(email)
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                forgetPasswordResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun updateUser(
        token: String,
        fieldsToBeUpdated: Map<String, RequestBody>,
        image: MultipartBody.Part? = null
    ) {
        val response = RetrofitClient.userService.updateUser(
            token,
            fieldsToBeUpdated,
            image
        )
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                updateUserResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun getProfile() = profile as LiveData<UserModel?>
    fun getUserList() = userList as LiveData<ArrayList<UserModel>>
    fun getCreateUserResult() = createUserResult as LiveData<DefaultResponseModel>
    fun getLoginResult() = loginResult as LiveData<DefaultResponseModel>
    fun getRemoveUserResult() = removeUserResult as LiveData<DefaultResponseModel>
    fun getForgetPasswordResult() = forgetPasswordResult as LiveData<DefaultResponseModel>
    fun getUpdateUserResult() = updateUserResult as LiveData<DefaultResponseModel>
}