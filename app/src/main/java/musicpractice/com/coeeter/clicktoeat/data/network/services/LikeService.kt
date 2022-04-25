package musicpractice.com.coeeter.clicktoeat.data.network.services

import musicpractice.com.coeeter.clicktoeat.data.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.data.models.LikesAndDislikesModel
import retrofit2.Call
import retrofit2.http.*

interface LikeService {
    @GET("likeOrDislike")
    fun getAllLikesAndDislikes(): Call<ArrayList<LikesAndDislikesModel>>

    @FormUrlEncoded
    @POST("likeOrDislike/{token}")
    fun createLikesAndDislikes(
        @Path("token") token: String,
        @Field("commentId") commentId: Int,
        @Field("isLiked") isLiked: Int,
        @Field("isDisliked") isDisliked: Int
    ): Call<DefaultResponseModel>

    @FormUrlEncoded
    @HTTP(method = "DElETE", path = "likeOrDislike/{token}", hasBody = true)
    fun deleteLikesAndDislikes(
        @Path("token") token: String,
        @Field("id") likesAndDislikesId: Int
    ): Call<DefaultResponseModel>
}