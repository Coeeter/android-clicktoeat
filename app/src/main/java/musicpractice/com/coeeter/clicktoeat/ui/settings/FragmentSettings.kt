package musicpractice.com.coeeter.clicktoeat.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentSettingsBinding
import musicpractice.com.coeeter.clicktoeat.ui.auth.LoginActivity
import musicpractice.com.coeeter.clicktoeat.utils.nextActivity
import musicpractice.com.coeeter.clicktoeat.utils.removeItemFromSharedPref

class FragmentSettings : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.editProfile.setOnClickListener {
            goToEditProfileActivity("Edit Profile")
        }

        binding.changePassword.setOnClickListener {
            goToEditProfileActivity("Change Password")
        }

        binding.deleteAccount.setOnClickListener {
            goToEditProfileActivity("Delete Account")
        }

        binding.logout.setOnClickListener {
            requireActivity().apply {
                removeItemFromSharedPref(
                    getString(R.string.sharedPrefName),
                    getString(R.string.sharedPrefToken),
                    getString(R.string.sharedPrefProfile)
                )
                requireActivity().nextActivity(Intent(requireContext(), LoginActivity::class.java))
                finish()
            }
        }
        return binding.root
    }

    private fun goToEditProfileActivity(formType: String) {
        Intent(requireContext(), EditProfileActivity::class.java).apply {
            putExtra("form", formType)
            requireActivity().nextActivity(this)
        }
    }

}