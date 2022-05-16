package musicpractice.com.coeeter.clicktoeat.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import musicpractice.com.coeeter.clicktoeat.data.api.LikeService
import musicpractice.com.coeeter.clicktoeat.data.models.LikeOrDislike
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeRepository @Inject constructor(private val likeService: LikeService) {

    private val _likeAndDislikeList = MutableLiveData<ArrayList<LikeOrDislike>>()
    val likeAndDislikeList: LiveData<ArrayList<LikeOrDislike>>
        get() = _likeAndDislikeList

    suspend fun getAllLikesAndDislikes() {
        withContext(Dispatchers.IO) {
            val response = likeService.getAllLikesAndDislikes()
            if (response.body() == null) return@withContext
            _likeAndDislikeList.postValue(response.body()!!)
        }
    }

    suspend fun createLikesAndDislikes(
        token: String,
        likeOrDislike: LikeOrDislike
    ) = likeOrDislike.run {
        withContext(Dispatchers.IO) {
            likeService.createLikesAndDislikes(
                token,
                commentId,
                isLiked,
                isDisliked
            )
        }
    }

    suspend fun deleteLikesAndDislikes(token: String, likesAndDislikesId: Int) =
        withContext(Dispatchers.IO) {
            likeService.deleteLikesAndDislikes(token, likesAndDislikesId)
        }

}