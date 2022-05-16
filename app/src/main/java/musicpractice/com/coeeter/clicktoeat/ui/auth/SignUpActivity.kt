package musicpractice.com.coeeter.clicktoeat.ui.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivitySignUpBinding
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import musicpractice.com.coeeter.clicktoeat.utils.showKeyboard

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var profileUri: Uri? = null
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.male.isChecked = true
        requestPermission()
        setUpListeners()
    }

    private fun setUpListeners() {
        authViewModel.apply {
            createdUserSuccessfully.observe(this@SignUpActivity) {
                Snackbar.make(
                    binding.root,
                    "Created account with username $it",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setAction("OKAY") { dismiss() }
                    addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                        override fun onShown(transientBottomBar: Snackbar?) {
                            super.onShown(transientBottomBar)
                            Handler(mainLooper).postDelayed({
                                if (!this@SignUpActivity.isFinishing ||
                                    !this@SignUpActivity.isDestroyed
                                ) finish()
                            }, 3000)
                        }
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            finish()
                        }
                    })
                }.show()
            }
            failedToCreateUser.observe(this@SignUpActivity) {
                with(it) {
                    when {
                        contains("Username") -> binding.username.run {
                            error = it
                            requestFocus()
                            showKeyboard()
                        }
                        contains("Email") -> binding.email.run {
                            error = it
                            requestFocus()
                            showKeyboard()
                        }
                        contains("Phone number") -> binding.phoneNum.run {
                            error = it
                            requestFocus()
                            showKeyboard()
                        }
                        contains("First name") -> binding.firstName.run {
                            error = it
                            requestFocus()
                            showKeyboard()
                        }
                        contains("Last name") -> binding.lastName.run {
                            error = it
                            requestFocus()
                            showKeyboard()
                        }
                        contains("Address") -> binding.address.run {
                            error = it
                            requestFocus()
                            showKeyboard()
                        }
                        contains("match") -> {
                            binding.confirmPassword.run {
                                error = it
                                requestFocus()
                                showKeyboard()
                            }
                            binding.password.run {
                                error = it
                                showKeyboard()
                            }
                        }
                        contains("Password") -> binding.password.run {
                            error = it
                            requestFocus()
                            showKeyboard()
                        }
                    }
                }
            }
        }
        binding.apply {
            profileImage.setOnClickListener {
                hideKeyboard()
                startActivityForResult(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    ), ON_ACTIVITY_RESULT_REQUEST_CODE
                )
            }
            submitBtn.setOnClickListener {
                hideKeyboard()
                val firstName = binding.firstName.run {
                    clearFocus()
                    text.toString().trim()
                }
                val lastName = binding.lastName.run {
                    clearFocus()
                    text.toString().trim()
                }
                val username = binding.username.run {
                    clearFocus()
                    text.toString().trim()
                }
                val password = binding.password.run {
                    clearFocus()
                    text.toString().trim()
                }
                val confirmPassword = binding.confirmPassword.run {
                    clearFocus()
                    text.toString().trim()
                }
                val email = binding.email.run {
                    clearFocus()
                    text.toString().trim()
                }
                val phoneNum = binding.phoneNum.run {
                    clearFocus()
                    text.toString().trim()
                }
                val address = binding.address.run {
                    clearFocus()
                    text.toString().trim()
                }
                val gender = when (binding.gender.checkedRadioButtonId) {
                    R.id.male -> "M"
                    R.id.female -> "F"
                    else -> "O"
                }
                authViewModel.createUserAccount(
                    username,
                    password,
                    confirmPassword,
                    email,
                    phoneNum,
                    firstName,
                    lastName,
                    gender,
                    address,
                    profileUri
                )
            }
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                0
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ON_ACTIVITY_RESULT_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK &&
            data != null &&
            data.data != null
        ) {
            binding.profileImage.setImageURI(data.data)
            profileUri = data.data
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }

    companion object {
        const val ON_ACTIVITY_RESULT_REQUEST_CODE = 10
    }
}