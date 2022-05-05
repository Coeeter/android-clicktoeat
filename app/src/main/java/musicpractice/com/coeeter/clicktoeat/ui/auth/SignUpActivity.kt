package musicpractice.com.coeeter.clicktoeat.ui.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.network.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.data.repositories.UserRepository
import musicpractice.com.coeeter.clicktoeat.databinding.ActivitySignUpBinding
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.isVisible


class SignUpActivity : AppCompatActivity(), AuthViewModel.AuthViewModelListener {
    companion object {
        const val IMAGE_CODE = 10
    }

    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        val userRepository = UserRepository(RetrofitClient.userService)
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(userRepository)
        )[AuthViewModel::class.java]
        authViewModel.authViewModelListener = this
        binding.authViewModel = authViewModel

        binding.male.isChecked = true
        authViewModel.onGenderClicked(binding.gender, 0)
    }

    private fun requestPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                0
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            binding.profileImage.setImageURI(data.data)
            authViewModel.image = data.data
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }

    override fun onStarted() {
        binding.progress.isVisible(true)
    }

    override fun onSuccess(result: String) {
        binding.progress.isVisible(false)
        Snackbar.make(binding.root, result, Snackbar.LENGTH_LONG)
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                    Handler(mainLooper).postDelayed({ finish() }, 1000)
                }
            }).show()
    }

    override fun onFailure(message: String) {
        binding.progress.isVisible(false)
        binding.root.createSnackBar(message)
    }
}