package musicpractice.com.coeeter.clicktoeat.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import musicpractice.com.coeeter.clicktoeat.repositories.CommentRepository
import musicpractice.com.coeeter.clicktoeat.repositories.FavoriteRepository
import musicpractice.com.coeeter.clicktoeat.repositories.RestaurantRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val commentRepository: CommentRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    val restaurantList = restaurantRepository.restaurantList
    val commentList = commentRepository.commentList
    val favoriteList = favoriteRepository.favoriteList
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getRestaurants() =
        viewModelScope.launch { restaurantRepository.getAllRestaurants() }

    fun getComments() =
        viewModelScope.launch { commentRepository.getAllComments() }

    fun getFavorites(token: String) =
        viewModelScope.launch { favoriteRepository.getUserFavorites(token) }

    fun addToFav(token: String, restaurantId: Int) {
        viewModelScope.launch {
            val response = favoriteRepository.createFavorites(token, restaurantId)
            if (response.body() == null || response.body()!!.affectedRows != 1)
                return@launch _error.postValue("Unknown error has occurred.")
            getFavorites(token)
        }
    }

    fun removeFav(token: String, favoriteId: Int) {
        viewModelScope.launch {
            val response = favoriteRepository.deleteFavorites(token, favoriteId)
            if (response.body() == null || response.body()!!.affectedRows != 1)
                return@launch _error.postValue("Unknown error has occurred.")
            getFavorites(token)
        }
    }

}