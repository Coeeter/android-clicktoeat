package musicpractice.com.coeeter.clicktoeat.viewmodels

import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.repositories.UserRepository
import okhttp3.MultipartBody

class UserViewModel constructor(private val userRepository: UserRepository) : ViewModel() {
    fun getUser(token: String) = userRepository.getUser(token)
    fun getAllUsers() = userRepository.getAllUsers()
    fun addUser(
        username: String,
        password: String,
        email: String,
        phoneNum: String,
        firstName: String,
        lastName: String,
        gender: String,
        address: String,
        image: MultipartBody.Part? = null
    ) = userRepository.addUser(
        username,
        password,
        email,
        phoneNum,
        firstName,
        lastName,
        gender,
        address,
        image
    )

    fun login(username: String, password: String) = userRepository.login(username, password)
    fun removeUser(token: String) = userRepository.removeUser(token)
    fun forgetPassword(email: String) = userRepository.forgetPassword(email)
    fun updateUser(
        token: String,
        image: MultipartBody.Part? = null,
        fieldsToBeUpdated: ArrayList<ArrayList<String>>
    ) = userRepository.updateUser(token, fieldsToBeUpdated, image)

    fun getProfile() = userRepository.getProfile()
    fun getUserList() = userRepository.getUserList()
    fun getCreateUserResult() = userRepository.getCreateUserResult()
    fun getLoginResult() = userRepository.getLoginResult()
    fun getRemoveUserResult() = userRepository.getRemoveUserResult()
    fun getForgetPasswordResult() = userRepository.getForgetPasswordResult()
    fun getUpdateUserResult() = userRepository.getUpdateUserResult()
}