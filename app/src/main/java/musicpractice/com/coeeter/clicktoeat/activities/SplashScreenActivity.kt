package musicpractice.com.coeeter.clicktoeat.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.gson.Gson
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivitySplashScreenBinding
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.UserViewModel

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivitySplashScreenBinding.inflate(layoutInflater).root)

        val sharedPreferences = getSharedPreferences("memory", MODE_PRIVATE)
        val token: String = sharedPreferences.getString("token", "") as String
        var intent = Intent(this, LoginActivity::class.java)
        if (token.isNotEmpty()) {
            intent = Intent(this, MainActivity::class.java)
            UserViewModel.getProfile(token).observe(this, Observer {
                val editor = getSharedPreferences("memory", MODE_PRIVATE).edit()
                if (it == null) {
                    Log.d("poly", "empty")
                    editor.remove("token").apply()
                    editor.remove("profile").apply()
                    intent = Intent(this, LoginActivity::class.java)
                    return@Observer
                }
                editor.putString("profile", Gson().toJson(it)).apply()
            })
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            overridePendingTransition(0, R.anim.fade)
            finish()
        }, 2500)
    }
}