package musicpractice.com.coeeter.clicktoeat.data.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.data.repositories.CommentRepository
import musicpractice.com.coeeter.clicktoeat.data.viewmodels.CommentViewModel

class CommentViewModelFactory(private val commentRepository: CommentRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            CommentViewModel(commentRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}