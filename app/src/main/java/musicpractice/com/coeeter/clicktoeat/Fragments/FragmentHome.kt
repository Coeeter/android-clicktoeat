package musicpractice.com.coeeter.clicktoeat.Fragments

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.Api.VolleySingleton
import musicpractice.com.coeeter.clicktoeat.Activities.LoginActivity
import musicpractice.com.coeeter.clicktoeat.Adapters.RestaurantCardAdapter
import org.json.JSONArray

class FragmentHome : Fragment() {
    private lateinit var getAllRestaurantLink: String
    private lateinit var adapter: RestaurantCardAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))

        getAllRestaurantLink = "${getString(R.string.base_url)}/restaurants?d=mobile"
        val recyclerView = view.findViewById<RecyclerView>(R.id.homeLayout)
        val request = JsonArrayRequest(Request.Method.GET, getAllRestaurantLink, null,
            {
                response: JSONArray ->
                run {
                    try {
                        adapter = RestaurantCardAdapter(response, this.activity as Context)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            recyclerView.layoutManager = GridLayoutManager(view.context, 4)
                        recyclerView.setHasFixedSize(true)
                        recyclerView.setItemViewCacheSize(10)
                    } catch (e: Exception) {
                        e.stackTraceToString()
                    }
                }
            },
            { error: VolleyError -> Log.d("error", error.toString()) }
        )
        VolleySingleton.getInstance(view.context).addToQueue(request)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.restaurant_menu, menu)
        searchView = menu.findItem(R.id.miSearch).actionView as SearchView
        searchView.queryHint = "Search for restaurants"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoginActivity.hideKeyboard(activity as Activity)
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        searchView.maxWidth = Int.MAX_VALUE

        super.onCreateOptionsMenu(menu, inflater)
    }

}