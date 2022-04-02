package musicpractice.com.coeeter.clicktoeat.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.apiClient.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.apiClient.models.DefaultResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Pair as UtilPair

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var submitBtn: Button
    private lateinit var emailInput: EditText
    private lateinit var errorView: TextView
    private lateinit var signUp: TextView
    private lateinit var forgetPasswordLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        supportActionBar?.hide()

        forgetPasswordLink = "${getString(R.string.base_url)}/users/forgotPassword"

        submitBtn = findViewById(R.id.submitBtn)
        emailInput = findViewById(R.id.email)
        errorView = findViewById(R.id.error)
        signUp = findViewById(R.id.signup)

        submitBtn.setOnClickListener {
            LoginActivity.hideKeyboard(this)
            emailInput.clearFocus()
            val email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                LoginActivity.animateErrorView(
                    this,
                    errorView,
                    R.anim.slide_down,
                    View.VISIBLE,
                    "Empty Field.\nPlease Fill up the field below to reset password"
                )
                return@setOnClickListener
            }

            submitBtn.isEnabled = false
            emailInput.isEnabled = false

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
                                UtilPair.create(findViewById(R.id.brand), "brand"),
                                UtilPair.create(emailInput, "field"),
                                UtilPair.create(submitBtn, "button")
                            )
                            emailInput.setText("")
                            submitBtn.isEnabled = true
                            emailInput.isEnabled = true
                            if (errorView.isVisible) LoginActivity.animateErrorView(
                                this@ForgetPasswordActivity,
                                errorView,
                                R.anim.slide_up,
                                View.INVISIBLE
                            )
                            startActivity(intent, options.toBundle())
                            finish()
                            return
                        }

                        submitBtn.isEnabled = true
                        emailInput.isEnabled = true
                        LoginActivity.animateErrorView(
                            this@ForgetPasswordActivity,
                            errorView,
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

        signUp.setOnClickListener {
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