package musicpractice.com.coeeter.clicktoeat.utils

import musicpractice.com.coeeter.clicktoeat.database.Database
import musicpractice.com.coeeter.clicktoeat.factories.CommentViewModelFactory
import musicpractice.com.coeeter.clicktoeat.factories.FavoriteViewModelFactory
import musicpractice.com.coeeter.clicktoeat.factories.RestaurantViewModelFactory
import musicpractice.com.coeeter.clicktoeat.factories.UserViewModelFactory
import musicpractice.com.coeeter.clicktoeat.repositories.CommentRepository
import musicpractice.com.coeeter.clicktoeat.repositories.FavoriteRepository
import musicpractice.com.coeeter.clicktoeat.repositories.RestaurantRepository
import musicpractice.com.coeeter.clicktoeat.repositories.UserRepository

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