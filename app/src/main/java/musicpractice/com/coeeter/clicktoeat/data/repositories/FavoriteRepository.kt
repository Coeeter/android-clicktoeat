package musicpractice.com.coeeter.clicktoeat.data.repositories

import musicpractice.com.coeeter.clicktoeat.data.daos.FavoriteDao

class FavoriteRepository private constructor(private val favoriteDao: FavoriteDao) {

    fun getAllFavorites(token: String) {
        favoriteDao.getAllFavorites(token)
    }

    fun addFavorite(token: String, restaurantId: Int) {
        favoriteDao.addFavorite(token, restaurantId)
    }

    fun removeFavorite(token: String, favoriteId: Int) {
        favoriteDao.removeFavorite(token, favoriteId)
    }

    fun getFavoriteList() = favoriteDao.getFavoriteList()
    fun getCreateFavoriteResult() = favoriteDao.getCreateFavoriteResult()
    fun getDeleteFavoriteResult() = favoriteDao.getDeleteFavoriteResult()

    companion object {
        @Volatile
        private var instance: FavoriteRepository? = null

        fun getInstance(favoriteDao: FavoriteDao) = instance ?: synchronized(this) {
            instance ?: FavoriteRepository(favoriteDao).also { instance = it }
        }
    }
}