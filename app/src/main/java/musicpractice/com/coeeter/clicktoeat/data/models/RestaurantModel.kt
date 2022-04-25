package musicpractice.com.coeeter.clicktoeat.data.models

import com.google.gson.annotations.SerializedName
import kotlin.math.*

data class RestaurantModel(
    val _id: Int,
    val name: String,
    val image: String,
    @SerializedName("location-latitude") val latitude: Double,
    @SerializedName("location-longitude") val longitude: Double,
    val description: String,
    @SerializedName("contact-website") val website: String?,
    @SerializedName("contact-socialMedia-Facebook") val facebook: String?,
    @SerializedName("contact-socialMedia-Twitter") val twitter: String?,
    @SerializedName("contact-phoneNum") val phoneNum: String?,
    @SerializedName("monday-openingHours") val mondayOpeningHours: String,
    @SerializedName("monday-closingHours") val mondayClosingHours: String,
    @SerializedName("tuesday-openingHours") val tuesdayOpeningHours: String,
    @SerializedName("tuesday-closingHours") val tuesdayClosingHours: String,
    @SerializedName("wednesday-openingHours") val wednesdayOpeningHours: String,
    @SerializedName("wednesday-closingHours") val wednesdayClosingHours: String,
    @SerializedName("thursday-openingHours") val thursdayOpeningHours: String,
    @SerializedName("thursday-closingHours") val thursdayClosingHours: String,
    @SerializedName("friday-openingHours") val fridayOpeningHours: String,
    @SerializedName("friday-closingHours") val fridayClosingHours: String,
    @SerializedName("saturday-openingHours") val saturdayOpeningHours: String,
    @SerializedName("saturday-closingHours") val saturdayClosingHours: String,
    @SerializedName("sunday-openingHours") val sundayOpeningHours: String,
    @SerializedName("sunday-closingHours") val sundayClosingHours: String,
    val tags: List<String>,
    val address: String
) {
    fun getAvgRatingAndCount(commentList: ArrayList<CommentModel>): HashMap<String, String> {
        var totalRating = 0.0
        var reviewCount = 0.0
        for (comment in commentList) {
            if (comment.restaurantId == this._id) {
                totalRating += comment.rating
                reviewCount++
            }
        }
        var averageRating = 0.0
        if (reviewCount > 0) {
            averageRating += totalRating / reviewCount
        }
        val hashMap = HashMap<String, String>()
        if (averageRating.toString().substring(averageRating.toString().length - 2) == ".0") {
            hashMap["average"] = averageRating.toInt().toString()
        } else {
            hashMap["average"] = ((averageRating * 100).roundToInt() / 100.0).toString()
        }
        hashMap["count"] = "(${reviewCount.toInt()})"
        return hashMap
    }

    fun getDistance(
        userLocation: HashMap<String, Double?>?
    ): String? {
        val userLongitude = userLocation?.get("longitude")
        val userLatitude = userLocation?.get("latitude")
        if (userLongitude == null || userLatitude == null) return null

        val differenceInLatitude =
            this.latitude * (Math.PI / 180) - userLatitude * (Math.PI / 180)
        val differenceInLongitude =
            this.longitude * (Math.PI / 180) - userLongitude * (Math.PI / 180)

        val a = sin(differenceInLatitude / 2).pow(2.0) +
                cos(this.latitude * (Math.PI / 180)) *
                cos(userLatitude * (Math.PI / 180)) *
                sin(differenceInLongitude / 2).pow(2.0)
        val c = 2 * asin(sqrt(a))
        var distance = (c * 6371 * 1000).roundToInt() / 1000.0
        if (distance < 1) {
            distance *= 1000
            return distance.toInt().toString() + "m"
        }
        distance = (distance * 100).roundToInt() / 100.0
        return distance.toString() + "km"
    }

    fun getOpeningAndClosingHours(): ArrayList<String> {
        val openingAndClosingHours = ArrayList<String>()
        openingAndClosingHours.add(
            "${formatTime(mondayOpeningHours)} to ${
                formatTime(
                    mondayClosingHours
                )
            }"
        )
        openingAndClosingHours.add(
            "${formatTime(tuesdayOpeningHours)} to ${
                formatTime(
                    tuesdayClosingHours
                )
            }"
        )
        openingAndClosingHours.add(
            "${formatTime(wednesdayOpeningHours)} to ${
                formatTime(
                    wednesdayClosingHours
                )
            }"
        )
        openingAndClosingHours.add(
            "${formatTime(thursdayOpeningHours)} to ${
                formatTime(
                    thursdayClosingHours
                )
            }"
        )
        openingAndClosingHours.add(
            "${formatTime(fridayOpeningHours)} to ${
                formatTime(
                    fridayClosingHours
                )
            }"
        )
        openingAndClosingHours.add(
            "${formatTime(saturdayOpeningHours)} to ${
                formatTime(
                    saturdayClosingHours
                )
            }"
        )
        openingAndClosingHours.add(
            "${formatTime(sundayOpeningHours)} to ${
                formatTime(
                    sundayClosingHours
                )
            }"
        )
        return openingAndClosingHours
    }

    private fun formatTime(time: String): String {
        val timeList = time.split(":")
        return "${timeList[0].toInt() % 12}:${timeList[1]} ${if (timeList[0].toInt() >= 12) "pm" else "am"}"
    }
}