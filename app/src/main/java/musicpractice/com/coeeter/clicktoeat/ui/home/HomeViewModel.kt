package musicpractice.com.coeeter.clicktoeat.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.data.repositories.CommentRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.FavoriteRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.RestaurantRepository
import musicpractice.com.coeeter.clicktoeat.ui.adapters.RestaurantCardAdapter
import musicpractice.com.coeeter.clicktoeat.utils.Coroutine

class HomeViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val commentRepository: CommentRepository,
    private val restaurantRepository: RestaurantRepository,
    private val context: Context,
    private val token: String
) : ViewModel() {
    var homeViewModelListener: HomeViewModelListener? = null
    lateinit var adapter: RestaurantCardAdapter

    fun createAdapter() {
        Coroutine.main {
            val commentResponse = commentRepository.getCommentList()
            val restaurantResponse = restaurantRepository.getRestaurantList()
            val favoriteResponse = favoriteRepository.getUserFavorites(token)
            if (commentResponse.body() == null || restaurantResponse.body() == null || favoriteResponse.body() == null) return@main
            this.adapter = RestaurantCardAdapter(
                restaurantResponse.body()!!,
                commentResponse.body()!!,
                favoriteResponse.body()!!,
                context,
                token
            )
            homeViewModelListener?.onComplete(this.adapter!!)
        }
    }

    interface HomeViewModelListener {
        fun onComplete(adapter: RestaurantCardAdapter)
        fun hideNotice()
        fun showNotice()
    }
}