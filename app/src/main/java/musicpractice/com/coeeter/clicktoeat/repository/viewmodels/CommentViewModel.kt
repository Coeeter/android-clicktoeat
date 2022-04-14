package musicpractice.com.coeeter.clicktoeat.repository.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import musicpractice.com.coeeter.clicktoeat.repository.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.repository.models.CommentModel
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
}