package musicpractice.com.coeeter.clicktoeat.ui.auth

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import musicpractice.com.coeeter.clicktoeat.data.models.User
import musicpractice.com.coeeter.clicktoeat.repositories.UserRepository
import musicpractice.com.coeeter.clicktoeat.utils.createFormData
import musicpractice.com.coeeter.clicktoeat.utils.getFile
import musicpractice.com.coeeter.clicktoeat.utils.getFileType
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _invalidLoginMessage = MutableLiveData<String>()
    val invalidLoginMessage: LiveData<String>
        get() = _invalidLoginMessage

    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    private val _profile = MutableLiveData<User?>()
    val profile: LiveData<User?>
        get() = _profile

    private val _failedToSendEmailToUser = MutableLiveData<String>()
    val failedToSendEmailToUser: LiveData<String>
        get() = _failedToSendEmailToUser

    private val _emailSent = MutableLiveData<String>()
    val emailSent: LiveData<String>
        get() = _emailSent

    private val _failedToCreateUser = MutableLiveData<String>()
    val failedToCreateUser: LiveData<String>
        get() = _failedToCreateUser

    private val _createdUserSuccessfully = MutableLiveData<String>()
    val createdUserSuccessfully: LiveData<String>
        get() = _createdUserSuccessfully

    fun loginUser(username: String, password: String) {
        if (!validateLoginFields(username, password)) return
        viewModelScope.launch {
            val loginResult = userRepository.login(username, password)
            if (loginResult.body() == null || loginResult.body()!!.result == null) return@launch
            val result = loginResult.body()!!.result!!
            if (result.contains("Invalid"))
                return@launch _invalidLoginMessage.postValue(result)
            _token.postValue(result)
        }
    }

    fun getProfile(token: String) {
        viewModelScope.launch {
            val profile = userRepository.getProfile(token)
            if (profile.body() == null || profile.body()!!.size == 0)
                return@launch _profile.postValue(
                    null
                )
            _profile.postValue(profile.body()!![0])
        }
    }

    fun sendPasswordResetLink(email: String) {
        if (email.isEmpty())
            return _failedToSendEmailToUser.postValue("Email required")
        viewModelScope.launch {
            val result = userRepository.forgotPassword(email)
            if (result.body() == null ||
                result.body()!!.accepted == null ||
                result.body()!!.accepted!!.isEmpty() ||
                result.body()!!.result != null
            ) return@launch _failedToSendEmailToUser.postValue(result.body()!!.result!!)
            _emailSent.postValue(result.body()!!.accepted!![0])
        }
    }

    fun createUserAccount(
        username: String,
        password: String,
        confirmPassword: String,
        email: String,
        phoneNum: String,
        firstName: String,
        lastName: String,
        gender: String,
        address: String,
        imageUri: Uri? = null
    ) {
        if (!validateSignUpFields(
                username,
                password,
                confirmPassword,
                email,
                phoneNum,
                firstName,
                lastName,
                address
            )
        ) return
        val imageFile = imageUri?.getFile(context.contentResolver)
        val requestFile = imageUri?.let {
            RequestBody.create(
                MediaType.parse(it.getFileType(context.contentResolver)),
                imageFile!!
            )
        }
        val uploadFile = requestFile?.let {
            MultipartBody.Part.createFormData(
                "uploadFile",
                imageFile?.name,
                requestFile
            )
        }
        viewModelScope.launch {
            val response = userRepository.createUser(
                username.createFormData(),
                password.createFormData(),
                email.createFormData(),
                phoneNum.createFormData(),
                firstName.createFormData(),
                lastName.createFormData(),
                gender.createFormData(),
                address.createFormData(),
                uploadFile
            )
            if (response.body() == null ||
                response.body()!!.affectedRows != 1 ||
                response.body()!!.result != null
            ) return@launch _failedToCreateUser.postValue(response.body()!!.result!!)
            _createdUserSuccessfully.postValue(username)
        }
    }

    private fun validateSignUpFields(
        username: String,
        password: String,
        confirmPassword: String,
        email: String,
        phoneNum: String,
        firstName: String,
        lastName: String,
        address: String
    ): Boolean {
        val fieldArray = arrayOf(
            username,
            password,
            email,
            phoneNum,
            firstName,
            lastName,
            address
        )
        val cleanedUpArray = arrayOf(
            "Username",
            "Password",
            "Email",
            "Phone number",
            "First name",
            "Last name",
            "Address"
        )
        for (i in fieldArray.indices) {
            if (fieldArray[i].isEmpty()) {
                _failedToCreateUser.postValue("${cleanedUpArray[i]} required")
                return false
            }
        }
        if (password != confirmPassword) {
            _failedToCreateUser.postValue("Passwords do not match")
            return false
        }
        return true
    }

    private fun validateLoginFields(username: String, password: String): Boolean {
        if (username.isEmpty()) return run {
            _invalidLoginMessage.postValue("Username required")
            false
        }

        if (password.isEmpty()) return run {
            _invalidLoginMessage.postValue("Password required")
            false
        }

        return true
    }

}