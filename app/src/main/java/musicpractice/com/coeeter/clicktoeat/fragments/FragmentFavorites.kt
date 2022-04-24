package musicpractice.com.coeeter.clicktoeat.fragments

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.activities.LoginActivity
import musicpractice.com.coeeter.clicktoeat.adapters.RestaurantCardAdapter
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentFavoritesBinding
import musicpractice.com.coeeter.clicktoeat.repository.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.CommentViewModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.FavoriteViewModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.RestaurantViewModel

class FragmentFavorites : Fragment() {
    private lateinit var restaurantCardAdapter: RestaurantCardAdapter
    private lateinit var restaurantList: ArrayList<RestaurantModel>
    private lateinit var recyclerView: RecyclerView
    private var searchable = false

    private lateinit var binding: FragmentFavoritesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val token = requireActivity().getSharedPreferences("memory", Context.MODE_PRIVATE)
            .getString("token", "")!!

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        recyclerView = binding.homeLayout

        restaurantCardAdapter = RestaurantCardAdapter(
            activity as Context,
            binding.nothingDisplay,
            token,
            RestaurantCardAdapter.FAVORITE_PAGE
        )

        val restaurantList = RestaurantViewModel.getAllRestaurants()
        val commentList = CommentViewModel.getAllComments()
        val favoriteList = FavoriteViewModel.getAllFavorites(token)

        restaurantList.observe(viewLifecycleOwner, Observer {
            restaurantCardAdapter.setRestaurantList(it)
            binding.progress.visibility = View.GONE
        })

        commentList.observe(viewLifecycleOwner, Observer {
            restaurantCardAdapter.setCommentList(it)
        })

        favoriteList.observe(viewLifecycleOwner, Observer {
            restaurantCardAdapter.setFavoriteList(it)
            searchable = true
            if (it.size == 0) {
                restaurantCardAdapter.setRestaurantList(ArrayList())
                binding.noFavs.visibility =
                    View.VISIBLE
                searchable = false
            }
        })

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
                        if (!searchable) return false
                        LoginActivity.hideKeyboard(activity as Activity)
                        searchView.clearFocus()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (!searchable) return false
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