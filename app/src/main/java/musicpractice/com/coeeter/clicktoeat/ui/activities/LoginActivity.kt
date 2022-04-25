package musicpractice.com.coeeter.clicktoeat.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityLoginBinding
import musicpractice.com.coeeter.clicktoeat.utils.InjectorUtils
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import musicpractice.com.coeeter.clicktoeat.data.viewmodels.UserViewModel
import android.util.Pair as UtilPair

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editor: SharedPreferences.Editor = getSharedPreferences("memory", MODE_PRIVATE).edit()

        val email = intent.getStringExtra("email")
        if (email != null) binding.root
            .createSnackBar("Email with password reset link has been sent to $email.")

        val accountCreated = intent.getStringExtra("username")
        if (accountCreated != null) binding.root
            .createSnackBar("Created account with username $accountCreated")

        binding.submitBtn.setOnClickListener {
            hideKeyboard()
            binding.username.clearFocus()
            binding.password.clearFocus()

            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) return@setOnClickListener binding.root
                .createSnackBar("Empty fields.\nPlease fill up the fields below to login.")

            val factory = InjectorUtils.provideUserViewModelFactory()
            val viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

            viewModel.getLoginResult().observe(this, Observer {
                if (it.result == null) return@Observer
                val result = it.result
                if (result.contains("Invalid")) return@Observer binding.root.createSnackBar(result)

                editor.putString("token", result).apply()
                viewModel.getProfile().observe(this, Observer { profile ->
                    editor.putString("profile", Gson().toJson(profile)).apply()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                })
                viewModel.getUser(result)
            })

            viewModel.login(username, password)
        }

        binding.forgetPass.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                UtilPair.create(binding.brand, "brand"),
                UtilPair.create(binding.username, "field"),
                UtilPair.create(binding.submitBtn, "button")
            )
            binding.username.setText("")
            binding.password.setText("")
            startActivity(intent, options.toBundle())
        }

        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
        }
    }

}