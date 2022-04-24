package musicpractice.com.coeeter.clicktoeat.activities

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.gson.Gson
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityLoginBinding
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.UserViewModel
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Pair as UtilPair

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editor: SharedPreferences.Editor = getSharedPreferences("memory", MODE_PRIVATE).edit()

        val email = intent.getStringExtra("email")
        if (email != null) {
            binding.root.createSnackBar("Email with password reset link has been sent to $email.")
        }

        val accountCreated = intent.getStringExtra("username")
        if (accountCreated != null) {
            binding.root.createSnackBar("Created account with username $accountCreated")
        }

        binding.submitBtn.setOnClickListener {
            hideKeyboard()
            binding.username.clearFocus()
            binding.password.clearFocus()

            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                return@setOnClickListener binding.root
                    .createSnackBar("Empty fields.\nPlease fill up the fields below to login.")
            }

            RetrofitClient.userService.login(username, password)
                .enqueue(object : Callback<DefaultResponseModel?> {
                    override fun onResponse(
                        call: Call<DefaultResponseModel?>,
                        response: Response<DefaultResponseModel?>
                    ) {
                        if (response.body() == null || response.body()!!.result == null) return
                        val result = response.body()!!.result!!
                        if (result.contains("Invalid")) {
                            return binding.root.createSnackBar(result)
                        }
                        editor.putString("token", result).apply()
                        UserViewModel.getProfile(result).observe(this@LoginActivity, Observer {
                            editor.putString("profile", Gson().toJson(it)).apply()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            finish()
                        })
                    }

                    override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                        Log.d("poly", t.message.toString())
                    }
                })

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
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
        }
    }

}