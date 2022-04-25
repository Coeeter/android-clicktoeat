package musicpractice.com.coeeter.clicktoeat.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.ActivityMapBinding
import musicpractice.com.coeeter.clicktoeat.databinding.RestaurantInfoWindowBinding
import musicpractice.com.coeeter.clicktoeat.data.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.utils.InjectorUtils
import musicpractice.com.coeeter.clicktoeat.utils.isVisible
import musicpractice.com.coeeter.clicktoeat.data.viewmodels.RestaurantViewModel

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val restaurantId = intent.getIntExtra("restaurant", -1)
        if (restaurantId == -1) finish()

        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener { finish() }

        val factory = InjectorUtils.provideRestaurantViewModelFactory()
        val viewModel = ViewModelProvider(this, factory)[RestaurantViewModel::class.java]

        viewModel.getRestaurantList().observe(this, Observer {
            var restaurant: RestaurantModel? = null
            for (res in it) {
                if (res._id == restaurantId) {
                    restaurant = res
                    break
                }
            }
            if (restaurant == null) return@Observer
            val resultCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this)

            val webMap = binding.mapWebView
            val nativeMap = binding.mapFragment

            val location = getLocation()
            if (resultCode == ConnectionResult.SUCCESS) {
                webMap.isVisible(false)
                nativeMap.isVisible(true)

                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.mapFragment) as SupportMapFragment

                mapFragment.getMapAsync { gMap ->
                    val restaurantLocation = LatLng(restaurant.latitude, restaurant.longitude)
                    val userLocation = LatLng(location?.get("latitude")!!, location["longitude"]!!)

                    gMap.addMarker(
                        MarkerOptions().position(restaurantLocation)
                            .title(restaurant.name)
                            .snippet(restaurant.address)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_icon))
                    )

                    gMap.addMarker(
                        MarkerOptions().position(userLocation)
                            .title("Your Location")
                    )

                    gMap.moveCamera(CameraUpdateFactory.zoomTo(16f))
                    gMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
                    gMap.setOnMapLoadedCallback {
                        gMap.animateCamera(
                            CameraUpdateFactory.newLatLng(restaurantLocation),
                            1000,
                            null
                        )
                    }

                    gMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                        override fun getInfoContents(p0: Marker): View? = null
                        override fun getInfoWindow(p0: Marker): View? {
                            val restaurantInfoWindowBinding = RestaurantInfoWindowBinding
                                .inflate(layoutInflater)
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
                return@Observer
            }
            webMap.isVisible(true)
            nativeMap.isVisible(false)
            val query = restaurant.name.replace(" ", "%20").replace("&", "%zy81")
            webMap.loadUrl(
                "${getString(R.string.base_url)}/restaurants/" +
                        "map?r=$query" +
                        "&lat=${location?.get("latitude")}" +
                        "&lon=${location?.get("longitude")}"
            )
            webMap.settings.javaScriptEnabled = true
        })
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): HashMap<String, Double?>? {
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
            val hashMap = HashMap<String, Double?>()
            hashMap["longitude"] = longitude
            hashMap["latitude"] = latitude
            return hashMap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}