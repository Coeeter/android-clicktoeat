package musicpractice.com.coeeter.clicktoeat.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.data.models.FavoriteModel
import musicpractice.com.coeeter.clicktoeat.data.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.data.network.RetrofitClient
import musicpractice.com.coeeter.clicktoeat.data.repositories.FavoriteRepository
import musicpractice.com.coeeter.clicktoeat.databinding.RestaurantCardBinding
import musicpractice.com.coeeter.clicktoeat.ui.home.HomeViewModel
import musicpractice.com.coeeter.clicktoeat.ui.restaurant.RestaurantActivity
import musicpractice.com.coeeter.clicktoeat.utils.Coroutine

class RestaurantCardAdapter(
    private var restaurantList: ArrayList<RestaurantModel>,
    private var commentList: ArrayList<CommentModel>,
    private var favoriteList: ArrayList<FavoriteModel>,
    private val context: Context,
    private val token: String
) : RecyclerView.Adapter<RestaurantCardAdapter.ViewHolder>() {
    private val originalRestaurantList = ArrayList<RestaurantModel>()

    init {
        originalRestaurantList.addAll(restaurantList)
    }

    var listener: HomeViewModel.HomeViewModelListener? = null
    private var recyclerView: RecyclerView? = null

    class ViewHolder(val binding: RestaurantCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        this.recyclerView?.apply {
            layoutManager =
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    GridLayoutManager(context, 2)
                else GridLayoutManager(context, 4)
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RestaurantCardBinding>(
            LayoutInflater.from(context),
            R.layout.restaurant_card,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant: RestaurantModel = restaurantList[position]

        holder.binding.restaurantTitle.text = restaurant.name

        val imageUrl =
            "${context.getString(R.string.base_url)}/public/${restaurant.image}"
        Picasso.with(context).load(imageUrl).into(holder.binding.brandImage)

        val commentMap = restaurant.getAvgRatingAndCount(commentList)
        holder.binding.avgRating.text = commentMap.averageRating
        holder.binding.totalRating.text = commentMap.count

        val favoriteId = favoriteList.find { it.restaurantID == restaurant._id }?._id ?: -1
        var isFav = false
        holder.binding.addToFavs.setImageResource(R.drawable.ic_favorite_border)
        if (favoriteId != -1) {
            holder.binding.addToFavs.setImageResource(R.drawable.ic_favorite)
            isFav = true
        }

        holder.binding.addToFavs.setOnClickListener {
            val repository = FavoriteRepository(RetrofitClient.favoriteService)
            if (!isFav) {
                Coroutine.main {
                    val response = repository.createFavorites(token, restaurant._id)
                    if (response.body() == null || response.body()!!.affectedRows != 1)
                        return@main
                    val animation = AnimationUtils.loadAnimation(context, R.anim.heart_animation)
                    animation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) =
                            (it as ImageView).setImageResource(R.drawable.ic_favorite)
                    })
                    it.startAnimation(animation)
                    repository.getUserFavorites(token).body()?.let {
                        this.favoriteList = it
                        notifyItemChanged(position)
                    }
                }
            }
            Coroutine.main {
                val response = repository.deleteFavorites(token, favoriteId)
                if (response.body() == null || response.body()!!.affectedRows != 1)
                    return@main
                repository.getUserFavorites(token).body()?.let {
                    this.favoriteList = it
                    notifyItemChanged(position)
                }
            }
        }

        holder.binding.distance.text = restaurant.getDistance(getLocation())

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
                (context as Activity).apply {
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.stay_still
                    )
                }
            })
        }
    }

    override fun getItemCount(): Int = restaurantList.size

    @SuppressLint("MissingPermission")
    private fun getLocation(): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            val location = Location(LocationManager.GPS_PROVIDER).apply {
                this.latitude = latitude!!
                this.longitude = longitude!!
            }
            return location
        } catch (error: Exception) {
            error.printStackTrace()
        }
        return null
    }

    fun searchRestaurants(searchTerm: String?) {
        val filteredRestaurants = ArrayList<RestaurantModel>()
        filteredRestaurants.addAll(originalRestaurantList)
        if (!searchTerm.isNullOrEmpty()) {
            filteredRestaurants.clear()
            val transformedSearchTerm = searchTerm.lowercase().trim()
            for (restaurant in originalRestaurantList)
                if (restaurant.name.lowercase().contains(transformedSearchTerm))
                    filteredRestaurants.add(restaurant)
        }
        for (i in filteredRestaurants.indices) {
            if (filteredRestaurants[i] !in restaurantList) {
                restaurantList.add(i, filteredRestaurants[i])
                val arrayList = ArrayList<String>()
                for (restaurant in restaurantList)
                    arrayList.add(restaurant.name)
                notifyItemInserted(i)
            }
        }
        for (i in restaurantList.indices.reversed()) {
            if (restaurantList[i] !in filteredRestaurants) {
                restaurantList.removeAt(i)
                notifyItemRemoved(i)
            }
        }
        listener?.hideNotice()
        if (filteredRestaurants.size == 0) listener?.showNotice()
        recyclerView?.scrollToPosition(0)
    }
}