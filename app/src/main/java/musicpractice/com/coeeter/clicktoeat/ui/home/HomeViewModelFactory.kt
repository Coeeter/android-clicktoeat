package musicpractice.com.coeeter.clicktoeat.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.data.repositories.CommentRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.FavoriteRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.RestaurantRepository

class HomeViewModelFactory(
    private val restaurantRepository: RestaurantRepository,
    private val commentRepository: CommentRepository,
    private val favoriteRepository: FavoriteRepository,
    private val context: Context,
    private val token: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                favoriteRepository,
                commentRepository,
                restaurantRepository,
                context,
                token
            ) as T
        }
        throw IllegalArgumentException("Invalid ViewModel Class")
    }
}