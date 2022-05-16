package musicpractice.com.coeeter.clicktoeat.data.models

import com.google.gson.annotations.SerializedName

data class LikeOrDislike(
    @SerializedName("_id") val id: Int? = null,
    val username: String? = null,
    val commentId: Int,
    val isLiked: Int,
    val isDisliked: Int
)