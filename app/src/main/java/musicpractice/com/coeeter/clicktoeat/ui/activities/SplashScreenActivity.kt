package musicpractice.com.coeeter.clicktoeat.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivitySplashScreenBinding
import musicpractice.com.coeeter.clicktoeat.utils.InjectorUtils
import musicpractice.com.coeeter.clicktoeat.data.viewmodels.UserViewModel

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivitySplashScreenBinding.inflate(layoutInflater).root)

        val sharedPreferences = getSharedPreferences("memory", MODE_PRIVATE)
        val token: String = sharedPreferences.getString("token", "") as String
        var intent = Intent(this, LoginActivity::class.java)
        if (token.isNotEmpty()) {
            intent = Intent(this, MainActivity::class.java)
            val viewModel = ViewModelProvider(
                this,
                InjectorUtils.provideUserViewModelFactory()
            )[UserViewModel::class.java]
            viewModel.getProfile().observe(this, Observer {
                val editor = getSharedPreferences("memory", MODE_PRIVATE).edit()
                if (it == null) {
                    editor.remove("token").apply()
                    editor.remove("profile").apply()
                    intent = Intent(this, LoginActivity::class.java)
                    return@Observer
                }
                editor.putString("profile", Gson().toJson(it)).apply()
            })
            viewModel.getUser(token)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            overridePendingTransition(0, R.anim.fade)
            finish()
        }, 2500)
    }
}