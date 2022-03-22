package musicpractice.com.coeeter.clicktoeat.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import org.json.JSONArray
import org.json.JSONObject

class RestaurantCardAdapter(
    private var restaurantList: JSONArray,
    private val context: Context
    ): RecyclerView.Adapter<RestaurantCardAdapter.ViewHolder>(), Filterable{

    private val originalRestaurantList = restaurantList

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.restaurantTitle)
        val brandImage: ImageView = view.findViewById(R.id.brandImage)
        val favBtn: ImageView = view.findViewById(R.id.addToFavs)
        val avgRating: TextView = view.findViewById(R.id.avgRating)
        val totalRating: TextView = view.findViewById(R.id.totalRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant: JSONObject = restaurantList.get(position) as JSONObject

        holder.title.text = restaurant.getString("name")

        val imageUrl = "${context.getString(R.string.base_url)}/public/${restaurant.getString("image")}"
        Picasso.with(context).load(imageUrl).into(holder.brandImage)
    }

    override fun getItemCount(): Int {
        return restaurantList.length()
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                if (constraint != null && constraint.isEmpty()) {
                    filterResults.values = originalRestaurantList
                    return filterResults
                }

                val searchTerm = constraint.toString().lowercase()
                val jsonArray = JSONArray()
                for (i in 0 until originalRestaurantList.length()) {
                    if ((originalRestaurantList.get(i) as JSONObject).get("name").toString().lowercase().contains(searchTerm)) {
                        jsonArray.put(originalRestaurantList.get(i))
                    }
                }
                filterResults.values = jsonArray
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                restaurantList = results?.values as JSONArray
                notifyDataSetChanged()
            }

        }
    }
}