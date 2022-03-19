package musicpractice.com.coeeter.clicktoeat.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.login.LoginActivity
import musicpractice.com.coeeter.clicktoeat.recycler.RestaurantCardAdapter
import org.json.JSONArray

class FragmentHome : Fragment() {
    private lateinit var getAllRestaurantLink: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        getAllRestaurantLink = "${getString(R.string.base_url)}/restaurants"
        val recyclerView = view.findViewById<RecyclerView>(R.id.homeLayout)
        val queue = Volley.newRequestQueue(view.context)
        val request = JsonArrayRequest(Request.Method.GET, getAllRestaurantLink, null,
            {
                response: JSONArray ->
                run {
                    recyclerView.adapter = RestaurantCardAdapter(response, this.activity as Context)
                    recyclerView.layoutManager = GridLayoutManager(view.context, 2)
                    recyclerView.setHasFixedSize(true)
                    recyclerView.setItemViewCacheSize(10)
                }
            },
            { error: VolleyError -> Log.d("error", error.toString()) }
        )
        queue.add(request)
        return view
    }
}