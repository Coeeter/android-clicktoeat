package musicpractice.com.coeeter.clicktoeat.activities

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.gson.Gson
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityLoginBinding
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.UserViewModel
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
            animateErrorView(
                this,
                binding.error,
                R.anim.slide_down,
                View.VISIBLE,
                "Email with password reset link has been sent to $email."
            )
        }

        val accountCreated = intent.getStringExtra("username")
        if (accountCreated != null) {
            animateErrorView(
                this,
                binding.error,
                R.anim.slide_down,
                View.VISIBLE,
                "Created account with username $accountCreated"
            )
        }

        binding.submitBtn.setOnClickListener {
            hideKeyboard(this)
            binding.username.clearFocus()
            binding.password.clearFocus()

            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                animateErrorView(
                    this,
                    binding.error,
                    R.anim.slide_down,
                    View.VISIBLE,
                    "Empty Fields.\nPlease Fill up the fields below to login"
                )
                return@setOnClickListener
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
                            animateErrorView(
                                this@LoginActivity,
                                binding.error,
                                R.anim.slide_down,
                                View.VISIBLE,
                                result
                            )
                            return
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
            if (!binding.error.isInvisible) animateErrorView(
                this,
                binding.error,
                R.anim.slide_up,
                View.INVISIBLE
            )
            startActivity(intent, options.toBundle())
        }

        binding.signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
        }
    }

    companion object {
        fun animateErrorView(
            context: Context,
            errorView: TextView,
            anim: Int,
            visible: Int,
            errorMsg: String = "",
            scrollParent: ScrollView? = null
        ) {
            if (visible != View.INVISIBLE) errorView.text = errorMsg
            if (errorView.isVisible && visible != View.INVISIBLE) return
            val animation = AnimationUtils.loadAnimation(context, anim)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {
                    scrollParent?.smoothScrollTo(0, 0)
                }

                override fun onAnimationEnd(animation: Animation?) {
                    errorView.visibility = visible
                    if (anim == R.anim.slide_up) return
                    Handler(Looper.getMainLooper()).postDelayed({
                        val animationInverse = AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_up
                        )
                        animationInverse.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {}
                            override fun onAnimationRepeat(animation: Animation?) {}
                            override fun onAnimationEnd(animation: Animation?) {
                                errorView.visibility = View.INVISIBLE
                            }
                        })
                        errorView.startAnimation(animationInverse)
                    }, 2500)
                }
            })
            errorView.startAnimation(animation)
        }

        fun hideKeyboard(context: Activity) {
            try {
                val imm: InputMethodManager =
                    context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
            } catch (e: Exception) {
            }
        }

    }

}