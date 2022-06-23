package musicpractice.com.coeeter.clicktoeat.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentFavoritesBinding
import musicpractice.com.coeeter.clicktoeat.ui.adapters.RestaurantCardAdapter
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.getStringFromSharedPref
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import musicpractice.com.coeeter.clicktoeat.utils.isVisible

@AndroidEntryPoint
class FragmentFavorites :
    Fragment(),
    MenuItem.OnActionExpandListener,
    SearchView.OnQueryTextListener,
    RestaurantCardAdapter.OnDataSetChangedListener {

    private lateinit var restaurantCardAdapter: RestaurantCardAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var filterElements: ArrayList<CheckBox>
    private lateinit var token: String
    private var searchItem: MenuItem? = null
    private var searchable = false
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        setUpListeners()
        initViews()
        return binding.root
    }

    private fun initViews() {
        token = requireActivity()
            .getStringFromSharedPref(
                requireContext().getString(R.string.sharedPrefName),
                requireContext().getString(R.string.sharedPrefToken)
            )
        restaurantCardAdapter = RestaurantCardAdapter(
            (activity as Context),
            RestaurantCardAdapter.FAVORITE_PAGE
        ).apply { dataSetListener = this@FragmentFavorites }
        recyclerView = binding.homeLayout.apply { adapter = restaurantCardAdapter }
        mainViewModel.apply {
            getRestaurants()
            getComments()
            getFavorites(token)
        }
        filterElements = arrayListOf(
            binding.asianCheck,
            binding.fastFoodCheck,
            binding.fineCheck,
            binding.fusionCheck,
            binding.halalCheck,
            binding.westernCheck,
        )
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
                if (it.size == 0) return@observe run {
                    binding.noFavs.isVisible(true)
                    searchable = false
                }
                binding.noFavs.isVisible(false)
                searchable = true
            }
            error.observe(viewLifecycleOwner) { binding.root.createSnackBar(it) }
        }
        binding.apply {
            reset.setOnClickListener { resetFilters() }
            apply.setOnClickListener { filterRestaurants() }
        }
    }

    private fun filterRestaurants() {
        val filters = ArrayList<String>()
        filterElements.forEach {
            if (it.isChecked) {
                when (it.id) {
                    R.id.asianCheck -> filters.add(getString(R.string.asian_cuisine))
                    R.id.fastFoodCheck -> filters.add(getString(R.string.fast_food))
                    R.id.fineCheck -> filters.add(getString(R.string.fine_dining))
                    R.id.fusionCheck -> filters.add(getString(R.string.fusion_cuisine))
                    R.id.halalCheck -> filters.add(getString(R.string.halal))
                    R.id.westernCheck -> filters.add(getString(R.string.western_cuisine))
                }
            }
        }
        restaurantCardAdapter.filterRestaurants(mainViewModel.setCategoryFilters(filters, true))
        binding.drawerLayout.closeDrawer(GravityCompat.END)
    }

    private fun resetFilters() {
        filterElements.forEach { it.isChecked = false }
        restaurantCardAdapter.filterRestaurants(mainViewModel.setCategoryFilters(arrayListOf(), true))
        binding.drawerLayout.closeDrawer(GravityCompat.END)
    }

    private fun resetSearchTerm() {
        requireActivity().apply {
            hideKeyboard()
            currentFocus?.clearFocus()
        }
        searchItem!!.collapseActionView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.restaurant_menu, menu)
        searchItem = menu.findItem(R.id.miSearch)
        searchItem!!.setOnActionExpandListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miSearch -> if (searchable) item.expandActionView() else false
            R.id.miFilter -> {
                if (searchItem?.isActionViewExpanded == false && searchable)
                    return binding.drawerLayout.openDrawer(GravityCompat.END).run { true }
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        (item!!.actionView as SearchView).apply {
            queryHint = getString(R.string.search_for_restaurants)
            maxWidth = Int.MAX_VALUE
            setOnQueryTextListener(this@FragmentFavorites)
        }
        resetFilters()
        return searchable
    }

    override fun onMenuItemActionCollapse(item: MenuItem?) = true

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!searchable) return false
        requireActivity().hideKeyboard()
        activity?.currentFocus?.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (!searchable) return false
        restaurantCardAdapter.filterRestaurants(mainViewModel.setSearchQuery(newText, true))
        return true
    }
    override fun onEmpty() = binding.nothingDisplay.isVisible(true)
    override fun onNotEmpty() = binding.nothingDisplay.isVisible(false)
    override fun addToFav(restaurantId: Int) = mainViewModel.addToFav(token, restaurantId)
    override fun removeFav(favoriteId: Int) = mainViewModel.removeFav(token, favoriteId)

    override fun onStop() {
        resetSearchTerm()
        resetFilters()
        super.onStop()
    }

    override fun onDestroy() {
        resetSearchTerm()
        resetFilters()
        super.onDestroy()
    }

}