package musicpractice.com.coeeter.clicktoeat.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentSettingsBinding
import musicpractice.com.coeeter.clicktoeat.ui.auth.LoginActivity

class FragmentSettings : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.logout.setOnClickListener {
            val editor =
                requireActivity().getSharedPreferences("memory", Context.MODE_PRIVATE).edit()
            editor.remove("token").apply()
            editor.remove("profile").apply()
            requireActivity().apply {
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        }
        return binding.root
    }

}