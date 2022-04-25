package musicpractice.com.coeeter.clicktoeat.data.repositories

import musicpractice.com.coeeter.clicktoeat.data.daos.CommentDao
import musicpractice.com.coeeter.clicktoeat.data.models.RestaurantModel

class CommentRepository private constructor(private val commentDao: CommentDao) {

    fun getAllComments() {
        commentDao.getAllComments()
    }

    fun addComment(
        token: String,
        restaurant: RestaurantModel,
        review: String,
        rating: Int
    ) {
        commentDao.addComment(token, restaurant, review, rating)
    }

    fun editComment(
        token: String,
        commentId: Int,
        restaurant: RestaurantModel,
        review: String,
        rating: Int
    ) {
        commentDao.editComment(token, commentId, restaurant, review, rating)
    }

    fun removeComment(
        token: String,
        commentId: Int
    ) {
        commentDao.removeComment(token, commentId)
    }

    fun getCommentList() = commentDao.getCommentList()
    fun getAddCommentResult() = commentDao.getCreateCommentResult()
    fun getEditCommentResult() = commentDao.getEditCommentResult()
    fun getDeleteCommentResult() = commentDao.getDeleteCommentResult()

    companion object {
        @Volatile
        private var instance: CommentRepository? = null

        fun getInstance(commentDao: CommentDao) = instance ?: synchronized(this) {
            instance ?: CommentRepository(commentDao).also { instance = it }
        }
    }
}