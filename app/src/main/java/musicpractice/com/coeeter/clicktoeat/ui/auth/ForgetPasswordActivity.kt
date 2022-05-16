package musicpractice.com.coeeter.clicktoeat.ui.auth


import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityForgetPasswordBinding
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import musicpractice.com.coeeter.clicktoeat.utils.nextActivity
import musicpractice.com.coeeter.clicktoeat.utils.showKeyboard

@AndroidEntryPoint
class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpListeners()
    }

    private fun setUpListeners() {
        authViewModel.failedToSendEmailToUser.observe(this) {
            binding.apply {
                email.run {
                    isEnabled = true
                    error = it
                    requestFocus()
                }
                submitBtn.isEnabled = true
            }
            showKeyboard()
        }

        authViewModel.emailSent.observe(this) {
            binding.apply {
                email.run {
                    setText("")
                    isEnabled = true
                }
                submitBtn.isEnabled = true
            }
            Snackbar.make(binding.root, "Password reset link sent to $it", Snackbar.LENGTH_SHORT)
                .apply {
                    setAction("OKAY", View.OnClickListener {
                        this.dismiss()
                    })
                    addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                        override fun onShown(transientBottomBar: Snackbar?) {
                            super.onShown(transientBottomBar)
                            Handler(mainLooper).postDelayed({
                                if (!this@ForgetPasswordActivity.isFinishing ||
                                    !this@ForgetPasswordActivity.isDestroyed
                                ) finish()
                            }, 3000)
                        }

                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            finish()
                        }
                    })
                }.show()
        }

        binding.apply {
            submitBtn.setOnClickListener { sendEmail() }
            signup.setOnClickListener { nextActivity(SignUpActivity::class.java) }
        }
    }

    private fun sendEmail() {
        hideKeyboard()
        val email = binding.email.run {
            isEnabled = false
            clearFocus()
            text.toString().trim()
        }
        binding.submitBtn.isEnabled = false
        authViewModel.sendPasswordResetLink(email)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}