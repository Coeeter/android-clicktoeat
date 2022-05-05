package musicpractice.com.coeeter.clicktoeat.data.repositories

import musicpractice.com.coeeter.clicktoeat.data.network.services.FavoriteService

class FavoriteRepository(private val favoriteService: FavoriteService) {

    suspend fun getUserFavorites(token: String) =
        favoriteService.getUserFavorites(token)

    suspend fun createFavorites(token: String, restaurantId: Int) =
        favoriteService.createFavorites(token, restaurantId)

    suspend fun deleteFavorites(token: String, favoriteId: Int) =
        favoriteService.deleteFavorites(token, favoriteId)

}