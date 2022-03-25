package musicpractice.com.coeeter.clicktoeat.models

import com.google.gson.annotations.SerializedName

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
)