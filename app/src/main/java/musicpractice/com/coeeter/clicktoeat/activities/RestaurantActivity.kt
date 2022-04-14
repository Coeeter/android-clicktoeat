package musicpractice.com.coeeter.clicktoeat.activities

import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.repository.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.FavoriteViewModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.RestaurantViewModel

class RestaurantActivity : AppCompatActivity() {
    private lateinit var restaurant: RestaurantModel
    private lateinit var token: String
    private var favoriteId = -1
    private var isFavLivedata = MutableLiveData(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        token = getSharedPreferences("memory", MODE_PRIVATE).getString("token", "").toString()

        val restaurantList = RestaurantViewModel.getAllRestaurants()
        val position = intent.getIntExtra("position", -1)
        restaurantList.observe(this, Observer {
            if (it.size == 0) return@Observer
            if (position == -1) return@Observer
            for (restaurant in it) {
                if (restaurant._id == position) {
                    this.restaurant = restaurant
                    break
                }
            }
            toolbar.title = restaurant.name
            Picasso.with(this)
                .load("${getString(R.string.base_url)}/public/${restaurant.image}")
                .into(findViewById<ImageView>(R.id.restaurantBrand))
        })

        val favoriteList = FavoriteViewModel.getAllFavorites(token)
        favoriteList.observe(this, Observer {
            if (it.size == 0) return@Observer
            isFavLivedata.value = false
            for (favorite in it) {
                if (favorite.restaurantID == restaurant._id) {
                    favoriteId = favorite._id
                    isFavLivedata.value = true
                    return@Observer
                }
            }
        })

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.restaurant_details_menu, menu)
        val addToFavItem = menu.findItem(R.id.miFav)
        isFavLivedata.observe(this, Observer { isFav ->
            if (isFav) addToFavItem.setIcon(R.drawable.ic_favorite_menu)
            else addToFavItem.setIcon(R.drawable.ic_favorite_border_menu)
        })
        addToFavItem.setOnMenuItemClickListener {
            if (!isFavLivedata.value!!) {
                FavoriteViewModel.createFavorite(token, restaurant._id)
                addToFavItem.setIcon(R.drawable.ic_favorite_menu)
                return@setOnMenuItemClickListener true
            }
            FavoriteViewModel.deleteFavorite(token, favoriteId)
            addToFavItem.setIcon(R.drawable.ic_favorite_border_menu)
            return@setOnMenuItemClickListener true
        }
        return super.onCreateOptionsMenu(menu)
    }
}