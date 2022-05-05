package musicpractice.com.coeeter.clicktoeat.data.network.services

import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.data.models.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    //get All users
    @GET("users")
    suspend fun getAllUsers(): Response<ArrayList<UserModel>>

    //get profile of logged in user
    @GET("users/{token}")
    suspend fun getProfile(@Path("token") token: String): Response<ArrayList<UserModel>>

    //create user
    @Multipart
    @POST("users?d=mobile")
    suspend fun createUser(
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phoneNum") phoneNum: RequestBody,
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("address") address: RequestBody,
        @Part uploadImage: MultipartBody.Part? = null
    ): Response<DefaultResponseModel>

    //update user
    @Multipart
    @POST("updateUsers/{token}")
    suspend fun updateUser(
        @Path("token") token: String,
        @PartMap updateParts: Map<String, RequestBody>,
        @Part uploadImage: MultipartBody.Part? = null
    ): Response<DefaultResponseModel>

    @DELETE("users/{token}")
    suspend fun deleteUser(@Path("token") token: String): Response<DefaultResponseModel>

    //login user
    @FormUrlEncoded
    @POST("users/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<DefaultResponseModel>

    //forget Password
    @FormUrlEncoded
    @POST("users/forgotPassword")
    suspend fun forgotPassword(@Field("email") email: String): Response<DefaultResponseModel>

}