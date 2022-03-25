package musicpractice.com.coeeter.clicktoeat.models

import com.google.gson.annotations.SerializedName

data class CommentModel(
    @SerializedName("_id") val id: Int,
    val restaurantId: Int,
    @SerializedName("restaurant") val restaurantName: String,
    val username: String,
    val review: String,
    val datePosted: String,
    val rating: Int
)