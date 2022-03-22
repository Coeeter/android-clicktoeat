package musicpractice.com.coeeter.clicktoeat.Activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.Api.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject
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

            val payload = JSONObject()
            payload.put("email", email)

            val request = JsonObjectRequest(Request.Method.POST, forgetPasswordLink, payload,
                {
                    response: JSONObject ->
                    run {
//                        Log.d("poly", "Hello")
                        if(response.has("accepted")) {
                            val emailArray: JSONArray = response.get("accepted") as JSONArray
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.putExtra("email", emailArray.get(0).toString())
                            val options = ActivityOptions.makeSceneTransitionAnimation(
                                this,
                                UtilPair.create(findViewById(R.id.brand), "brand"),
                                UtilPair.create(emailInput, "field"),
                                UtilPair.create(submitBtn, "button")
                            )
                            emailInput.setText("")
                            submitBtn.isEnabled = true
                            emailInput.isEnabled = true
                            if (!errorView.isInvisible) LoginActivity.animateErrorView(
                                this@ForgetPasswordActivity,
                                errorView,
                                R.anim.slide_up,
                                View.INVISIBLE
                            )
                            startActivity(intent, options.toBundle())
                            finish()
                            return@JsonObjectRequest
                        }

                        submitBtn.isEnabled = true
                        emailInput.isEnabled = true
                        LoginActivity.animateErrorView(
                            this,
                            errorView,
                            R.anim.slide_down,
                            View.VISIBLE,
                            "Invalid Email"
                        )
                    }
                },
                {
                    error : VolleyError -> Log.d("error", error.toString())
                })
            request.retryPolicy = DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            VolleySingleton.getInstance(this).addToQueue(request)
        }

        signUp.setOnClickListener {
            LoginActivity.startSignUpPage(this, findViewById(R.id.brand), submitBtn, emailInput)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}