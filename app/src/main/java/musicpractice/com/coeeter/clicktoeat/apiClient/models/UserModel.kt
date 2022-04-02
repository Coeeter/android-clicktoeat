package musicpractice.com.coeeter.clicktoeat.apiClient.models

data class UserModel(
    val _id: Int,
    val username: String,
    val email: String?,
    val phoneNum: String?,
    val firstName: String,
    val lastName: String?,
    val imagePath: String?,
    val gender: Char?,
    val address: String?
)