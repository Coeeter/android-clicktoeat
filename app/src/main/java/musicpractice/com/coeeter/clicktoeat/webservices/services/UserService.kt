package musicpractice.com.coeeter.clicktoeat.webservices.services

import musicpractice.com.coeeter.clicktoeat.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.models.UserModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    //get All users
    @GET("users")
    fun getAllUsers(): Call<ArrayList<UserModel>>

    //get profile of logged in user
    @GET("users/{token}")
    fun getProfile(@Path("token") token: String): Call<UserModel>

    //create user
    @POST("users?d=mobile")
    fun createUser(@Body requestBody: RequestBody): Call<DefaultResponseModel>

    //update user
    @POST("updateUsers/{token}")
    fun updateUser(@Path("token") token: String, @Body requestBody: RequestBody): Call<DefaultResponseModel>

    @DELETE("users/{token}")
    fun deleteUser(@Path("token") token: String): Call<DefaultResponseModel>

    //login user
    @POST("users/login")
    fun login(@Body requestBody: RequestBody): Call<DefaultResponseModel>

    //forget Password
    @POST("users/forgotPassword")
    fun forgotPassword(@Body requestBody: RequestBody): Call<DefaultResponseModel>
}