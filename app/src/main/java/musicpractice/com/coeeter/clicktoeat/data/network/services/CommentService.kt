<<<<<<< HEAD:app/src/main/java/musicpractice/com/coeeter/clicktoeat/data/api/CommentService.kt
package musicpractice.com.coeeter.clicktoeat.data.api

import musicpractice.com.coeeter.clicktoeat.data.models.Comment
import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponse
import retrofit2.Response
=======
package musicpractice.com.coeeter.clicktoeat.data.network.services

import musicpractice.com.coeeter.clicktoeat.data.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponseModel
import retrofit2.Call
>>>>>>> master:app/src/main/java/musicpractice/com/coeeter/clicktoeat/data/network/services/CommentService.kt
import retrofit2.http.*

interface CommentService {
    //get all comments
    @GET("comments?d=mobile")
    suspend fun getAllComments(): Response<ArrayList<Comment>>

    //create Comment
    @FormUrlEncoded
    @POST("comments/{token}")
    suspend fun createComment(
        @Path("token") token: String,
        @Field("restaurantId") restaurantId: Int,
        @Field("restaurant") restaurant: String,
        @Field("review") review: String,
        @Field("rating") rating: Int
    ): Response<DefaultResponse>

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
    ): Response<DefaultResponse>

    //delete Comment
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "comments/{token}", hasBody = true)
    suspend fun deleteComment(
        @Path("token") token: String,
        @Field("id") commentId: Int
    ): Response<DefaultResponse>
}