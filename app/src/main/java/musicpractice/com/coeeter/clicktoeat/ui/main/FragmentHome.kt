package musicpractice.com.coeeter.clicktoeat.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentHomeBinding
import musicpractice.com.coeeter.clicktoeat.ui.adapters.RestaurantCardAdapter
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.getStringFromSharedPref
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import musicpractice.com.coeeter.clicktoeat.utils.isVisible

@AndroidEntryPoint
class FragmentHome :
    Fragment(),
    MenuItem.OnActionExpandListener,
    SearchView.OnQueryTextListener,
    RestaurantCardAdapter.OnDataSetChangedListener {

    private lateinit var restaurantCardAdapter: RestaurantCardAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentHomeBinding
    private lateinit var token: String
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        setUpListeners()
        initVariables()
        return binding.root
    }

    private fun initVariables() {
        token = requireActivity()
            .getStringFromSharedPref(
                requireContext().getString(R.string.sharedPrefName),
                requireContext().getString(R.string.sharedPrefToken)
            )
        restaurantCardAdapter = RestaurantCardAdapter(
            (activity as Context),
            RestaurantCardAdapter.HOME_PAGE
        ).apply { dataSetListener = this@FragmentHome }
        recyclerView = binding.homeLayout.apply { adapter = restaurantCardAdapter }
        mainViewModel.apply {
            getRestaurants()
            getComments()
            getFavorites(token)
        }
    }

    private fun setUpListeners() {
        mainViewModel.apply {
            restaurantList.observe(viewLifecycleOwner) {
                restaurantCardAdapter.setRestaurantList(it)
                binding.progress.isVisible(false)
            }
            commentList.observe(viewLifecycleOwner) {
                restaurantCardAdapter.setCommentList(it)
                binding.progress.isVisible(false)
            }
            favoriteList.observe(viewLifecycleOwner) {
                restaurantCardAdapter.setFavoriteList(it)
                binding.progress.isVisible(false)
            }
            error.observe(viewLifecycleOwner) { binding.root.createSnackBar(it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.restaurant_menu, menu)
        menu.findItem(R.id.miSearch).setOnActionExpandListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        (item!!.actionView as SearchView).apply {
            queryHint = getString(R.string.search_for_restaurants)
            maxWidth = Int.MAX_VALUE
            setOnQueryTextListener(this@FragmentHome)
        }
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?) = true

    override fun onQueryTextSubmit(query: String?) = requireActivity().run {
        hideKeyboard()
        currentFocus?.clearFocus()
        true
    }

    override fun onQueryTextChange(newText: String?) =
        restaurantCardAdapter.searchRestaurants(newText).run { true }

    override fun onEmpty() = binding.nothingDisplay.isVisible(true)

    override fun onNotEmpty() = binding.nothingDisplay.isVisible(false)

    override fun addToFav(restaurantId: Int) = mainViewModel.addToFav(token, restaurantId)

    override fun removeFav(favoriteId: Int) = mainViewModel.removeFav(token, favoriteId)
}
