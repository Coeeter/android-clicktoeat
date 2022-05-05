package musicpractice.com.coeeter.clicktoeat.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.repositories.UserRepository
import musicpractice.com.coeeter.clicktoeat.utils.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    var username: String? = null
    var password: String? = null
    var confirmPassword: String? = null
    var email: String? = null
    var phoneNum: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var address: String? = null
    var image: Uri? = null
    var gender: String? = null

    var authViewModelListener: AuthViewModelListener? = null

    fun onLoginButtonClicked(view: View) {
        (view.context as Activity).hideKeyboard()
        authViewModelListener?.onStarted()
        if (username.isNullOrEmpty()) return run {
            authViewModelListener?.onFailure("Username Required")
        }

        if (password.isNullOrEmpty()) return run {
            authViewModelListener?.onFailure("Password Required")
        }

        Coroutine.main {
            val response = repository.loginUser(username!!, password!!)
            if (response.body()!!.result?.contains("Invalid") == true)
                return@main authViewModelListener?.onFailure(response.body()!!.result!!)!!
            val editor = view.context.getSharedPreferences("memory", Context.MODE_PRIVATE).edit()
            editor.putString("username", username).apply()
            editor.putString("token", response.body()!!.result).apply()
            authViewModelListener?.onSuccess(response.body()!!.result!!)
        }
    }

    fun onForgetPasswordButtonClicked(view: View) {
        (view.context as Activity).hideKeyboard()
        authViewModelListener?.onStarted()
        if (email.isNullOrEmpty()) return run {
            authViewModelListener?.onFailure("Email Required")
        }

        Coroutine.main {
            val response = repository.forgetPassword(email!!)
            if (response.body()!!.accepted == null)
                return@main authViewModelListener?.onFailure("Invalid Email")!!
            authViewModelListener?.onSuccess(
                "Sent password reset link to \n" +
                        "${response.body()!!.accepted?.get(0)}"
            )
        }
    }

    fun onSignUpButtonClicked(view: View) {
        (view.context as Activity).hideKeyboard()
        authViewModelListener?.onStarted()
        val fieldList = listOf<String?>(
            firstName,
            lastName,
            username,
            password,
            email,
            phoneNum,
            address,
            gender
        )
        val fieldNameList = listOf<String>(
            "First Name",
            "Last Name",
            "Username",
            "Password",
            "Email",
            "Phone number",
            "Address",
            "Gender"
        )
        for (i in fieldList.indices) {
            if (fieldList[i].isNullOrEmpty()) return run {
                authViewModelListener?.onFailure("${fieldNameList[i]} required")
            }
        }
        if (password != confirmPassword) return run {
            authViewModelListener?.onFailure("Passwords do not match")
        }

        Coroutine.main {
            var uploadImage: MultipartBody.Part? = null
            if (image != null) {
                val imageFile = image!!.getFile(view.context.contentResolver)
                val requestFile = RequestBody.create(
                    MediaType.parse(image!!.getFileType(view.context.contentResolver)),
                    imageFile
                )
                uploadImage = MultipartBody.Part.createFormData(
                    "uploadFile",
                    imageFile.name,
                    requestFile
                )
            }
            val response = repository.createUser(
                username!!.createMultipartFormData(),
                password!!.createMultipartFormData(),
                email!!.createMultipartFormData(),
                phoneNum!!.createMultipartFormData(),
                firstName!!.createMultipartFormData(),
                lastName!!.createMultipartFormData(),
                gender!!.createMultipartFormData(),
                address!!.createMultipartFormData(),
                uploadImage
            )
            if (response.body()!!.result != null) return@main run {
                authViewModelListener?.onFailure(response.body()!!.result!!)
            }
            if (response.body()!!.affectedRows == 1)
                authViewModelListener?.onSuccess("Created account with username $username")
        }

    }

    fun goToForgetPassword(view: View) {
        Intent(view.context, ForgetPasswordActivity::class.java).let {
            view.context.apply {
                startActivity(it)
                (this as AppCompatActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.stay_still
                )
            }
        }
    }

    fun goToSignUpPage(view: View) {
        Intent(view.context, SignUpActivity::class.java).let {
            view.context.apply {
                startActivity(it)
                (this as AppCompatActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.stay_still
                )
            }
        }
    }

    fun onGenderClicked(radioGroup: RadioGroup, id: Int) {
        when (radioGroup.checkedRadioButtonId) {
            R.id.male -> gender = "M"
            R.id.female -> gender = "F"
        }
    }

    fun getImage(view: View) {
        (view.context as Activity).run {
            hideKeyboard()
            startActivityForResult(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ), SignUpActivity.IMAGE_CODE
            )
        }
    }

    interface AuthViewModelListener {
        fun onStarted()
        fun onSuccess(result: String)
        fun onFailure(message: String)
    }
}