package musicpractice.com.coeeter.clicktoeat.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.network.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.data.repositories.CommentRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.FavoriteRepository
import musicpractice.com.coeeter.clicktoeat.data.repositories.RestaurantRepository
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentHomeBinding
import musicpractice.com.coeeter.clicktoeat.ui.adapters.RestaurantCardAdapter
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard

class FragmentHome : Fragment(), HomeViewModel.HomeViewModelListener {
    private lateinit var restaurantCardAdapter: RestaurantCardAdapter
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val memory = requireActivity().getSharedPreferences("memory", Context.MODE_PRIVATE)
        val token = memory.getString("token", "")!!
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        ViewModelProvider(
            this,
            HomeViewModelFactory(
                RestaurantRepository(RetrofitClient.restaurantService),
                CommentRepository(RetrofitClient.commentService),
                FavoriteRepository(RetrofitClient.favoriteService),
                requireContext(),
                token
            )
        )[HomeViewModel::class.java].apply {
            homeViewModelListener = this@FragmentHome
            createAdapter()
        }
        hideNotice()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.restaurant_menu, menu)
        val searchItem = menu.findItem(R.id.miSearch)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                val searchView = item!!.actionView as SearchView
                searchView.queryHint = getString(R.string.search_for_restaurants)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        requireActivity().hideKeyboard()
                        searchView.clearFocus()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        restaurantCardAdapter.searchRestaurants(newText)
                        return true
                    }
                })
                searchView.maxWidth = Int.MAX_VALUE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?) = true
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onComplete(adapter: RestaurantCardAdapter) {
        restaurantCardAdapter = adapter.apply { listener = this@FragmentHome }
        binding.homeLayout.apply {
            this.adapter = restaurantCardAdapter
        }
        binding.progress.visibility = View.GONE
    }

    override fun hideNotice() {
        binding.visible = false
    }

    override fun showNotice() {
        binding.visible = true
    }
}