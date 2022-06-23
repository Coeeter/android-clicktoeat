package musicpractice.com.coeeter.clicktoeat.ui.restaurant

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import musicpractice.com.coeeter.clicktoeat.data.models.Comment
import musicpractice.com.coeeter.clicktoeat.data.models.LikeOrDislike
import musicpractice.com.coeeter.clicktoeat.repositories.*
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val likeRepository: LikeRepository
) : ViewModel() {
    val restaurantList = restaurantRepository.restaurantList
    val commentList = commentRepository.commentList
    val userList = userRepository.userList
    val likeList = likeRepository.likeAndDislikeList
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getRestaurants() =
        viewModelScope.launch { restaurantRepository.getAllRestaurants() }

    fun getComments() =
        viewModelScope.launch { commentRepository.getAllComments() }

    fun getUsers() =
        viewModelScope.launch { userRepository.getAllUsers() }

    fun getLikes() =
        viewModelScope.launch { likeRepository.getAllLikesAndDislikes() }

    fun createComment(token: String, comment: Comment) {
        viewModelScope.launch {
            val response = commentRepository.createComment(token, comment)
            if (response.body() == null || response.body()!!.affectedRows != 1)
                return@launch _error.postValue("Unknown error has occurred.")
            getComments()
        }
    }

    fun updateComment(token: String, comment: Comment) {
        viewModelScope.launch {
            val response = commentRepository.updateComment(token, comment)
            if (response.body() == null || response.body()!!.affectedRows != 1)
                return@launch _error.postValue("Unknown error has occurred")
            getComments()
        }
    }

    fun deleteComment(token: String, commentId: Int) {
        viewModelScope.launch {
            val response = commentRepository.deleteComment(token, commentId)
            if (response.body() == null || response.body()!!.affectedRows != 1)
                return@launch _error.postValue("Unknown error has occurred")
            getComments()
        }
    }

    fun addLike(token: String, likeOrDislike: LikeOrDislike) {
        viewModelScope.launch {
            val response = likeRepository.createLikesAndDislikes(token, likeOrDislike)
            if (response.body() == null || response.body()!!.affectedRows != 1)
                return@launch _error.postValue("Unknown error has occurred")
            getLikes()
        }
    }

    fun removeLike(token: String, likeId: Int) {
        viewModelScope.launch {
            val response = likeRepository.deleteLikesAndDislikes(token, likeId)
            if (response.body() == null || response.body()!!.affectedRows != 1)
                return@launch _error.postValue("Unknown error has occurred")
            getLikes()
        }
    }

}