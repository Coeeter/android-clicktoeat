package musicpractice.com.coeeter.clicktoeat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        val sharedPreferences = getSharedPreferences("memory", MODE_PRIVATE)
        val token : String = sharedPreferences.getString("token", "") as String
        var intent = Intent(this, MainActivity::class.java)
        if (token.isEmpty()) {
            intent = Intent(this, LoginActivity::class.java)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            overridePendingTransition(0, R.anim.fade)
            finish()
        }, 2500)
    }
}