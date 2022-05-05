package musicpractice.com.coeeter.clicktoeat.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.network.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.data.repositories.UserRepository
import musicpractice.com.coeeter.clicktoeat.databinding.ActivitySplashScreenBinding
import musicpractice.com.coeeter.clicktoeat.ui.auth.LoginActivity
import musicpractice.com.coeeter.clicktoeat.ui.home.MainActivity
import musicpractice.com.coeeter.clicktoeat.utils.Coroutine

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val sharedPreferences = getSharedPreferences("memory", MODE_PRIVATE)
        val token: String = sharedPreferences.getString("token", "") as String
        var intent = Intent(this, LoginActivity::class.java)
        if (token.isNotEmpty()) {
            Coroutine.main {
                val repository = UserRepository(RetrofitClient.userService)
                val response = repository.getProfile(token)
                if (response.body() != null && response.body()!!.size == 1)
                    intent = Intent(this, MainActivity::class.java)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            overridePendingTransition(0, R.anim.fade)
            finish()
        }, 2500)
    }
}