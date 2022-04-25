package musicpractice.com.coeeter.clicktoeat.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.repositories.CommentRepository
import musicpractice.com.coeeter.clicktoeat.viewmodels.CommentViewModel

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