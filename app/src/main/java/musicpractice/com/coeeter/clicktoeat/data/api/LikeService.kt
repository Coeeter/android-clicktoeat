package musicpractice.com.coeeter.clicktoeat.data.api

import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponse
import musicpractice.com.coeeter.clicktoeat.data.models.LikeOrDislike
import retrofit2.Response
import retrofit2.http.*

interface LikeService {
    @GET("likeOrDislike")
    suspend fun getAllLikesAndDislikes(): Response<ArrayList<LikeOrDislike>>

    @FormUrlEncoded
    @POST("likeOrDislike/{token}")
    suspend fun createLikesAndDislikes(
        @Path("token") token: String,
        @Field("commentId") commentId: Int,
        @Field("isLiked") isLiked: Int,
        @Field("isDisliked") isDisliked: Int
    ): Response<DefaultResponse>

    @DELETE("likeOrDislike/{token}?d=mobile")
    suspend fun deleteLikesAndDislikes(
        @Path("token") token: String,
        @Query("id") likesAndDislikesId: Int
    ): Response<DefaultResponse>
}