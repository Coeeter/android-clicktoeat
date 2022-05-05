package musicpractice.com.coeeter.clicktoeat.data.repositories

import musicpractice.com.coeeter.clicktoeat.data.network.services.UserService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(private val userService: UserService) {
    suspend fun loginUser(username: String, password: String) =
        userService.login(username, password)

    suspend fun forgetPassword(email: String) =
        userService.forgotPassword(email)

    suspend fun createUser(
        username: RequestBody,
        password: RequestBody,
        email: RequestBody,
        phoneNum: RequestBody,
        firstName: RequestBody,
        lastName: RequestBody,
        gender: RequestBody,
        address: RequestBody,
        uploadImage: MultipartBody.Part? = null
    ) =
        userService.createUser(
            username,
            password,
            email,
            phoneNum,
            firstName,
            lastName,
            gender,
            address,
            uploadImage
        )

    suspend fun getProfile(token: String) =
        userService.getProfile(token)
}