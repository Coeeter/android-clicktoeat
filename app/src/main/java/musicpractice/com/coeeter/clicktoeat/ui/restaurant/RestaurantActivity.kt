package musicpractice.com.coeeter.clicktoeat.ui.restaurant

import android.os.BaseBundle
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.models.Restaurant
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityRestaurantBinding
import musicpractice.com.coeeter.clicktoeat.ui.main.MainViewModel
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.getStringFromSharedPref

@AndroidEntryPoint
class RestaurantActivity : AppCompatActivity() {
    private lateinit var token: String
    private lateinit var restaurant: Restaurant
    private var restaurantId: Int? = null
    private var favoriteId = -1
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityRestaurantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpListeners()
        setUpVariables()
    }

    private fun setUpVariables() {
        token = getStringFromSharedPref(
            getString(R.string.sharedPrefName),
            getString(R.string.sharedPrefToken)
        )
        restaurantId = intent.getIntExtra("position", -1)
        mainViewModel.apply {
            getRestaurants()
            getFavorites(token)
        }
    }

    private fun setUpListeners() {
        mainViewModel.apply {
            restaurantList.observe(this@RestaurantActivity) {
                restaurant = it.find { restaurant -> restaurant._id == restaurantId }!!
                binding.apply {
                    Picasso.with(this@RestaurantActivity)
                        .load("${getString(R.string.base_url)}public/${restaurant.image}")
                        .into(restaurantBrand)
                    viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
                        .apply {
                            addFragment(FragmentRestaurantDetails(restaurant), "Details")
                            addFragment(FragmentRestaurantReviews(restaurant), "Reviews")
                        }
                    tabs.setupWithViewPager(viewPager)
                    toolbar.title = restaurant.name
                }
            }
            error.observe(this@RestaurantActivity) { binding.root.createSnackBar(it) }
        }
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.restaurant_details_menu, menu)
        val addToFavItem = menu.findItem(R.id.miFav)
        mainViewModel.favoriteList.observe(this) {
            val favorite = it.find { fav -> fav.restaurantID == restaurantId }
                ?: return@observe addToFavItem.run {
                    setIcon(R.drawable.ic_favorite_border)
                    favoriteId = -1
                }
            addToFavItem.setIcon(R.drawable.ic_favorite)
            favoriteId = favorite._id
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miFav -> {
                if (favoriteId == -1)
                    return mainViewModel.addToFav(token, restaurant._id).run { true }
                mainViewModel.removeFav(token, favoriteId).run { true }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragmentList = ArrayList<Fragment>()
        private val fragmentTitleList = ArrayList<String>()

        override fun getCount() = fragmentList.size
        override fun getItem(position: Int) = fragmentList[position]
        override fun getPageTitle(position: Int) = fragmentTitleList[position]

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }
    }
}