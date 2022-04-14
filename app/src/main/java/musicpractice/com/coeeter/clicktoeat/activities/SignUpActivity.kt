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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.DefaultResponseModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class SignUpActivity : AppCompatActivity() {
    private val requestCodeToGetImage = 10
    private var profileUri: Uri? = null
    private lateinit var error: TextView
    private lateinit var submitBtn: Button
    private lateinit var nameInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneNumInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var genderInput: RadioGroup
    private lateinit var parent: ScrollView
    private lateinit var profilePic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            0
        )

        error = findViewById(R.id.error)
        submitBtn = findViewById(R.id.submitBtn)

        nameInput = findViewById(R.id.name)
        usernameInput = findViewById(R.id.username)
        passwordInput = findViewById(R.id.password)
        confirmPasswordInput = findViewById(R.id.confirmPassword)
        emailInput = findViewById(R.id.email)
        phoneNumInput = findViewById(R.id.phoneNum)
        addressInput = findViewById(R.id.address)
        genderInput = findViewById(R.id.gender)
        parent = findViewById(R.id.parent)
        profilePic = findViewById(R.id.profileImage)

        findViewById<RadioButton>(R.id.male).isChecked = true

        profilePic.setOnClickListener {
            LoginActivity.hideKeyboard(this)
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, requestCodeToGetImage)
        }

        submitBtn.setOnClickListener {
            LoginActivity.hideKeyboard(this)

            for (input in arrayOf(
                nameInput,
                usernameInput,
                passwordInput,
                confirmPasswordInput,
                emailInput,
                phoneNumInput,
                addressInput
            )) {
                input.clearFocus()
            }

            val name = nameInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()
            val email = emailInput.text.toString()
            val phoneNum = phoneNumInput.text.toString()
            val address = addressInput.text.toString()
            var gender = ""
            when (genderInput.checkedRadioButtonId) {
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
                        error,
                        R.anim.slide_down,
                        View.VISIBLE,
                        "Empty Field.\nPlease fill up " +
                                "the fields below to register",
                        parent
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
                    error,
                    R.anim.slide_down,
                    View.VISIBLE,
                    "Wrong confirm password " +
                            "entered.\nPlease input correct password",
                    parent
                )
                return@setOnClickListener
            }

            var uploadFile: MultipartBody.Part? = null
            if (profileUri != null) {
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = contentResolver.query(profileUri!!, filePathColumn, null, null, null)
                assert(cursor != null)
                cursor!!.moveToFirst()

                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val imageFile = File(cursor.getString(columnIndex))
                val requestFile = RequestBody.create(
                    MediaType.parse(contentResolver.getType(profileUri!!)!!),
                    imageFile
                )
                uploadFile =
                    MultipartBody.Part.createFormData("uploadFile", imageFile.name, requestFile)
                cursor.close()
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
                            error,
                            R.anim.slide_down,
                            View.VISIBLE,
                            response.body()!!.result!!,
                            parent
                        )
                        return
                    }
                    if (response.body()!!.affectedRows != null && response.body()!!.affectedRows == 1) {
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        intent.putExtra("username", username)
                        val options = ActivityOptions
                            .makeSceneTransitionAnimation(
                                this@SignUpActivity,
                                Pair.create(findViewById(R.id.brand), "brand"),
                                Pair.create(findViewById(R.id.form), "field"),
                                Pair.create(submitBtn, "button")
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
            profilePic.setImageURI(data.data)
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