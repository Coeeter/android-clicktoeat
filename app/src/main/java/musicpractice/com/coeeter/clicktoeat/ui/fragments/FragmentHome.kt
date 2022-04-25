package musicpractice.com.coeeter.clicktoeat.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentHomeBinding
import musicpractice.com.coeeter.clicktoeat.ui.adapters.RestaurantCardAdapter
import musicpractice.com.coeeter.clicktoeat.utils.InjectorUtils
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import musicpractice.com.coeeter.clicktoeat.utils.isVisible
import musicpractice.com.coeeter.clicktoeat.viewmodels.CommentViewModel
import musicpractice.com.coeeter.clicktoeat.viewmodels.FavoriteViewModel
import musicpractice.com.coeeter.clicktoeat.viewmodels.RestaurantViewModel

class FragmentHome : Fragment() {
    private lateinit var restaurantCardAdapter: RestaurantCardAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val token = requireActivity().getSharedPreferences("memory", Context.MODE_PRIVATE)
            .getString("token", "")!!

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        recyclerView = binding.homeLayout

        restaurantCardAdapter = RestaurantCardAdapter(
            activity as Context,
            binding.nothingDisplay,
            token,
            RestaurantCardAdapter.HOME_PAGE
        )

        val restaurantViewModel = ViewModelProvider(
            this,
            InjectorUtils.provideRestaurantViewModelFactory()
        )[RestaurantViewModel::class.java]
        val commentViewModel = ViewModelProvider(
            this,
            InjectorUtils.provideCommentViewModelFactory()
        )[CommentViewModel::class.java]
        val favoriteViewModel = ViewModelProvider(
            this,
            InjectorUtils.provideFavoriteViewModelFactory()
        )[FavoriteViewModel::class.java]

        restaurantViewModel.getRestaurantList().observe(viewLifecycleOwner, Observer {
            restaurantCardAdapter.setRestaurantList(it)
            binding.progress.isVisible(false)
        })
        restaurantViewModel.getAllRestaurants()

        commentViewModel.getCommentList().observe(viewLifecycleOwner, Observer {
            restaurantCardAdapter.setCommentList(it)
        })
        commentViewModel.getAllComments()

        favoriteViewModel.getFavoriteList().observe(viewLifecycleOwner, Observer {
            restaurantCardAdapter.setFavoriteList(it)
        })
        favoriteViewModel.getAllFavorites(token)

        recyclerView.apply {
            adapter = restaurantCardAdapter
            layoutManager = GridLayoutManager(context, 2)
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                layoutManager = GridLayoutManager(context, 4)
            setHasFixedSize(true)
            setItemViewCacheSize(10)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.restaurant_menu, menu)
        val searchItem = menu.findItem(R.id.miSearch)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                val searchView = item!!.actionView as SearchView
                searchView.queryHint = "Search for restaurants"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        requireActivity().hideKeyboard()
                        searchView.clearFocus()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        restaurantCardAdapter.filter.filter(newText)
                        return true
                    }
                })
                searchView.maxWidth = Int.MAX_VALUE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

}