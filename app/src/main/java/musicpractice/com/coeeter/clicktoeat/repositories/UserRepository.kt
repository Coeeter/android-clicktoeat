package musicpractice.com.coeeter.clicktoeat.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import musicpractice.com.coeeter.clicktoeat.data.api.UserService
import musicpractice.com.coeeter.clicktoeat.data.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userService: UserService) {

    private val _userList = MutableLiveData<ArrayList<User>>()
    val userList: LiveData<ArrayList<User>>
        get() = _userList

    suspend fun getAllUsers() {
        withContext(Dispatchers.IO) {
            val response = userService.getAllUsers()
            if (response.body() == null) return@withContext
            _userList.postValue(response.body()!!)
        }
    }

    suspend fun getProfile(token: String) = userService.getProfile(token)

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
    ) = withContext(Dispatchers.IO) {
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
    }

    suspend fun updateUser(
        token: String,
        fieldsToBeUpdated: Map<String, RequestBody>,
        uploadImage: MultipartBody.Part? = null
    ) = withContext(Dispatchers.IO) {
        userService.updateUser(
            token,
            fieldsToBeUpdated,
            uploadImage
        )
    }

    suspend fun deleteUser(token: String) =
        withContext(Dispatchers.IO) { userService.deleteUser(token) }

    suspend fun login(username: String, password: String) =
        withContext(Dispatchers.IO) { userService.login(username, password) }

    suspend fun forgotPassword(email: String) =
        withContext(Dispatchers.IO) { userService.forgotPassword(email) }

}