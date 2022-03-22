package musicpractice.com.coeeter.clicktoeat.Activities

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.Api.VolleySingleton
import org.json.JSONObject
import android.util.Pair as UtilPair

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var submitBtn: Button
    private lateinit var errorView: TextView
    private lateinit var forgetPass: TextView
    private lateinit var signUp: TextView
    private lateinit var logInLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        logInLink = "${getString(R.string.base_url)}/users/login"

        errorView = findViewById(R.id.error)
        usernameInput = findViewById(R.id.username)
        passwordInput = findViewById(R.id.password)
        submitBtn = findViewById(R.id.submitBtn)
        forgetPass = findViewById(R.id.forgetPass)
        signUp = findViewById(R.id.signup)

        val editor : SharedPreferences.Editor = getSharedPreferences("memory", MODE_PRIVATE).edit()

        val email = intent.getStringExtra("email")
        if (email != null) {
            animateErrorView(
                this,
                errorView,
                R.anim.slide_down,
                View.VISIBLE,
                "Email with password reset link has been sent to $email."
            )
        }

        val accountCreated = intent.getStringExtra("username")
        if (accountCreated != null) {
            animateErrorView(
                this,
                errorView,
                R.anim.slide_down,
                View.VISIBLE,
                "Created account with username $accountCreated"
            )
        }

        submitBtn.setOnClickListener {
            hideKeyboard(this)
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                animateErrorView(
                    this,
                    errorView,
                    R.anim.slide_down,
                    View.VISIBLE,
                    "Empty Fields.\nPlease Fill up the fields below to login"
                )
                return@setOnClickListener
            }

            val payload = JSONObject()
            payload.put("username", username)
            payload.put("password", password)

            val request = JsonObjectRequest(Request.Method.POST, logInLink, payload,
                {
                    response : JSONObject ->
                    run {
                        val result: String = response.getString("result") ?: return@JsonObjectRequest
                        if (result == "Invalid Password" || result == "Invalid Username") {
                            animateErrorView(this@LoginActivity, errorView, R.anim.slide_down, View.VISIBLE, result)
                            return@JsonObjectRequest
                        }
                        editor.putString("token", result)
                        editor.apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                        finish()
                    }
                },
                {
                    error : VolleyError -> Log.d("error", error.toString())
                })
            VolleySingleton.getInstance(this).addToQueue(request)
        }

        forgetPass.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this,
                UtilPair.create(findViewById<View>(R.id.brand), "brand"),
                UtilPair.create(usernameInput, "field"),
                UtilPair.create(submitBtn, "button")
            )
            usernameInput.setText("")
            passwordInput.setText("")
            if (!errorView.isInvisible) animateErrorView(this, errorView, R.anim.slide_up, View.INVISIBLE)
            startActivity(intent, options.toBundle())
        }

        signUp.setOnClickListener {
            startSignUpPage(this, findViewById(R.id.brand), submitBtn, usernameInput)
        }
    }

    companion object {
        fun animateErrorView(context: Context, errorView: TextView, anim: Int, visible: Int, errorMsg: String="", scrollParent: ScrollView?=null) {
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
                        animationInverse.setAnimationListener(object: Animation.AnimationListener {
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

        fun startSignUpPage(context: Activity, logo: TextView, button: Button, field: EditText) {
            val intent = Intent(context, SignUpActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                context,
                UtilPair.create(logo, "brand"),
                UtilPair.create(button, "button"),
                UtilPair.create(field, "field")
            )
            context.startActivity(intent, options.toBundle())
        }

        fun hideKeyboard(context: Activity) {
            try {
                val imm : InputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
            } catch (e: Exception) {}
        }

    }

}