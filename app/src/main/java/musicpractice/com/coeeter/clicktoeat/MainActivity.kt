package musicpractice.com.coeeter.clicktoeat

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import musicpractice.com.coeeter.clicktoeat.fragments.FragmentFavorites
import musicpractice.com.coeeter.clicktoeat.fragments.FragmentHome
import musicpractice.com.coeeter.clicktoeat.fragments.FragmentSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeTitle("Home")

        replaceFragment(FragmentHome())

        findViewById<NavigationBarView>(R.id.bottomNav).setOnItemSelectedListener {
            when(it.itemId) {
                R.id.miHome -> {
                    replaceFragment(FragmentHome())
                    changeTitle("Home")
                }
                R.id.miFavorites -> {
                    replaceFragment(FragmentFavorites())
                    changeTitle("Favorites")
                }
                R.id.miSettings -> {
                    replaceFragment(FragmentSettings())
                    changeTitle("Settings")
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }

    private fun changeTitle(title : String=getString(R.string.app_name)) {
        supportActionBar?.title = Html.fromHtml("<font color=\"#FFFFFF\">$title</font>")
    }
}