package musicpractice.com.coeeter.clicktoeat.utils

import musicpractice.com.coeeter.clicktoeat.data.database.Database
import musicpractice.com.coeeter.clicktoeat.data.factories.CommentViewModelFactory
import musicpractice.com.coeeter.clicktoeat.data.factories.FavoriteViewModelFactory
import musicpractice.com.coeeter.clicktoeat.data.factories.RestaurantViewModelFactory
import musicpractice.com.coeeter.clicktoeat.data.factories.UserViewModelFactory
import musicpractice.com.coeeter.clicktoeat.data.repositories.CommentRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.FavoriteRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.RestaurantRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.UserRepository

object InjectorUtils {

    fun provideCommentViewModelFactory(): CommentViewModelFactory {
        val commentRepository = CommentRepository.getInstance(Database.getInstance().commentDao)
        return CommentViewModelFactory(commentRepository)
    }

    fun provideFavoriteViewModelFactory(): FavoriteViewModelFactory {
        val favoriteRepository = FavoriteRepository.getInstance(Database.getInstance().favoriteDao)
        return FavoriteViewModelFactory(favoriteRepository)
    }

    fun provideRestaurantViewModelFactory(): RestaurantViewModelFactory {
        val restaurantRepository =
            RestaurantRepository.getInstance(Database.getInstance().restaurantDao)
        return RestaurantViewModelFactory(restaurantRepository)
    }

    fun provideUserViewModelFactory(): UserViewModelFactory {
        val userRepository = UserRepository.getInstance(Database.getInstance().userDao)
        return UserViewModelFactory(userRepository)
    }

}