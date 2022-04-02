package musicpractice.com.coeeter.clicktoeat.apiClient.services

import musicpractice.com.coeeter.clicktoeat.apiClient.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.apiClient.models.DefaultResponseModel
import retrofit2.Call
import retrofit2.http.*

interface CommentService {
    //get all comments
    @GET("comments")
    fun getAllComments(): Call<ArrayList<CommentModel>>

    //create Comment
    @FormUrlEncoded
    @POST("comments/{token}")
    fun createComment(
        @Path("token") token: String,
        @Field("restaurantId") restaurantId: Int,
        @Field("restaurant") restaurant: String,
        @Field("review") review: String,
        @Field("rating") rating: Int
    ): Call<DefaultResponseModel>

    //update Comment
    @FormUrlEncoded
    @HTTP(method = "PUT", path = "comments/{token}", hasBody = true)
    fun updateComment(
        @Path("token") token: String,
        @Field("id") commentId: Int,
        @Field("restaurantId") restaurantId: Int,
        @Field("restaurant") restaurant: String,
        @Field("review") review: String,
        @Field("rating") rating: Int
    ): Call<DefaultResponseModel>

    //delete Comment
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "comments/{token}", hasBody = true)
    fun deleteComment(
        @Path("token") token: String,
        @Field("id") commentId: Int
    ): Call<DefaultResponseModel>
}