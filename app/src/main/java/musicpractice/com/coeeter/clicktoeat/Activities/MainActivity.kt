package musicpractice.com.coeeter.clicktoeat.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import musicpractice.com.coeeter.clicktoeat.Fragments.FragmentFavorites
import musicpractice.com.coeeter.clicktoeat.Fragments.FragmentHome
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.Fragments.FragmentSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navbar = findViewById<NavigationBarView>(R.id.bottomNav)

        navbar.selectedItemId = R.id.miHome

        replaceFragment(FragmentHome())

        navbar.setOnItemSelectedListener {
            when(it.itemId) {
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

    private fun replaceFragment(fragment : Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }
}