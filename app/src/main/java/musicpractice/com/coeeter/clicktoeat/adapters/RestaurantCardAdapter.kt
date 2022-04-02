package musicpractice.com.coeeter.clicktoeat.adapters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.apiClient.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.apiClient.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.apiClient.models.DefaultResponseModel
import musicpractice.com.coeeter.clicktoeat.apiClient.models.FavoriteModel
import musicpractice.com.coeeter.clicktoeat.apiClient.models.RestaurantModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.*

class RestaurantCardAdapter(
    private var restaurantList: ArrayList<RestaurantModel>,
    private var commentList: ArrayList<CommentModel>,
    private var favoriteList: ArrayList<FavoriteModel>,
    private val context: Context,
    private val nothingToDisplayView: LinearLayout,
    private val token: String
) : RecyclerView.Adapter<RestaurantCardAdapter.ViewHolder>(), Filterable {

    private val originalRestaurantList = restaurantList

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.restaurantTitle)
        val brandImage: ImageView = view.findViewById(R.id.brandImage)
        val favBtn: ImageView = view.findViewById(R.id.addToFavs)
        val avgRating: TextView = view.findViewById(R.id.avgRating)
        val reviewCount: TextView = view.findViewById(R.id.totalRating)
        val distance: TextView = view.findViewById(R.id.distance)
        val tags: RecyclerView = view.findViewById(R.id.recycler)
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

        val commentMap = getAvgRatingAndCount(restaurant)
        holder.avgRating.text = commentMap["average"]
        holder.reviewCount.text = commentMap["count"]

        val favIndex = getFavoriteIndex(restaurant)
        holder.favBtn.setImageResource(R.drawable.ic_favorite_border)
        holder.favBtn.tag = "notFav"
        if (favIndex != -1) {
            holder.favBtn.setImageResource(R.drawable.ic_favorite)
            holder.favBtn.tag = "fav $favIndex"
        }

        holder.favBtn.setOnClickListener {
            if (holder.favBtn.tag.toString() == "notFav")
                return@setOnClickListener addToFav(holder.favBtn, restaurant._id)
            removeFromFav(holder.favBtn)
        }

        val location = getLocation()
        val distance =
            if (location == null) null
            else getDistance(getLocation()!!, restaurant)
        holder.distance.text = distance
        holder.distance.visibility = View.VISIBLE
        if (distance == null) holder.distance.visibility = View.GONE

        holder.tags.apply {
            adapter = TagAdapter(restaurant.tags)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    private fun getAvgRatingAndCount(restaurant: RestaurantModel): HashMap<String, String> {
        var totalRating = 0.0
        var reviewCount = 0.0
        for (comment in commentList) {
            if (comment.restaurantId == restaurant._id) {
                totalRating += comment.rating
                reviewCount++
            }
        }
        var averageRating = 0.0
        if (reviewCount > 0) {
            averageRating += totalRating / reviewCount
        }
        val hashMap = HashMap<String, String>()
        if (averageRating.toString().substring(averageRating.toString().length - 2) == ".0") {
            hashMap["average"] = averageRating.toInt().toString()
        } else {
            hashMap["average"] = ((averageRating * 100).roundToInt() / 100.0).toString()
        }
        hashMap["count"] = "(${reviewCount.toInt()})"
        return hashMap
    }

    private fun updateFav() {
        RetrofitClient.favoriteService.getUserFavorites(token)
            .enqueue(object : Callback<ArrayList<FavoriteModel>?> {
                override fun onResponse(
                    call: Call<ArrayList<FavoriteModel>?>,
                    response: Response<ArrayList<FavoriteModel>?>
                ) {
                    if (response.body() == null) return
                    favoriteList = response.body()!!
                }

                override fun onFailure(call: Call<ArrayList<FavoriteModel>?>, t: Throwable) {
                    Log.d("poly", t.message.toString())
                }
            })
    }

    private fun getFavoriteIndex(restaurant: RestaurantModel): Int {
        for (fav in favoriteList) {
            if (fav.restaurantID == restaurant._id) {
                return fav._id
            }
        }
        return -1
    }

    private fun addToFav(favBtn: ImageView, restaurantId: Int) {
        RetrofitClient.favoriteService.createFavorites(token, restaurantId)
            .enqueue(object : Callback<DefaultResponseModel?> {
                override fun onResponse(
                    call: Call<DefaultResponseModel?>,
                    response: Response<DefaultResponseModel?>
                ) {
                    if (response.body() != null && response.body()!!.affectedRows == 1) {
                        favBtn.tag = "fav ${response.body()!!.insertId}"
                        favBtn.setImageResource(R.drawable.ic_favorite)
                        val animation =
                            AnimationUtils.loadAnimation(context, R.anim.heart_animation)
                        favBtn.startAnimation(animation)
                        updateFav()
                        return
                    }
                    if (response.body()!!.result!!.contains("Invalid")) {
                        Toast.makeText(context, response.body()!!.result, Toast.LENGTH_LONG).show()
                    }
                    return
                }

                override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                    Log.d("poly", t.message.toString())
                }
            })
    }

    private fun removeFromFav(favBtn: ImageView) {
        val favoriteId = favBtn.tag.toString().split(" ")[1].toInt()
        RetrofitClient.favoriteService.deleteFavorites(token, favoriteId)
            .enqueue(object : Callback<DefaultResponseModel?> {
                override fun onResponse(
                    call: Call<DefaultResponseModel?>,
                    response: Response<DefaultResponseModel?>
                ) {
                    if (response.body() != null && response.body()!!.affectedRows == 1) {
                        favBtn.tag = "notFav"
                        favBtn.setImageResource(R.drawable.ic_favorite_border)
                        updateFav()
                        return
                    }
                    if (response.body()?.result?.contains("Invalid") == true) {
                        Toast.makeText(context, response.body()!!.result, Toast.LENGTH_LONG).show()
                    }
                    return
                }

                override fun onFailure(call: Call<DefaultResponseModel?>, t: Throwable) {
                    Log.d("poly", t.message.toString())
                }
            })
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

    private fun getDistance(
        userLocation: HashMap<String, Double?>,
        restaurant: RestaurantModel
    ): String? {
        val userLongitude = userLocation["longitude"]
        val userLatitude = userLocation["latitude"]
        if (userLongitude == null || userLatitude == null) return null

        val differenceInLatitude =
            restaurant.latitude * (Math.PI / 180) - userLatitude * (Math.PI / 180)
        val differenceInLongitude =
            restaurant.longitude * (Math.PI / 180) - userLongitude * (Math.PI / 180)

        val a = sin(differenceInLatitude / 2).pow(2.0) +
                cos(restaurant.latitude * (Math.PI / 180)) *
                cos(userLatitude * (Math.PI / 180)) *
                sin(differenceInLongitude / 2).pow(2.0)
        val c = 2 * asin(sqrt(a))
        var distance = (c * 6371 * 1000).roundToInt() / 1000.0
        if (distance < 1) {
            distance *= 1000
            return distance.toInt().toString() + "m"
        }
        distance = (distance * 100).roundToInt() / 100.0
        return distance.toString() + "km"
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