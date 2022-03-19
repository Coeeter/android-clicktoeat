package musicpractice.com.coeeter.clicktoeat.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import org.json.JSONArray
import org.json.JSONObject

class RestaurantCardAdapter(private val restaurantList: JSONArray, private val context: Context): RecyclerView.Adapter<RestaurantCardAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.restaurantTitle)
        val brandImage: ImageView = view.findViewById(R.id.brandImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant: JSONObject = restaurantList.get(position) as JSONObject

        holder.title.text = restaurant.getString("name")

        val imageUrl = "http://10.0.2.2:8080/public/${restaurant.getString("image")}"
        Picasso.with(context).load(imageUrl).into(holder.brandImage)
    }

    override fun getItemCount(): Int {
        return restaurantList.length()
    }
}