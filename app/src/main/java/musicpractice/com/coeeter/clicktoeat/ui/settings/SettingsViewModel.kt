package musicpractice.com.coeeter.clicktoeat.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import musicpractice.com.coeeter.clicktoeat.R
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
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    val token = sharedPreferences.getString(
        context.getString(R.string.sharedPrefToken),
        null
    )!!
    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState>
        get() = _state

    fun loginWithToken(password: String) {
        _state.postValue(SettingsState.Loading)
        viewModelScope.launch {
            val response = userRepository.loginWithToken(
                token,
                password
            )
            if (response.body() == null ||
                response.body()!!.result == null ||
                response.body()!!.result!!.contains("Invalid")
            ) return@launch _state.postValue(SettingsState.Failure(response.body()?.result))
            _state.postValue(SettingsState.LoginSuccess)
        }
    }

    fun deleteAccount() {
        _state.postValue(SettingsState.Loading)
        viewModelScope.launch {
            val response = userRepository.deleteUser(token)
            if (response.body() == null ||
                response.body()!!.result != null ||
                response.body()!!.affectedRows != 1
            ) return@launch _state.postValue(SettingsState.Failure(response.body()?.result))
            _state.postValue(SettingsState.DeleteSuccess)
        }
    }

    fun updatePassword(password: String) {
        _state.postValue(SettingsState.Loading)
        val map = hashMapOf("password" to password.createFormData())
        viewModelScope.launch {
            val response = userRepository.updateUser(token, map)
            if (response.body() == null ||
                response.body()!!.result == null ||
                response.body()!!.result!!.contains("Invalid")
            ) return@launch _state.postValue(SettingsState.Failure(response.body()?.result))
            sharedPreferences.edit().putString(
                context.getString(R.string.sharedPrefToken),
                response.body()!!.result!!
            ).apply()
            _state.postValue(SettingsState.UpdateSuccess)
        }
    }

    fun updateProfile(
        username: String,
        email: String,
        phoneNum: String,
        address: String,
        firstName: String,
        lastName: String,
        gender: String,
        profilePictureUri: Uri?
    ) {
        _state.postValue(SettingsState.Loading)
        val map = hashMapOf(
            "username" to username.createFormData(),
            "email" to email.createFormData(),
            "phoneNum" to phoneNum.createFormData(),
            "address" to address.createFormData(),
            "firstName" to firstName.createFormData(),
            "lastName" to lastName.createFormData(),
            "gender" to gender.createFormData()
        )
        val file = profilePictureUri?.getFile(context.contentResolver)
        val requestFile = profilePictureUri?.let {
            RequestBody.create(
                MediaType.parse(it.getFileType(context.contentResolver)),
                file!!
            )
        }
        val uploadFile = requestFile?.let {
            MultipartBody.Part.createFormData(
                "uploadFile",
                file?.name,
                requestFile
            )
        }
        viewModelScope.launch {
            val response = userRepository.updateUser(token, map, uploadFile)
            if (response.body() == null ||
                response.body()!!.result == null ||
                response.body()!!.result!!.contains("Invalid")
            ) return@launch _state.postValue(SettingsState.Failure(response.body()?.result))
            sharedPreferences.edit().putString(
                context.getString(R.string.sharedPrefToken),
                response.body()!!.result!!
            ).apply()
            _state.postValue(SettingsState.UpdateSuccess)
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            val response = userRepository.getProfile(token)
            if (response.body() == null || response.body()!!.size == 0)
                return@launch _state.postValue(SettingsState.Failure())
            return@launch _state.postValue(SettingsState.Profile(response.body()!![0]))
        }
    }

    sealed class SettingsState {
        object Loading : SettingsState()
        object LoginSuccess : SettingsState()
        object UpdateSuccess : SettingsState()
        object DeleteSuccess : SettingsState()
        class Failure(val message: String? = null) : SettingsState()
        class Profile(val user: User) : SettingsState()
    }
}