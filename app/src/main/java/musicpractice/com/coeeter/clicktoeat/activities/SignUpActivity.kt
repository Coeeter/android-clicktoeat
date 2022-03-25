package musicpractice.com.coeeter.clicktoeat.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.webservices.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.models.DefaultResponseModel
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    private lateinit var userCreationLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        userCreationLink = "${getString(R.string.base_url)}/users?d=mobile"

        val error = findViewById<TextView>(R.id.error)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        val nameInput = findViewById<EditText>(R.id.name)
        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPassword)
        val emailInput = findViewById<EditText>(R.id.email)
        val phoneNumInput = findViewById<EditText>(R.id.phoneNum)
        val addressInput = findViewById<EditText>(R.id.address)
        val genderInput = findViewById<RadioGroup>(R.id.gender)
        val parent = findViewById<ScrollView>(R.id.parent)

        findViewById<RadioButton>(R.id.male).isChecked = true

        submitBtn.setOnClickListener {
            LoginActivity.hideKeyboard(this)

            val name = nameInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()
            val email = emailInput.text.toString()
            val phoneNum = phoneNumInput.text.toString()
            val address = addressInput.text.toString()
            var gender = ""
            when (genderInput.checkedRadioButtonId) {
                R.id.male -> gender = "M"
                R.id.female -> gender = "F"
            }

            for (item in arrayOf(
                name,
                username,
                password,
                confirmPassword,
                email,
                phoneNum,
                address
            )) {
                if (item.isEmpty()) {
                    LoginActivity.animateErrorView(
                        this,
                        error,
                        R.anim.slide_down,
                        View.VISIBLE,
                        "Empty Field.\nPlease fill up " +
                                "the fields below to register",
                        parent
                    )
                    return@setOnClickListener
                }
            }

            val nameArray = name.split(" ")

            val firstName = nameArray[0]
            val lastName =
                if (nameArray.size >= 2)
                    nameArray
                        .slice(1 until nameArray.size)
                        .joinToString(" ")
                else ""

            if (password != confirmPassword) {
                LoginActivity.animateErrorView(
                    this,
                    error,
                    R.anim.slide_down,
                    View.VISIBLE,
                    "Wrong confirm password " +
                            "entered.\nPlease input correct password",
                    parent
                )
                return@setOnClickListener
            }

            val payload = JSONObject()
            payload.put("username", username)
            payload.put("password", password)
            payload.put("email", email)
            payload.put("phoneNum", phoneNum)
            payload.put("firstName", firstName)
            payload.put("lastName", lastName)
            payload.put("gender", gender)
            payload.put("address", address)

            val requestBody = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                payload.toString()
            )

            RetrofitClient.userService.createUser(requestBody)
                .enqueue(object : Callback<DefaultResponseModel?> {
                    override fun onResponse(
                        call: Call<DefaultResponseModel?>,
                        response: Response<DefaultResponseModel?>
                    ) {
                        if (response.body()!!.result != null) {
                            LoginActivity.animateErrorView(
                                this@SignUpActivity,
                                error,
                                R.anim.slide_down,
                                View.VISIBLE,
                                response.body()!!.result!!,
                                parent
                            )
                            return
                        }
                        if (response.body()!!.affectedRows != null && response.body()!!.affectedRows == 1) {
                            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                            intent.putExtra("username", username)
                            val options = ActivityOptions
                                .makeSceneTransitionAnimation(
                                    this@SignUpActivity,
                                    Pair.create(findViewById(R.id.brand), "brand"),
                                    Pair.create(findViewById(R.id.form), "field"),
                                    Pair.create(submitBtn, "button")
                                )
                            startActivity(intent, options.toBundle())
                            finish()
                            return
                        }
                    }

                    override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                        Log.d("poly", t.message.toString())
                    }
                })
        }
    }

}