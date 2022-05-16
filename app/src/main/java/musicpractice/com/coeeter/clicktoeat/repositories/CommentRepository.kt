package musicpractice.com.coeeter.clicktoeat.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import musicpractice.com.coeeter.clicktoeat.data.api.CommentService
import musicpractice.com.coeeter.clicktoeat.data.models.Comment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(private val commentService: CommentService) {

    private val _commentList = MutableLiveData<ArrayList<Comment>>()
    val commentList: LiveData<ArrayList<Comment>>
        get() = _commentList

    suspend fun getAllComments() {
        withContext(Dispatchers.IO){
            val response = commentService.getAllComments()
            if (response.body() == null) return@withContext
            _commentList.postValue(response.body()!!)
        }
    }

    suspend fun createComment(token: String, comment: Comment) = comment.run {
        withContext(Dispatchers.IO){
            commentService.createComment(
                token,
                restaurantId,
                restaurantName,
                review,
                rating
            )
        }
    }

    suspend fun updateComment(token: String, comment: Comment) = comment.run {
        withContext(Dispatchers.IO){
            commentService.updateComment(
                token,
                id!!,
                restaurantId,
                restaurantName,
                review,
                rating
            )
        }
    }

    suspend fun deleteComment(token: String, commentId: Int) =
        withContext(Dispatchers.IO){ commentService.deleteComment(token, commentId) }

}