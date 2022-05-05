package musicpractice.com.coeeter.clicktoeat.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.network.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.data.repositories.UserRepository
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityLoginBinding
import musicpractice.com.coeeter.clicktoeat.ui.home.MainActivity
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.isVisible

class LoginActivity : AppCompatActivity(), AuthViewModel.AuthViewModelListener {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val userRepository = UserRepository(RetrofitClient.userService)
        val authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(userRepository)
        )[AuthViewModel::class.java]
        authViewModel.authViewModelListener = this
        binding.authViewModel = authViewModel
    }

    override fun onStarted() {
        binding.progress.isVisible(true)
    }

    override fun onSuccess(result: String) {
        binding.progress.isVisible(false)
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
    }

    override fun onFailure(message: String) {
        binding.progress.isVisible(false)
        binding.root.createSnackBar(message)
    }

}