package musicpractice.com.coeeter.clicktoeat.database

import musicpractice.com.coeeter.clicktoeat.daos.CommentDao
import musicpractice.com.coeeter.clicktoeat.daos.FavoriteDao
import musicpractice.com.coeeter.clicktoeat.daos.RestaurantDao
import musicpractice.com.coeeter.clicktoeat.daos.UserDao

class Database {
    val commentDao = CommentDao()
    val favoriteDao = FavoriteDao()
    val restaurantDao = RestaurantDao()
    val userDao = UserDao()

    companion object {
        @Volatile
        private var instance: Database? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: Database().also { instance = it }
        }
    }
}