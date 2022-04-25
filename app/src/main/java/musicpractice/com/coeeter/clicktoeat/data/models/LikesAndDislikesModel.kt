package musicpractice.com.coeeter.clicktoeat.data.models

import com.google.gson.annotations.SerializedName

data class LikesAndDislikesModel(
    @SerializedName("_id") val id: Int,
    val username: String,
    val commentId: Int,
    val isLiked: Int,
    val isDisliked: Int
)