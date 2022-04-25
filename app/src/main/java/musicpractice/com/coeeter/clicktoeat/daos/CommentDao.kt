package musicpractice.com.coeeter.clicktoeat.daos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import musicpractice.com.coeeter.clicktoeat.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentDao {
    private val commentList = MutableLiveData<ArrayList<CommentModel>>()
    private val createCommentResult = MutableLiveData<DefaultResponseModel>()
    private val editCommentResult = MutableLiveData<DefaultResponseModel>()
    private val deleteCommentResult = MutableLiveData<DefaultResponseModel>()

    fun getAllComments() {
        val response = RetrofitClient.commentService.getAllComments()
        response.enqueue(object : Callback<ArrayList<CommentModel>?> {
            override fun onResponse(
                call: Call<ArrayList<CommentModel>?>,
                response: Response<ArrayList<CommentModel>?>
            ) {
                if (response.body() == null) return
                commentList.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<ArrayList<CommentModel>?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun addComment(
        token: String,
        restaurant: RestaurantModel,
        review: String,
        rating: Int
    ) {
        val response = RetrofitClient.commentService.createComment(
            token,
            restaurant._id,
            restaurant.name,
            review,
            rating
        )
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                createCommentResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun editComment(
        token: String,
        commentId: Int,
        restaurant: RestaurantModel,
        review: String,
        rating: Int
    ) {
        val response = RetrofitClient.commentService.updateComment(
            token,
            commentId,
            restaurant._id,
            restaurant.name,
            review,
            rating
        )
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                editCommentResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun removeComment(token: String, commentId: Int) {
        val response = RetrofitClient.commentService.deleteComment(token, commentId)
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null) return
                deleteCommentResult.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun getCommentList() = commentList as LiveData<ArrayList<CommentModel>>
    fun getCreateCommentResult() = createCommentResult as LiveData<DefaultResponseModel>
    fun getEditCommentResult() = editCommentResult as LiveData<DefaultResponseModel>
    fun getDeleteCommentResult() = deleteCommentResult as LiveData<DefaultResponseModel>
}