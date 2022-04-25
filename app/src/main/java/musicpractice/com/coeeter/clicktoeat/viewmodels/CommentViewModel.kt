package musicpractice.com.coeeter.clicktoeat.viewmodels

import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.repositories.CommentRepository

class CommentViewModel(private val commentRepository: CommentRepository) : ViewModel() {

    fun getAllComments() = commentRepository.getAllComments()

    fun addComment(
        token: String,
        restaurant: RestaurantModel,
        review: String,
        rating: Int
    ) = commentRepository.addComment(token, restaurant, review, rating)

    fun editComment(
        token: String,
        commentId: Int,
        restaurant: RestaurantModel,
        review: String,
        rating: Int
    ) = commentRepository.editComment(token, commentId, restaurant, review, rating)

    fun removeComment(
        token: String,
        commentId: Int
    ) = commentRepository.removeComment(token, commentId)

    fun getCommentList() = commentRepository.getCommentList()
    fun getAddCommentResult() = commentRepository.getAddCommentResult()
    fun getEditCommentResult() = commentRepository.getEditCommentResult()
    fun getDeleteCommentResult() = commentRepository.getDeleteCommentResult()
}