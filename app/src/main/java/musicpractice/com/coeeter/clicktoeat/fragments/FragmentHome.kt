package musicpractice.com.coeeter.clicktoeat.fragments

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.activities.LoginActivity
import musicpractice.com.coeeter.clicktoeat.adapters.RestaurantCardAdapter
import musicpractice.com.coeeter.clicktoeat.repository.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.CommentViewModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.FavoriteViewModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.RestaurantViewModel

class FragmentHome : Fragment() {
    private lateinit var restaurantCardAdapter: RestaurantCardAdapter
    private lateinit var restaurantList: ArrayList<RestaurantModel>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val token = view.context.getSharedPreferences("memory", Context.MODE_PRIVATE)
            .getString("token", "")!!

        (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))
        recyclerView = view.findViewById<RecyclerView>(R.id.homeLayout)

        restaurantCardAdapter = RestaurantCardAdapter(
            activity as Context,
            view.findViewById<LinearLayout>(R.id.nothingDisplay),
            token,
            RestaurantCardAdapter.HOME_PAGE
        )

        val restaurantList = RestaurantViewModel.getAllRestaurants()
        val commentList = CommentViewModel.getAllComments()
        val favoriteList = FavoriteViewModel.getAllFavorites(token)

        restaurantList.observe(viewLifecycleOwner, Observer {
            restaurantCardAdapter.setRestaurantList(it)
        })

        commentList.observe(viewLifecycleOwner, Observer {
            restaurantCardAdapter.setCommentList(it)
        })

        favoriteList.observe(viewLifecycleOwner, Observer {
            restaurantCardAdapter.setFavoriteList(it)
        })

        recyclerView.apply {
            adapter = restaurantCardAdapter
            layoutManager = GridLayoutManager(view.context, 2)
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                layoutManager = GridLayoutManager(view.context, 4)
            setHasFixedSize(true)
            setItemViewCacheSize(10)
        }
        view.findViewById<ProgressBar>(R.id.progress).visibility = View.GONE

        return view
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
                        LoginActivity.hideKeyboard(activity as Activity)
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