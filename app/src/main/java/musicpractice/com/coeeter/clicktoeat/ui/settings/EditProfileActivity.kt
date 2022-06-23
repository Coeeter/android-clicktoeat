package musicpractice.com.coeeter.clicktoeat.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.models.User
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityEditProfileBinding
import musicpractice.com.coeeter.clicktoeat.ui.auth.LoginActivity
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var inputArray: Array<EditText> = arrayOf()
    private var profileUri: Uri? = null

    companion object {
        const val ACTION_PICK_IMAGE = 23
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.apply {
            title = intent.getStringExtra("form")
            setNavigationOnClickListener { finish() }
        }
        setUpListeners()
        when (intent.getStringExtra("form")) {
            "Edit Profile" -> {
                binding.editProfile.visibility = View.VISIBLE
                setUpEditProfileForm()
            }
            "Change Password" -> {
                binding.changePassword.visibility = View.VISIBLE
                setUpChangePasswordForm()
            }
            "Delete Account" -> {
                binding.deleteAccount.visibility = View.VISIBLE
                setUpDeleteAccountForm()
            }
        }
    }

    private fun setUpListeners() {
        settingsViewModel.state.observe(this) {
            when (it) {
                is SettingsViewModel.SettingsState.Loading ->
                    binding.progress.visibility = View.VISIBLE
                is SettingsViewModel.SettingsState.LoginSuccess -> {
                    hideLoading()
                    when (intent.getStringExtra("form")) {
                        "Change Password" -> changePassword()
                        "Delete Account" -> settingsViewModel.deleteAccount()
                    }
                }
                is SettingsViewModel.SettingsState.DeleteSuccess -> {
                    hideLoading()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                is SettingsViewModel.SettingsState.Failure -> {
                    hideLoading()
                    binding.root.createSnackBar(it.message ?: "Unknown error has occurred")
                }
                is SettingsViewModel.SettingsState.UpdateSuccess -> {
                    hideLoading()
                    inputArray.forEach { input -> input.setText("") }
                    if (intent.getStringExtra("form") == "Edit Profile")
                        binding.profileImage.setImageResource(R.drawable.ic_person)
                    binding.root.createSnackBar(
                        when (intent.getStringExtra("form")) {
                            "Change Password" -> "Password Updated Successfully!"
                            "Edit Profile" -> "Profile Updated Successfully!"
                            else -> ""
                        }
                    )
                    Handler(mainLooper).postDelayed({ finish() }, 3000)
                }
                is SettingsViewModel.SettingsState.Profile -> {
                    populateData(it.user)
                }
            }
        }
    }

    private fun populateData(user: User) {
        binding.username.setText(user.username)
        binding.email.setText(user.email)
        binding.phoneNum.setText(user.phoneNum)
        binding.address.setText(user.address)
        binding.firstName.setText(user.firstName)
        binding.lastName.setText(user.lastName)
        when (user.gender) {
            'M' -> binding.male.isChecked = true
            'F' -> binding.female.isChecked = true
        }
        Picasso.with(this)
            .load("${getString(R.string.base_url)}upload/${user.imagePath}")
            .into(binding.profileImage)
    }

    private fun hideLoading() {
        binding.progress.visibility = View.GONE
    }

    private fun setUpEditProfileForm() {
        settingsViewModel.getProfile()
        binding.profileImage.setOnClickListener {
            startActivityForResult(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ),
                ACTION_PICK_IMAGE
            )
        }
        binding.editProfileBtn.setOnClickListener {
            hideKeyboard()
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val phoneNum = binding.phoneNum.text.toString()
            val address = binding.address.text.toString()
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val gender = when (binding.gender.checkedRadioButtonId) {
                R.id.male -> "M"
                R.id.female -> "F"
                else -> "O"
            }
            binding.apply {
                inputArray = arrayOf(
                    this.username,
                    this.email,
                    this.phoneNum,
                    this.address,
                    this.firstName,
                    this.lastName
                )
                inputArray.forEach {
                    if (it.text.toString().isEmpty())
                        return@setOnClickListener it.setError("Field required!")
                }
            }
            settingsViewModel.updateProfile(
                username,
                email,
                phoneNum,
                address,
                firstName,
                lastName,
                gender,
                profileUri
            )
        }
    }

    private fun setUpChangePasswordForm() {
        binding.changePasswordBtn.setOnClickListener {
            hideKeyboard()
            loginWithToken(binding.oldPassword)
        }
    }

    private fun setUpDeleteAccountForm() {
        binding.deleteAccountBtn.setOnClickListener {
            hideKeyboard()
            loginWithToken(binding.password)
        }
    }

    private fun loginWithToken(passwordInput: EditText) {
        val password = passwordInput.text.toString()
        if (password.isEmpty())
            return passwordInput.setError("Password required!")
        settingsViewModel.loginWithToken(password)
    }

    private fun changePassword() {
        val password = binding.newPassword.text.toString()
        val confirmPassword = binding.newPasswordConfirm.text.toString()
        if (password.isEmpty())
            return binding.newPassword.setError("Password required!")
        if (password != confirmPassword)
            return run {
                binding.newPassword.error = "Passwords do not match!"
                binding.newPasswordConfirm.error = "Passwords do not match!"
            }
        inputArray = arrayOf(
            binding.oldPassword,
            binding.newPassword,
            binding.newPasswordConfirm
        )
        settingsViewModel.updatePassword(password)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_PICK_IMAGE &&
            resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            binding.profileImage.setImageURI(data.data)
            profileUri = data.data!!
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}