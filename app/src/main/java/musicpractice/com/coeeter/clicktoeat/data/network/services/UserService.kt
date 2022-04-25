package musicpractice.com.coeeter.clicktoeat.data.network.services

import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.data.models.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    //get All users
    @GET("users")
    fun getAllUsers(): Call<ArrayList<UserModel>>

    //get profile of logged in user
    @GET("users/{token}")
    fun getProfile(@Path("token") token: String): Call<ArrayList<UserModel>>

    //create user
    @Multipart
    @POST("users?d=mobile")
    fun createUser(
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phoneNum") phoneNum: RequestBody,
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("address") address: RequestBody,
        @Part uploadImage: MultipartBody.Part? = null
    ): Call<DefaultResponseModel>

    //update user
    @Multipart
    @POST("updateUsers/{token}")
    fun updateUser(
        @Path("token") token: String,
        @PartMap updateParts: Map<String, RequestBody>,
        @Part uploadImage: MultipartBody.Part? = null
    ): Call<DefaultResponseModel>

    @DELETE("users/{token}")
    fun deleteUser(@Path("token") token: String): Call<DefaultResponseModel>

    //login user
    @FormUrlEncoded
    @POST("users/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<DefaultResponseModel>

    //forget Password
    @FormUrlEncoded
    @POST("users/forgotPassword")
    fun forgotPassword(@Field("email") email: String): Call<DefaultResponseModel>

}