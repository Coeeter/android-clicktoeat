package musicpractice.com.coeeter.clicktoeat.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityForgetPasswordBinding
import musicpractice.com.coeeter.clicktoeat.utils.InjectorUtils
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import musicpractice.com.coeeter.clicktoeat.viewmodels.UserViewModel
import android.util.Pair as UtilPair

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitBtn.setOnClickListener {
            hideKeyboard()
            binding.email.clearFocus()
            val email = binding.email.text.toString().trim()

            if (email.isEmpty()) {
                return@setOnClickListener binding.email
                    .createSnackBar("Empty field.\nPlease fill up the field below to reset password.")
            }

            binding.submitBtn.isEnabled = false
            binding.email.isEnabled = false

            val factory = InjectorUtils.provideUserViewModelFactory()
            val viewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

            viewModel.getForgetPasswordResult().observe(this, Observer {
                if (it.accepted == null) return@Observer binding.run {
                    this.submitBtn.isEnabled = true
                    this.email.isEnabled = true
                    this.submitBtn.createSnackBar("Invalid Email")
                }

                val sentEmail = it.accepted[0]
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("email", sentEmail)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    UtilPair.create(binding.brand, "brand"),
                    UtilPair.create(binding.email, "field"),
                    UtilPair.create(binding.submitBtn, "button")
                )
                binding.email.setText("")
                binding.submitBtn.isEnabled = true
                binding.email.isEnabled = true
                startActivity(intent, options.toBundle())
                finish()
            })

            viewModel.forgetPassword(email)
        }

        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}