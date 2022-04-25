package musicpractice.com.coeeter.clicktoeat.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityRestaurantBinding
import musicpractice.com.coeeter.clicktoeat.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.ui.fragments.FragmentRestaurantDetails
import musicpractice.com.coeeter.clicktoeat.ui.fragments.FragmentRestaurantReviews
import musicpractice.com.coeeter.clicktoeat.utils.InjectorUtils
import musicpractice.com.coeeter.clicktoeat.viewmodels.FavoriteViewModel
import musicpractice.com.coeeter.clicktoeat.viewmodels.RestaurantViewModel

class RestaurantActivity : AppCompatActivity() {
    private lateinit var restaurant: RestaurantModel
    private lateinit var token: String
    private var favoriteId = -1
    private var isFavLivedata = MutableLiveData(false)
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var binding: ActivityRestaurantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        token = getSharedPreferences("memory", MODE_PRIVATE).getString("token", "").toString()
        val position = intent.getIntExtra("position", -1)

        val restaurantViewModel = ViewModelProvider(
            this,
            InjectorUtils.provideRestaurantViewModelFactory()
        )[RestaurantViewModel::class.java]

        favoriteViewModel = ViewModelProvider(
            this,
            InjectorUtils.provideFavoriteViewModelFactory()
        )[FavoriteViewModel::class.java]

        restaurantViewModel.getRestaurantList().observe(this, Observer {
            if (it.size == 0) return@Observer
            if (position == -1) return@Observer
            for (restaurant in it) {
                if (restaurant._id == position) {
                    this.restaurant = restaurant
                    break
                }
            }
            binding.toolbar.title = restaurant.name
            Picasso.with(this)
                .load("${getString(R.string.base_url)}/public/${restaurant.image}")
                .into(binding.restaurantBrand)

            val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
            viewPagerAdapter.addFragment(FragmentRestaurantDetails(restaurant), "Details")
            viewPagerAdapter.addFragment(FragmentRestaurantReviews(restaurant), "Reviews")
            binding.viewPager.adapter = viewPagerAdapter

            binding.tabs.setupWithViewPager(binding.viewPager)
        })
        restaurantViewModel.getAllRestaurants()

        favoriteViewModel.getFavoriteList().observe(this, Observer {
            isFavLivedata.value = false
            for (favorite in it) {
                if (favorite.restaurantID == restaurant._id) {
                    favoriteId = favorite._id
                    isFavLivedata.postValue(true)
                    return@Observer
                }
            }
        })
        favoriteViewModel.getAllFavorites(token)

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.restaurant_details_menu, menu)
        val addToFavItem = menu.findItem(R.id.miFav)
        isFavLivedata.observe(this, Observer { isFav ->
            if (isFav) addToFavItem.setIcon(R.drawable.ic_favorite)
            else addToFavItem.setIcon(R.drawable.ic_favorite_border)
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miFav -> {
                if (!isFavLivedata.value!!) {
                    favoriteViewModel.getCreateFavoriteResult().observe(this, Observer {
                        if (it.affectedRows != 1) return@Observer
                        favoriteViewModel.getAllFavorites(token)
                    })
                    favoriteViewModel.addFavorite(token, restaurant._id)
                    return true
                }
                favoriteViewModel.getDeleteFavoriteResult().observe(this, Observer {
                    if (it.affectedRows != 1) return@Observer
                    favoriteViewModel.getAllFavorites(token)
                })
                favoriteViewModel.removeFavorite(token, favoriteId)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragmentList = ArrayList<Fragment>()
        private val fragmentTitleList = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }
    }
}