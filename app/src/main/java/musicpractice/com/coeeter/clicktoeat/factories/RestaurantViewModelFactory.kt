package musicpractice.com.coeeter.clicktoeat.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.repositories.RestaurantRepository
import musicpractice.com.coeeter.clicktoeat.viewmodels.RestaurantViewModel

class RestaurantViewModelFactory(private val repository: RestaurantRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
            RestaurantViewModel(repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}