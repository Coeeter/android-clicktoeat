package musicpractice.com.coeeter.clicktoeat.adapters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.activities.RestaurantActivity
import musicpractice.com.coeeter.clicktoeat.repository.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.repository.models.FavoriteModel
import musicpractice.com.coeeter.clicktoeat.repository.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.FavoriteViewModel

class RestaurantCardAdapter(
    private val context: Context,
    private val nothingToDisplayView: LinearLayout,
    private val token: String,
    private val layoutMode: Int
) : RecyclerView.Adapter<RestaurantCardAdapter.ViewHolder>(), Filterable {

    companion object {
        const val HOME_PAGE = 0
        const val FAVORITE_PAGE = 1
    }

    private var restaurantList = ArrayList<RestaurantModel>()
    private var commentList = ArrayList<CommentModel>()
    private var favoriteList = ArrayList<FavoriteModel>()

    private var originalRestaurantList = restaurantList

    fun setRestaurantList(restaurantList: ArrayList<RestaurantModel>) {
        val filteredRestaurantArray = ArrayList<RestaurantModel>()
        filteredRestaurantArray.addAll(restaurantList)
        if (layoutMode == FAVORITE_PAGE) {
            filteredRestaurantArray.clear()
            for (restaurant in restaurantList) {
                for (favorite in favoriteList) {
                    if (favorite.restaurantID == restaurant._id) {
                        filteredRestaurantArray.add(restaurant)
                    }
                }
            }
        }
        this.restaurantList = filteredRestaurantArray
        this.originalRestaurantList = this.restaurantList
        notifyDataSetChanged()
    }

    fun setCommentList(commentList: ArrayList<CommentModel>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }

    fun setFavoriteList(favoriteList: ArrayList<FavoriteModel>) {
        this.favoriteList = favoriteList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.restaurantTitle)
        val brandImage: ImageView = view.findViewById(R.id.brandImage)
        val favBtn: ImageView = view.findViewById(R.id.addToFavs)
        val avgRating: TextView = view.findViewById(R.id.avgRating)
        val reviewCount: TextView = view.findViewById(R.id.totalRating)
        val distance: TextView = view.findViewById(R.id.distance)
        val tags: RecyclerView = view.findViewById(R.id.recycler)
        val parent: ConstraintLayout = view.findViewById(R.id.parent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.restaurant_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant: RestaurantModel = restaurantList[position]

        holder.title.text = restaurant.name

        val imageUrl =
            "${context.getString(R.string.base_url)}/public/${restaurant.image}"
        Picasso.with(context).load(imageUrl).into(holder.brandImage)

        val commentMap = restaurant.getAvgRatingAndCount(commentList)
        holder.avgRating.text = commentMap["average"]
        holder.reviewCount.text = commentMap["count"]

        val favIndex = getFavoriteIndex(restaurant)
        var isFav = false
        holder.favBtn.setImageResource(R.drawable.ic_favorite_border)
        if (favIndex != -1) {
            holder.favBtn.setImageResource(R.drawable.ic_favorite)
            isFav = true
        }

        holder.favBtn.setOnClickListener {
            if (!isFav) {
                FavoriteViewModel.createFavorite(token, restaurant._id)
                holder.favBtn.setImageResource(R.drawable.ic_favorite)
                val animation = AnimationUtils.loadAnimation(context, R.anim.heart_animation)
                holder.favBtn.startAnimation(animation)
                return@setOnClickListener
            }
            FavoriteViewModel.deleteFavorite(token, favIndex)
            holder.favBtn.setImageResource(R.drawable.ic_favorite_border)
        }

        val location = getLocation()
        val distance = restaurant.getDistance(location)
        holder.distance.text = distance
        holder.distance.visibility = View.VISIBLE
        if (distance == null) holder.distance.visibility = View.GONE

        holder.tags.apply {
            adapter = TagAdapter(restaurant.tags)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        for (view in arrayOf(holder.brandImage, holder.title, holder.parent)) {
            view.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, RestaurantActivity::class.java)
                intent.putExtra("position", restaurant._id)
                context.startActivity(intent)
                (context as AppCompatActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.stay_still
                )
            })
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    private fun getFavoriteIndex(restaurant: RestaurantModel): Int {
        for (fav in favoriteList) {
            if (fav.restaurantID == restaurant._id) {
                return fav._id
            }
        }
        return -1
    }

    private fun getLocation(): HashMap<String, Double?>? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }
        try {
            val userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            var longitude = userLocation?.longitude
            var latitude = userLocation?.latitude
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10F
            ) { location ->
                longitude = location.longitude
                latitude = location.latitude
            }
            val hashMap = HashMap<String, Double?>()
            hashMap["longitude"] = longitude
            hashMap["latitude"] = latitude
            return hashMap
        } catch (error: Exception) {
            error.printStackTrace()
        }
        return null
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null && constraint.isEmpty()) {
                    filterResults.values = originalRestaurantList
                    return filterResults
                }
                val searchTerm = constraint.toString().lowercase()
                val filteredRestaurants = ArrayList<RestaurantModel>()
                for (i in 0 until originalRestaurantList.size) {
                    if (originalRestaurantList[i].name
                            .lowercase().contains(searchTerm)
                    ) {
                        filteredRestaurants.add(originalRestaurantList[i])
                    }
                }
                filterResults.values = filteredRestaurants
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                restaurantList = results.values as ArrayList<RestaurantModel>
                nothingToDisplayView.visibility = View.GONE
                if (restaurantList.size == 0) {
                    nothingToDisplayView.visibility = View.VISIBLE
                }
                notifyDataSetChanged()
            }

        }
    }
}