package musicpractice.com.coeeter.clicktoeat.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.models.Comment
import musicpractice.com.coeeter.clicktoeat.data.models.Favorite
import musicpractice.com.coeeter.clicktoeat.data.models.Restaurant
import musicpractice.com.coeeter.clicktoeat.databinding.RecyclerRestaurantItemBinding
import musicpractice.com.coeeter.clicktoeat.ui.restaurant.RestaurantActivity
import musicpractice.com.coeeter.clicktoeat.utils.every
import musicpractice.com.coeeter.clicktoeat.utils.isVisible
import musicpractice.com.coeeter.clicktoeat.utils.nextActivity

class RestaurantCardAdapter(
    private val context: Context,
    private val layoutMode: Int
) : RecyclerView.Adapter<RestaurantCardAdapter.ViewHolder>() {

    companion object {
        const val HOME_PAGE = 0
        const val FAVORITE_PAGE = 1
    }

    private val restaurantList = ArrayList<Restaurant>()
    private val commentList = ArrayList<Comment>()
    private val favoriteList = ArrayList<Favorite>()
    private val originalRestaurantList = ArrayList<Restaurant>()
    private var recyclerView: RecyclerView? = null
    var dataSetListener: OnDataSetChangedListener? = null

    fun setRestaurantList(restaurantList: ArrayList<Restaurant>) {
        val filteredRestaurantArray = ArrayList<Restaurant>()
        filteredRestaurantArray.addAll(restaurantList)
        if (layoutMode == FAVORITE_PAGE) {
            filteredRestaurantArray.clear()
            val favIdList = favoriteList.map { it.restaurantID }
            filteredRestaurantArray.addAll(restaurantList.filter { it._id in favIdList })
        }
        this.restaurantList.apply {
            clear()
            addAll(filteredRestaurantArray)
        }
        this.originalRestaurantList.apply {
            clear()
            addAll(this@RestaurantCardAdapter.restaurantList)
        }
        notifyDataSetChanged()
    }

    fun setCommentList(commentList: ArrayList<Comment>) {
        this.commentList.apply {
            clear()
            addAll(commentList)
        }
        notifyDataSetChanged()
    }

    fun setFavoriteList(favoriteList: ArrayList<Favorite>) {
        this.favoriteList.apply {
            clear()
            addAll(favoriteList)
        }
        setRestaurantList(this.restaurantList)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: RecyclerRestaurantItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerRestaurantItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurantList[position]

        holder.binding.restaurantTitle.text = restaurant.name

        val imageUrl =
            "${context.getString(R.string.base_url)}public/${restaurant.image}"
        Picasso.with(context).load(imageUrl).into(holder.binding.brandImage)

        val commentMap = restaurant.getAvgRatingAndCount(commentList)
        holder.binding.avgRating.text = commentMap["average"]
        holder.binding.totalRating.text = commentMap["count"]

        val favoriteId = favoriteList.find { it.restaurantID == restaurant._id }?._id ?: -1
        var isFav = false
        holder.binding.addToFavs.setImageResource(R.drawable.ic_favorite_border)
        if (favoriteId != -1) {
            holder.binding.addToFavs.setImageResource(R.drawable.ic_favorite)
            isFav = true
        }

        holder.binding.addToFavs.setOnClickListener {
            if (!isFav) {
                dataSetListener?.addToFav(restaurant._id)
                holder.binding.addToFavs.setImageResource(R.drawable.ic_favorite)
                val animation = AnimationUtils.loadAnimation(context, R.anim.heart_animation)
                holder.binding.addToFavs.startAnimation(animation)
                return@setOnClickListener
            }
            dataSetListener?.removeFav(favoriteId)
            holder.binding.addToFavs.setImageResource(R.drawable.ic_favorite_border)
        }

        holder.binding.progress.isVisible(true)
        getLocation().observe((context as LifecycleOwner), Observer {
            holder.binding.progress.isVisible(true)
            val distance = restaurant.getDistance(it)
                ?: return@Observer run {
                    holder.binding.distance.isVisible(false)
                    holder.binding.progress.isVisible(false)
                }
            holder.binding.distance.apply {
                holder.binding.progress.isVisible(false)
                text = distance
                isVisible(true)
            }
        })

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
            view.setOnClickListener {
                (context as AppCompatActivity).apply {
                    nextActivity(
                        Intent(
                            context,
                            RestaurantActivity::class.java
                        ).apply {
                            putExtra("position", restaurant._id)
                        }
                    )
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView.apply {
            layoutManager =
                if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    GridLayoutManager(context, 2)
                else GridLayoutManager(context, 4)
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    override fun getItemCount() = restaurantList.size

    @SuppressLint("MissingPermission")
    private fun getLocation(): LiveData<Location?> {
        val locationLiveData = MutableLiveData<Location?>()
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            val userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            locationLiveData.postValue(userLocation)
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10F
            ) { locationLiveData.postValue(it) }
        } catch (error: Exception) {
            error.printStackTrace()
            locationLiveData.postValue(null)
        }
        return locationLiveData
    }

    fun filterRestaurants(filteredRestaurants: ArrayList<Restaurant>) {
        for (i in filteredRestaurants.indices) {
            restaurantList.find { it._id == filteredRestaurants[i]._id }
                ?: run {
                    restaurantList.add(i, filteredRestaurants[i])
                    notifyItemInserted(i)
                }
        }
        for (i in restaurantList.indices.reversed()) {
            filteredRestaurants.find { it._id == restaurantList[i]._id }
                ?: run {
                    restaurantList.removeAt(i)
                    notifyItemRemoved(i)
                }
        }
        recyclerView?.scrollToPosition(0)
        if (filteredRestaurants.size == 0) return dataSetListener!!.onEmpty()
        dataSetListener!!.onNotEmpty()
    }

    interface OnDataSetChangedListener {
        fun onEmpty()
        fun onNotEmpty()
        fun addToFav(restaurantId: Int)
        fun removeFav(favoriteId: Int)
    }
}