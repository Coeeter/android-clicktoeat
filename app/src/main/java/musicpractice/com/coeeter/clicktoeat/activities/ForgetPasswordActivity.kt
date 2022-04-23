package musicpractice.com.coeeter.clicktoeat.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityForgetPasswordBinding
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.DefaultResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Pair as UtilPair

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitBtn.setOnClickListener {
            LoginActivity.hideKeyboard(this)
            binding.email.clearFocus()
            val email = binding.email.text.toString().trim()

            if (email.isEmpty()) {
                LoginActivity.animateErrorView(
                    this,
                    binding.error,
                    R.anim.slide_down,
                    View.VISIBLE,
                    "Empty Field.\nPlease Fill up the field below to reset password"
                )
                return@setOnClickListener
            }

            binding.submitBtn.isEnabled = false
            binding.email.isEnabled = false

            RetrofitClient.userService.forgotPassword(email)
                .enqueue(object : Callback<DefaultResponseModel?> {
                    override fun onResponse(
                        call: Call<DefaultResponseModel?>,
                        response: Response<DefaultResponseModel?>
                    ) {
                        if (response.body() != null && response.body()!!.accepted != null) {
                            val sentEmail = response.body()!!.accepted!![0]
                            val intent =
                                Intent(this@ForgetPasswordActivity, LoginActivity::class.java)
                            intent.putExtra("email", sentEmail)
                            val options = ActivityOptions.makeSceneTransitionAnimation(
                                this@ForgetPasswordActivity,
                                UtilPair.create(binding.brand, "brand"),
                                UtilPair.create(binding.email, "field"),
                                UtilPair.create(binding.submitBtn, "button")
                            )
                            binding.email.setText("")
                            binding.submitBtn.isEnabled = true
                            binding.email.isEnabled = true
                            if (binding.error.isVisible) LoginActivity.animateErrorView(
                                this@ForgetPasswordActivity,
                                binding.error,
                                R.anim.slide_up,
                                View.INVISIBLE
                            )
                            startActivity(intent, options.toBundle())
                            finish()
                            return
                        }

                        binding.submitBtn.isEnabled = true
                        binding.email.isEnabled = true
                        LoginActivity.animateErrorView(
                            this@ForgetPasswordActivity,
                            binding.error,
                            R.anim.slide_down,
                            View.VISIBLE,
                            "Invalid Email"
                        )
                    }

                    override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                        Log.d("poly", t.message.toString())
                    }
                })
        }

        binding.signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}