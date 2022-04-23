package musicpractice.com.coeeter.clicktoeat.activities

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivitySignUpBinding
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.utils.getFile
import musicpractice.com.coeeter.clicktoeat.utils.getFileType
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpActivity : AppCompatActivity() {
    private val requestCodeToGetImage = 10
    private var profileUri: Uri? = null

    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            0
        )

        findViewById<RadioButton>(R.id.male).isChecked = true

        binding.profileImage.setOnClickListener {
            LoginActivity.hideKeyboard(this)
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, requestCodeToGetImage)
        }

        binding.submitBtn.setOnClickListener {
            LoginActivity.hideKeyboard(this)

            for (input in arrayOf(
                binding.name,
                binding.username,
                binding.password,
                binding.confirmPassword,
                binding.email,
                binding.phoneNum,
                binding.address
            )) {
                input.clearFocus()
            }

            val name = binding.name.text.toString()
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val email = binding.email.text.toString()
            val phoneNum = binding.phoneNum.text.toString()
            val address = binding.address.text.toString()
            var gender = ""
            when (binding.gender.checkedRadioButtonId) {
                R.id.male -> gender = "M"
                R.id.female -> gender = "F"
            }

            for (item in arrayOf(
                name,
                username,
                password,
                confirmPassword,
                email,
                phoneNum,
                address
            )) {
                if (item.isEmpty()) {
                    LoginActivity.animateErrorView(
                        this,
                        binding.error,
                        R.anim.slide_down,
                        View.VISIBLE,
                        "Empty Field.\nPlease fill up " +
                                "the fields below to register",
                        binding.parent
                    )
                    return@setOnClickListener
                }
            }

            val nameArray = name.split(" ")

            val firstName = nameArray[0]
            val lastName =
                if (nameArray.size >= 2)
                    nameArray
                        .slice(1 until nameArray.size)
                        .joinToString(" ")
                else ""

            if (password != confirmPassword) {
                LoginActivity.animateErrorView(
                    this,
                    binding.error,
                    R.anim.slide_down,
                    View.VISIBLE,
                    "Wrong confirm password " +
                            "entered.\nPlease input correct password",
                    binding.parent
                )
                return@setOnClickListener
            }

            var uploadFile: MultipartBody.Part? = null
            if (profileUri != null) {
                val imageFile = profileUri!!.getFile(contentResolver)

                val requestFile = RequestBody.create(
                    MediaType.parse(profileUri!!.getFileType(contentResolver)),
                    imageFile
                )
                uploadFile =
                    MultipartBody.Part.createFormData("uploadFile", imageFile.name, requestFile)
            }

            RetrofitClient.userService.createUser(
                createRequestBody(username),
                createRequestBody(password),
                createRequestBody(email),
                createRequestBody(phoneNum),
                createRequestBody(firstName),
                createRequestBody(lastName),
                createRequestBody(gender),
                createRequestBody(address),
                uploadFile
            ).enqueue(object : Callback<DefaultResponseModel?> {
                override fun onResponse(
                    call: Call<DefaultResponseModel?>,
                    response: Response<DefaultResponseModel?>
                ) {
                    if (response.body()!!.result != null) {
                        LoginActivity.animateErrorView(
                            this@SignUpActivity,
                            binding.error,
                            R.anim.slide_down,
                            View.VISIBLE,
                            response.body()!!.result!!,
                            binding.parent
                        )
                        return
                    }
                    if (response.body()!!.affectedRows != null && response.body()!!.affectedRows == 1) {
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        intent.putExtra("username", username)
                        val options = ActivityOptions
                            .makeSceneTransitionAnimation(
                                this@SignUpActivity,
                                Pair.create(binding.brand, "brand"),
                                Pair.create(binding.form, "field"),
                                Pair.create(binding.submitBtn, "button")
                            )
                        startActivity(intent, options.toBundle())
                        finish()
                        return
                    }
                }

                override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                    Log.d("poly", t.message.toString())
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeToGetImage && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            binding.profileImage.setImageURI(data.data)
            profileUri = data.data
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }

    private fun createRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value)
    }
}