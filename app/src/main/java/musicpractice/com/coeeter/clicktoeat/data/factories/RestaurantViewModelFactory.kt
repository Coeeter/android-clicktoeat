package musicpractice.com.coeeter.clicktoeat.data.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.data.repositories.RestaurantRepository
import musicpractice.com.coeeter.clicktoeat.data.viewmodels.RestaurantViewModel

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