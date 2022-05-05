package musicpractice.com.coeeter.clicktoeat.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.data.repositories.UserRepository
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val repository: UserRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Invalid ViewModel Class")
    }
}