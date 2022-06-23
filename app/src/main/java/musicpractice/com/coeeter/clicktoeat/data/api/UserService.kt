package musicpractice.com.coeeter.clicktoeat.data.api

import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponse
import musicpractice.com.coeeter.clicktoeat.data.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    //get All users
    @GET("users")
    suspend fun getAllUsers(): Response<ArrayList<User>>

    //get profile of logged in user
    @GET("users/{token}")
    suspend fun getProfile(@Path("token") token: String): Response<ArrayList<User>>

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
    ): Response<DefaultResponse>

    //update user
    @JvmSuppressWildcards
    @Multipart
    @POST("users/updateUsers/{token}")
    suspend fun updateUser(
        @Path("token") token: String,
        @PartMap updateParts: Map<String, RequestBody>,
        @Part uploadImage: MultipartBody.Part? = null
    ): Response<DefaultResponse>

    @DELETE("users/{token}")
    suspend fun deleteUser(@Path("token") token: String): Response<DefaultResponse>

    //login user
    @FormUrlEncoded
    @POST("users/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<DefaultResponse>

    // login user with token
    @FormUrlEncoded
    @POST("users/login")
    suspend fun loginWithToken(
        @Field("token") token: String,
        @Field("password") password: String
    ): Response<DefaultResponse>

    //forget Password
    @FormUrlEncoded
    @POST("users/forgotPassword")
    suspend fun forgotPassword(@Field("email") email: String): Response<DefaultResponse>

}