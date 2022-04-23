package musicpractice.com.coeeter.clicktoeat.repository.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.repository.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.repository.models.RestaurantModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CommentViewModel : ViewModel() {
    private val commentList = MutableLiveData<ArrayList<CommentModel>>()

    fun getAllComments(): LiveData<ArrayList<CommentModel>> {
        RetrofitClient.commentService.getAllComments()
            .enqueue(object : Callback<ArrayList<CommentModel>?> {
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
        return commentList
    }

    fun createComment(
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
                if (response.body() == null || response.body()!!.affectedRows != 1) return
                getAllComments()
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
            review, rating
        )
        response.enqueue(object : Callback<DefaultResponseModel?> {
            override fun onResponse(
                call: Call<DefaultResponseModel?>,
                response: Response<DefaultResponseModel?>
            ) {
                if (response.body() == null || response.body()!!.affectedRows != 1) return
                getAllComments()
            }

            override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                Log.d("poly", t.message.toString())
            }
        })
    }

    fun deleteComment(token: String, commentId: Int) {
        RetrofitClient.commentService.deleteComment(token, commentId)
            .enqueue(object : Callback<DefaultResponseModel?> {
                override fun onResponse(
                    call: Call<DefaultResponseModel?>,
                    response: Response<DefaultResponseModel?>
                ) {
                    if (response.body() == null || response.body()!!.affectedRows != 1) return
                    getAllComments()
                }

                override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                    Log.d("poly", t.message.toString())
                }
            })
    }
}