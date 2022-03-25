package musicpractice.com.coeeter.clicktoeat.webservices.services

import musicpractice.com.coeeter.clicktoeat.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.models.DefaultResponseModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CommentService {
    //get all comments
    @GET("comments")
    fun getAllComments(): Call<ArrayList<CommentModel>>

    //create Comment
    @POST("comments/{token}")
    fun createComment(@Path("token") token:String, @Body requestBody: RequestBody): Call<DefaultResponseModel>

    //update Comment
    @HTTP(method = "PUT", path = "comments/{token}", hasBody = true)
    fun updateComment(@Path("token") token: String, @Body requestBody: RequestBody): Call<DefaultResponseModel>

    //delete Comment
    @HTTP(method = "DELETE", path = "comments/{token}", hasBody = true)
    fun deleteComment(@Path("token") token: String, @Body requestBody: RequestBody): Call<DefaultResponseModel>
}