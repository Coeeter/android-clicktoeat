package musicpractice.com.coeeter.clicktoeat.viewmodels

import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.repositories.FavoriteRepository

class FavoriteViewModel constructor(private val favoriteRepository: FavoriteRepository) :
    ViewModel() {

    fun getAllFavorites(token: String) = favoriteRepository.getAllFavorites(token)

    fun addFavorite(token: String, restaurantId: Int) =
        favoriteRepository.addFavorite(token, restaurantId)

    fun removeFavorite(token: String, favoriteId: Int) =
        favoriteRepository.removeFavorite(token, favoriteId)

    fun getFavoriteList() = favoriteRepository.getFavoriteList()
    fun getCreateFavoriteResult() = favoriteRepository.getCreateFavoriteResult()
    fun getDeleteFavoriteResult() = favoriteRepository.getDeleteFavoriteResult()

}