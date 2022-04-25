package musicpractice.com.coeeter.clicktoeat.data.database

import musicpractice.com.coeeter.clicktoeat.data.daos.CommentDao
import musicpractice.com.coeeter.clicktoeat.data.daos.FavoriteDao
import musicpractice.com.coeeter.clicktoeat.data.daos.RestaurantDao
import musicpractice.com.coeeter.clicktoeat.data.daos.UserDao

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