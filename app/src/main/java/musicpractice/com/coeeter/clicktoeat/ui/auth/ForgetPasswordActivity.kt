package musicpractice.com.coeeter.clicktoeat.ui.auth

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.network.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.data.repositories.UserRepository
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityForgetPasswordBinding
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.isVisible

class ForgetPasswordActivity : AppCompatActivity(), AuthViewModel.AuthViewModelListener {
    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password)
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
        binding.email.isEnabled = false
        binding.submitBtn.isEnabled = false
    }

    override fun onSuccess(result: String) {
        binding.progress.isVisible(false)
        binding.email.isEnabled = true
        binding.submitBtn.isEnabled = true
        Snackbar.make(binding.root, result, Snackbar.LENGTH_LONG)
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                    Handler(mainLooper).postDelayed({ finish() }, 1000)
                }
            })
            .show()
    }

    override fun onFailure(message: String) {
        binding.progress.isVisible(false)
        binding.email.isEnabled = true
        binding.submitBtn.isEnabled = true
        binding.root.createSnackBar(message)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}