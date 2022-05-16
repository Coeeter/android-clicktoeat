package musicpractice.com.coeeter.clicktoeat.ui.restaurant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import musicpractice.com.coeeter.clicktoeat.data.models.Comment
import musicpractice.com.coeeter.clicktoeat.repositories.CommentRepository
import musicpractice.com.coeeter.clicktoeat.repositories.RestaurantRepository
import musicpractice.com.coeeter.clicktoeat.repositories.UserRepository
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val restaurantList = restaurantRepository.restaurantList
    val commentList = commentRepository.commentList
    val userList = userRepository.userList
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getRestaurants() =
        viewModelScope.launch { restaurantRepository.getAllRestaurants() }

    fun getComments() =
        viewModelScope.launch { commentRepository.getAllComments() }

    fun getUsers() =
        viewModelScope.launch { userRepository.getAllUsers() }

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

}