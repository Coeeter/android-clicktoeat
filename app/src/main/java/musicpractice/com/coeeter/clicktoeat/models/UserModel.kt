package musicpractice.com.coeeter.clicktoeat.models

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