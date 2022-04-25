package musicpractice.com.coeeter.clicktoeat.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.repositories.FavoriteRepository
import musicpractice.com.coeeter.clicktoeat.viewmodels.FavoriteViewModel

class FavoriteViewModelFactory(private val repository: FavoriteRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}