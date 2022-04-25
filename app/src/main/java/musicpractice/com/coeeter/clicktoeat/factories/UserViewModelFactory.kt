package musicpractice.com.coeeter.clicktoeat.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.repositories.UserRepository
import musicpractice.com.coeeter.clicktoeat.viewmodels.UserViewModel

class UserViewModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            UserViewModel(repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}