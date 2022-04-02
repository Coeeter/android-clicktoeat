package musicpractice.com.coeeter.clicktoeat.fragments

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.activities.LoginActivity
import musicpractice.com.coeeter.clicktoeat.adapters.RestaurantCardAdapter
import musicpractice.com.coeeter.clicktoeat.apiClient.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.apiClient.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.apiClient.models.FavoriteModel
import musicpractice.com.coeeter.clicktoeat.apiClient.models.RestaurantModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentHome : Fragment() {
    private lateinit var restaurantCardAdapter: RestaurantCardAdapter
    private lateinit var searchView: SearchView
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

        val getAllRestaurantLink = "${getString(R.string.base_url)}/restaurants?d=mobile"

        RetrofitClient.restaurantService.getRestaurants()
            .enqueue(object : Callback<ArrayList<RestaurantModel>?> {
                override fun onResponse(
                    call: Call<ArrayList<RestaurantModel>?>,
                    restaurantresponse: Response<ArrayList<RestaurantModel>?>
                ) {
                    if (restaurantresponse.body() == null) return
                    RetrofitClient.commentService.getAllComments()
                        .enqueue(object : Callback<ArrayList<CommentModel>?> {
                            override fun onResponse(
                                call: Call<ArrayList<CommentModel>?>,
                                commentresponse: Response<ArrayList<CommentModel>?>
                            ) {
                                if (commentresponse.body() == null) return
                                RetrofitClient.favoriteService.getUserFavorites(token)
                                    .enqueue(object : Callback<ArrayList<FavoriteModel>?> {
                                        override fun onResponse(
                                            call: Call<ArrayList<FavoriteModel>?>,
                                            favoriteResponse: Response<ArrayList<FavoriteModel>?>
                                        ) {
                                            if (favoriteResponse.body() == null) return
                                            setUpRecyclerView(
                                                view,
                                                recyclerView,
                                                restaurantresponse.body()!!,
                                                commentresponse.body()!!,
                                                favoriteResponse.body()!!
                                            )
                                        }

                                        override fun onFailure(
                                            call: Call<ArrayList<FavoriteModel>?>,
                                            t: Throwable
                                        ) {
                                            Log.d("poly", t.message.toString())
                                        }
                                    })
                            }

                            override fun onFailure(
                                call: Call<ArrayList<CommentModel>?>,
                                t: Throwable
                            ) {
                                Log.d("poly", t.message.toString())
                            }
                        })
                }

                override fun onFailure(call: Call<ArrayList<RestaurantModel>?>, t: Throwable) {
                    Log.d("poly", t.message.toString())
                }
            })
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
                searchView = item!!.actionView as SearchView
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
                recyclerView.adapter!!.notifyDataSetChanged()
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpRecyclerView(
        view: View,
        recyclerView: RecyclerView,
        restaurantList: ArrayList<RestaurantModel>,
        commentList: ArrayList<CommentModel>,
        favoriteList: ArrayList<FavoriteModel>
    ) {
        try {
            val token = activity?.getSharedPreferences("memory", Context.MODE_PRIVATE)
                ?.getString("token", "")!!
            restaurantCardAdapter = RestaurantCardAdapter(
                restaurantList,
                commentList,
                favoriteList,
                activity as Context,
                view.findViewById<LinearLayout>(R.id.nothingDisplay),
                token
            )
            recyclerView.apply {
                adapter = restaurantCardAdapter
                layoutManager = GridLayoutManager(view.context, 2)
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                    layoutManager = GridLayoutManager(view.context, 4)
                setHasFixedSize(true)
                setItemViewCacheSize(10)
            }
            view.findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
        } catch (e: Exception) {
            e.stackTraceToString()
        }
    }
}