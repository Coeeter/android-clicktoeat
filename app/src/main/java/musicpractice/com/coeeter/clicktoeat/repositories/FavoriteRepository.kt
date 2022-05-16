package musicpractice.com.coeeter.clicktoeat.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import musicpractice.com.coeeter.clicktoeat.data.api.FavoriteService
import musicpractice.com.coeeter.clicktoeat.data.models.Favorite
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(private val favoriteService: FavoriteService) {

    private val _favoriteList = MutableLiveData<ArrayList<Favorite>>()
    val favoriteList: LiveData<ArrayList<Favorite>>
        get() = _favoriteList

    suspend fun getUserFavorites(token: String) {
        withContext(Dispatchers.IO) {
            val response = favoriteService.getUserFavorites(token)
            if (response.body() == null) return@withContext
            _favoriteList.postValue(response.body()!!)
        }
    }

    suspend fun createFavorites(token: String, restaurantId: Int) =
        withContext(Dispatchers.IO) { favoriteService.createFavorites(token, restaurantId) }

    suspend fun deleteFavorites(token: String, favoriteId: Int) =
        withContext(Dispatchers.IO) { favoriteService.deleteFavorites(token, favoriteId) }

}