package musicpractice.com.coeeter.clicktoeat.data.repositories

import musicpractice.com.coeeter.clicktoeat.data.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.data.network.services.CommentService

class CommentRepository(private val commentService: CommentService) {

    suspend fun getCommentList() =
        commentService.getAllComments()

    suspend fun createComment(
        token: String,
        restaurant: RestaurantModel,
        review: String,
        rating: Int
    ) = commentService.createComment(
        token,
        restaurant._id,
        restaurant.name,
        review,
        rating
    )

    suspend fun updateComment(
        token: String,
        commentId: Int,
        restaurant: RestaurantModel,
        review: String,
        rating: Int
    ) = commentService.updateComment(
        token,
        commentId,
        restaurant._id,
        restaurant.name,
        review,
        rating
    )

    suspend fun deleteComment(token: String, commentId: Int) =
        commentService.deleteComment(token, commentId)

}