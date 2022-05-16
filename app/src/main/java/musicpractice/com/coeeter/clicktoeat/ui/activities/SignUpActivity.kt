package musicpractice.com.coeeter.clicktoeat.ui.activities

import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Pair
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivitySignUpBinding
import musicpractice.com.coeeter.clicktoeat.utils.*
import musicpractice.com.coeeter.clicktoeat.data.viewmodels.UserViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody


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
            hideKeyboard()
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, requestCodeToGetImage)
        }

        binding.submitBtn.setOnClickListener {
            hideKeyboard()

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
            val gender = when (binding.gender.checkedRadioButtonId) {
                R.id.male -> "M"
                R.id.female -> "F"
                else -> "O"
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
                    return@setOnClickListener binding.root
                        .createSnackBar("Empty field.\nPlease fill up the fields below to register.")
                }
            }

            val nameArray = name.split(" ")

            val firstName = nameArray[0]
            val lastName = if (nameArray.size >= 2)
                nameArray.slice(1 until nameArray.size).joinToString(" ")
            else ""

            if (password != confirmPassword) {
                return@setOnClickListener binding.root
                    .createSnackBar("Wrong confirm password entered.\nPlease input correct password.")
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

            val viewModel = ViewModelProvider(
                this,
                InjectorUtils.provideUserViewModelFactory()
            )[UserViewModel::class.java]
            viewModel.getCreateUserResult().observe(this, Observer {
                if (it.result != null) return@Observer binding.submitBtn.createSnackBar(it.result)

                if (it.affectedRows != null && it.affectedRows == 1) {
                    val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                    intent.putExtra("username", username)
                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        this@SignUpActivity,
                        Pair.create(binding.brand, "brand"),
                        Pair.create(binding.form, "field"),
                        Pair.create(binding.submitBtn, "button")
                    )
                    startActivity(intent, options.toBundle())
                    finish()
                }
            })
            viewModel.addUser(
                username,
                password,
                email,
                phoneNum,
                firstName,
                lastName,
                gender,
                address,
                uploadFile
            )

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
}