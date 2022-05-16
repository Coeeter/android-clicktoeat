package musicpractice.com.coeeter.clicktoeat.ui.restaurant

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityMapBinding
import musicpractice.com.coeeter.clicktoeat.databinding.RestaurantInfoWindowBinding
import musicpractice.com.coeeter.clicktoeat.utils.isVisible

@AndroidEntryPoint
class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private val restaurantViewModel: RestaurantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val restaurantId = intent.getIntExtra("restaurant", -1)
        if (restaurantId == -1) finish()

        binding.toolbar.setNavigationOnClickListener { finish() }

        restaurantViewModel.restaurantList.observe(this) {
            val restaurant = it.filter { restaurant -> restaurant._id == restaurantId }[0]
            val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
            val location = getLocation()
            if (resultCode == ConnectionResult.SUCCESS) {
                binding.mapWebView.isVisible(false)
                binding.mapFragment.isVisible(true)
                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
                mapFragment.getMapAsync { map ->
                    val restaurantLocation = LatLng(restaurant.latitude, restaurant.longitude)
                    val userLocation = location?.latitude?.let { lat ->
                        LatLng(lat, location.longitude)
                    }
                    map.addMarker(
                        MarkerOptions().position(restaurantLocation)
                            .title(restaurant.name)
                            .snippet(restaurant.address)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_icon))
                    )
                    map.moveCamera(CameraUpdateFactory.zoomTo(16f))
                    if (userLocation != null) {
                        map.addMarker(MarkerOptions().position(userLocation).title("Your location"))
                        map.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
                        map.setOnMapLoadedCallback {
                            map.animateCamera(
                                CameraUpdateFactory.newLatLng(restaurantLocation),
                                1000,
                                null
                            )
                        }
                    }else {
                        map.moveCamera(CameraUpdateFactory.newLatLng(restaurantLocation))
                    }
                    map.setInfoWindowAdapter(object: GoogleMap.InfoWindowAdapter {
                        override fun getInfoContents(p0: Marker): View? = null

                        override fun getInfoWindow(p0: Marker): View {
                            val restaurantInfoWindowBinding = RestaurantInfoWindowBinding.inflate(layoutInflater)
                            val title = restaurantInfoWindowBinding.restaurantTitle
                            val description = restaurantInfoWindowBinding.description

                            p0.title?.let { it -> title.text = it }
                            p0.snippet?.let { it ->
                                description.text = it
                                description.isVisible(true)
                            }
                            return restaurantInfoWindowBinding.root
                        }

                    })
                }
                return@observe
            }
            binding.mapWebView.isVisible(true)
            binding.mapFragment.isVisible(false)
            val query = restaurant.name.replace(" ", "%20").replace("&", "%zy81")
            if (location == null) return@observe
            binding.mapWebView.loadUrl(
                "${getString(R.string.base_url)}/restaurants/" +
                        "map?r=$query" +
                        "&lat=${location.latitude}" +
                        "&lon=${location.longitude}"
            )
            binding.mapWebView.settings.javaScriptEnabled = true
        }

        restaurantViewModel.getRestaurants()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): Location? {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            val userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            var longitude = userLocation?.longitude
            var latitude = userLocation?.latitude
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10f
            ) { location ->
                longitude = location.longitude
                latitude = location.latitude
            }
            return Location(LocationManager.GPS_PROVIDER).apply {
                this.longitude = longitude!!
                this.latitude = latitude!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}