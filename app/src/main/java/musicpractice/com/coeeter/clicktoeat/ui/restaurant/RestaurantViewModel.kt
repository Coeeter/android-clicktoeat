package musicpractice.com.coeeter.clicktoeat.ui.restaurant

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.data.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.data.repositories.FavoriteRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.RestaurantRepository
import musicpractice.com.coeeter.clicktoeat.utils.Coroutine
import java.lang.IllegalArgumentException
import kotlin.coroutines.coroutineContext

class RestaurantViewModel(
    private val restaurantId: Int,
    private val token: String,
    private val restaurantRepository: RestaurantRepository,
    private val favoriteRepository: FavoriteRepository
): ViewModel() {

    private var restaurant = MutableLiveData<RestaurantModel>()
    private var favoriteId = -1
    private var isFav = false

    val getRestaurant get() = restaurant as LiveData<RestaurantModel>

    init {
        Coroutine.main {
            val restaurantResponse = restaurantRepository.getRestaurantList()
            if (restaurantResponse.body() == null) return@main
            restaurantResponse.body()!!.forEach {
                if (it._id == restaurantId) restaurant.postValue(it)
            }
            val favoriteResponse = favoriteRepository.getUserFavorites(token)
            if (favoriteResponse.body() == null) return@main
            favoriteResponse.body()!!.forEach { favorite ->
                if (favorite.restaurantID == restaurantId) {
                    favoriteId = favorite._id
                    isFav = true
                    return@forEach
                }
            }
        }
    }

    class RestaurantViewModelFactory(
        private val restaurantId: Int,
        private val token: String,
        private val restaurantRepository: RestaurantRepository,
        private val favoriteRepository: FavoriteRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
                return RestaurantViewModel(restaurantId, token, restaurantRepository, favoriteRepository) as T
            }
            throw IllegalArgumentException("Invalid class")
        }
    }

}