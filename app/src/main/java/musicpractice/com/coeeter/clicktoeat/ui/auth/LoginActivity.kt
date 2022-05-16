package musicpractice.com.coeeter.clicktoeat.ui.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityLoginBinding
import musicpractice.com.coeeter.clicktoeat.ui.main.MainActivity
import musicpractice.com.coeeter.clicktoeat.utils.*

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpListeners()
    }

    private fun setUpListeners() {
        authViewModel.invalidLoginMessage.observe(this) {
            if (it.lowercase().contains("username"))
                return@observe binding.username.run {
                    error = it
                    requestFocus()
                    showKeyboard()
                }
            binding.password.run {
                error = it
                requestFocus()
                showKeyboard()
            }
        }

        authViewModel.token.observe(this) {
            saveItemToSharedPref(
                getString(R.string.sharedPrefName),
                getString(R.string.sharedPrefToken),
                it
            )
            authViewModel.getProfile(it)
        }

        authViewModel.profile.observe(this) {
            if (it == null)
                return@observe removeItemFromSharedPref(
                    getString(R.string.sharedPrefName),
                    getString(R.string.sharedPrefToken),
                    getString(R.string.sharedPrefProfile)
                )
            saveItemToSharedPref(
                getString(R.string.sharedPrefName),
                getString(R.string.sharedPrefProfile),
                it
            )
            nextActivity(MainActivity::class.java)
            finish()
        }

        binding.apply {
            submitBtn.setOnClickListener { loginUser() }
            forgetPass.setOnClickListener { nextActivity(ForgetPasswordActivity::class.java) }
            signup.setOnClickListener { nextActivity(SignUpActivity::class.java) }
        }
    }

    private fun loginUser() {
        hideKeyboard()
        val username = binding.username.run {
            clearFocus()
            text.toString().trim()
        }
        val password = binding.password.run {
            clearFocus()
            text.toString().trim()
        }
        authViewModel.loginUser(username, password)
    }

}