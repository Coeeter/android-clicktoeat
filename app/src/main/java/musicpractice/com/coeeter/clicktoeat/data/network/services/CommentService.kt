package musicpractice.com.coeeter.clicktoeat.data.network.services

import musicpractice.com.coeeter.clicktoeat.data.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponseModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface CommentService {
    //get all comments
    @GET("comments?d=mobile")
    suspend fun getAllComments(): Response<ArrayList<CommentModel>>

    //create Comment
    @FormUrlEncoded
    @POST("comments/{token}")
    suspend fun createComment(
        @Path("token") token: String,
        @Field("restaurantId") restaurantId: Int,
        @Field("restaurant") restaurant: String,
        @Field("review") review: String,
        @Field("rating") rating: Int
    ): Response<DefaultResponseModel>

    //update Comment
    @FormUrlEncoded
    @HTTP(method = "PUT", path = "comments/{token}", hasBody = true)
    suspend fun updateComment(
        @Path("token") token: String,
        @Field("id") commentId: Int,
        @Field("restaurantId") restaurantId: Int,
        @Field("restaurant") restaurant: String,
        @Field("review") review: String,
        @Field("rating") rating: Int
    ): Response<DefaultResponseModel>

    //delete Comment
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "comments/{token}", hasBody = true)
    suspend fun deleteComment(
        @Path("token") token: String,
        @Field("id") commentId: Int
    ): Response<DefaultResponseModel>
}