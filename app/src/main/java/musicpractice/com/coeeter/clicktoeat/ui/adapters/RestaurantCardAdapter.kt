package musicpractice.com.coeeter.clicktoeat.ui.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.RestaurantCardBinding
import musicpractice.com.coeeter.clicktoeat.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.models.FavoriteModel
import musicpractice.com.coeeter.clicktoeat.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.ui.activities.RestaurantActivity
import musicpractice.com.coeeter.clicktoeat.utils.InjectorUtils
import musicpractice.com.coeeter.clicktoeat.utils.isVisible
import musicpractice.com.coeeter.clicktoeat.viewmodels.FavoriteViewModel

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
        setRestaurantList(this.restaurantList)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: RestaurantCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RestaurantCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant: RestaurantModel = restaurantList[position]

        holder.binding.restaurantTitle.text = restaurant.name

        val imageUrl =
            "${context.getString(R.string.base_url)}/public/${restaurant.image}"
        Picasso.with(context).load(imageUrl).into(holder.binding.brandImage)

        val commentMap = restaurant.getAvgRatingAndCount(commentList)
        holder.binding.avgRating.text = commentMap["average"]
        holder.binding.totalRating.text = commentMap["count"]

        val favIndex = getFavoriteIndex(restaurant)
        var isFav = false
        holder.binding.addToFavs.setImageResource(R.drawable.ic_favorite_border)
        if (favIndex != -1) {
            holder.binding.addToFavs.setImageResource(R.drawable.ic_favorite)
            isFav = true
        }

        val viewModel = ViewModelProvider(
            (context as AppCompatActivity),
            InjectorUtils.provideFavoriteViewModelFactory()
        )[FavoriteViewModel::class.java]
        holder.binding.addToFavs.setOnClickListener {
            if (!isFav) {
                viewModel.getCreateFavoriteResult()
                    .observe(context, Observer {
                        if (it.affectedRows != 1) return@Observer
                        viewModel.getAllFavorites(token)
                    })
                viewModel.addFavorite(token, restaurant._id)
                return@setOnClickListener
            }
            viewModel.getDeleteFavoriteResult().observe(context, Observer {
                if (it.affectedRows != 1) return@Observer
                viewModel.getAllFavorites(token)
            })
            viewModel.removeFavorite(token, favIndex)
        }

        val location = getLocation()
        val distance = restaurant.getDistance(location)
        holder.binding.distance.text = distance
        holder.binding.distance.isVisible(true)
        if (distance == null) holder.binding.distance.isVisible(false)

        holder.binding.tags.apply {
            adapter = TagAdapter(restaurant.tags)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        for (view in arrayOf(
            holder.binding.brandImage,
            holder.binding.restaurantTitle,
            holder.binding.parent
        )) {
            view.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, RestaurantActivity::class.java)
                intent.putExtra("position", restaurant._id)
                context.startActivity(intent)
                context.overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.stay_still
                )
            })
        }
    }

    override fun getItemCount(): Int = restaurantList.size

    private fun getFavoriteIndex(restaurant: RestaurantModel): Int {
        for (fav in favoriteList) {
            if (fav.restaurantID == restaurant._id) {
                return fav._id
            }
        }
        return -1
    }

    @SuppressLint("MissingPermission")
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
                nothingToDisplayView.isVisible(false)
                if (restaurantList.size == 0) nothingToDisplayView.isVisible(true)
                notifyDataSetChanged()
            }

        }
    }
}