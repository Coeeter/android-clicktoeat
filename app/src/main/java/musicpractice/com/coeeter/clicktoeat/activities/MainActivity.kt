package musicpractice.com.coeeter.clicktoeat.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityMainBinding
import musicpractice.com.coeeter.clicktoeat.fragments.FragmentFavorites
import musicpractice.com.coeeter.clicktoeat.fragments.FragmentHome
import musicpractice.com.coeeter.clicktoeat.fragments.FragmentSettings

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navbar = binding.bottomNav

        navbar.selectedItemId = R.id.miHome

        replaceFragment(FragmentHome())

        navbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> replaceFragment(FragmentHome())
                R.id.miFavorites -> replaceFragment(FragmentFavorites())
                R.id.miSettings -> replaceFragment(FragmentSettings())
            }
            true
        }

        navbar.setOnItemReselectedListener(
            NavigationBarView.OnItemReselectedListener { return@OnItemReselectedListener }
        )
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }
}