package musicpractice.com.coeeter.clicktoeat.ui.restaurant

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityMapBinding
import musicpractice.com.coeeter.clicktoeat.databinding.MapRestaurantInfoWindowBinding
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
            getLocation().addOnSuccessListener { location: Location? ->
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
                            map.addMarker(
                                MarkerOptions().position(userLocation).title("Your location")
                            )
                            map.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
                            map.setOnMapLoadedCallback {
                                map.animateCamera(
                                    CameraUpdateFactory.newLatLng(restaurantLocation),
                                    1000,
                                    null
                                )
                            }
                        } else {
                            map.moveCamera(CameraUpdateFactory.newLatLng(restaurantLocation))
                        }
                        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                            override fun getInfoContents(p0: Marker): View? = null

                            override fun getInfoWindow(p0: Marker): View {
                                val restaurantInfoWindowBinding =
                                    MapRestaurantInfoWindowBinding.inflate(layoutInflater)
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
                    return@addOnSuccessListener
                }
                binding.mapWebView.isVisible(true)
                binding.mapFragment.isVisible(false)
                val query = restaurant.name.replace(" ", "%20").replace("&", "%zy81")
                if (location == null) return@addOnSuccessListener
                binding.mapWebView.loadUrl(
                    "${getString(R.string.base_url)}/restaurants/" +
                            "map?r=$query" +
                            "&lat=${location.latitude}" +
                            "&lon=${location.longitude}"
                )
                binding.mapWebView.settings.javaScriptEnabled = true
            }
        }
        restaurantViewModel.getRestaurants()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): Task<Location> {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        return fusedLocationClient.lastLocation
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}