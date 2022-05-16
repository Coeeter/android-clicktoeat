package musicpractice.com.coeeter.clicktoeat.data.models

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("_id") val id: Int? = null,
    val restaurantId: Int,
    @SerializedName("restaurant") val restaurantName: String,
    val username: String? = null,
    val review: String,
    val datePosted: String? = null,
    val rating: Int
)