package musicpractice.com.coeeter.clicktoeat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivitySplashScreenBinding
import musicpractice.com.coeeter.clicktoeat.ui.main.MainActivity
import musicpractice.com.coeeter.clicktoeat.utils.removeItemFromSharedPref
import musicpractice.com.coeeter.clicktoeat.utils.saveItemToSharedPref

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivitySplashScreenBinding.inflate(layoutInflater).root)

        val sharedPreferences = getSharedPreferences(
            getString(R.string.sharedPrefName),
            MODE_PRIVATE
        )
        val token = sharedPreferences.getString(
            getString(R.string.sharedPrefToken),
            ""
        )!!

        var intent = Intent(this, LoginActivity::class.java)

        authViewModel.profile.observe(this) {
            if (it == null) return@observe removeItemFromSharedPref(
                getString(R.string.sharedPrefName),
                getString(R.string.sharedPrefToken),
                getString(R.string.sharedPrefProfile)
            ).also {
                intent = Intent(this, LoginActivity::class.java)
            }

            intent = Intent(this, MainActivity::class.java)
            saveItemToSharedPref(
                getString(R.string.sharedPrefName),
                getString(R.string.sharedPrefProfile),
                it
            )
        }
        if (token.isNotEmpty()) authViewModel.getProfile(token)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            overridePendingTransition(0, R.anim.fade)
            finish()
        }, 2500)
    }
}