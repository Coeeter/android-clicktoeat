<<<<<<< HEAD:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/restaurant/FragmentRestaurantDetails.kt
package musicpractice.com.coeeter.clicktoeat.ui.restaurant
=======
package musicpractice.com.coeeter.clicktoeat.ui.fragments
>>>>>>> master:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/fragments/FragmentRestaurantDetails.kt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import musicpractice.com.coeeter.clicktoeat.R
<<<<<<< HEAD:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/restaurant/FragmentRestaurantDetails.kt
import musicpractice.com.coeeter.clicktoeat.data.models.Restaurant
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentRestaurantDetailsBinding
=======
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentRestaurantDetailsBinding
import musicpractice.com.coeeter.clicktoeat.data.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.ui.activities.MapActivity
>>>>>>> master:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/fragments/FragmentRestaurantDetails.kt
import musicpractice.com.coeeter.clicktoeat.ui.adapters.TagAdapter
import musicpractice.com.coeeter.clicktoeat.utils.isVisible


class FragmentRestaurantDetails(
    private val restaurant: Restaurant
) : Fragment(),
    View.OnClickListener {
    private lateinit var binding: FragmentRestaurantDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantDetailsBinding.inflate(inflater, container, false)
        initViews()
        setUpListeners()
        return binding.root
    }

<<<<<<< HEAD:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/restaurant/FragmentRestaurantDetails.kt
    private fun initViews() {
        binding.apply {
            description.text = restaurant.description
            openingHours.text = restaurant.getOpeningAndClosingHours().joinToString("\n")
            if (restaurant.facebook == null) facebook.isVisible(false)
            if (restaurant.website == null) website.isVisible(false)
            if (restaurant.twitter == null) twitter.isVisible(false)
            if (restaurant.phoneNum == null) phone.isVisible(false)
            phone.text = restaurant.phoneNum
            binding.tags.apply {
                adapter = TagAdapter(restaurant.tags)
                layoutManager = LinearLayoutManager(
                    activity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                setHasFixedSize(true)
            }
            location.text = restaurant.address
=======
        binding.description.text = restaurant.description
        binding.readMore.setOnClickListener(this)
        binding.collapseDescription.setOnClickListener(this)

        binding.openingHours.text =
            restaurant.getOpeningAndClosingHours().joinToString("\n")

        if (restaurant.facebook == null) binding.facebook.isVisible(false)
        if (restaurant.website == null) binding.website.isVisible(false)
        if (restaurant.twitter == null) binding.twitter.isVisible(false)
        if (restaurant.phoneNum == null) binding.phone.isVisible(false)
        binding.phone.text = restaurant.phoneNum
        for (link in arrayOf(binding.facebook, binding.website, binding.twitter, binding.phone)) {
            link.setOnClickListener(this)
>>>>>>> master:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/fragments/FragmentRestaurantDetails.kt
        }
    }

    private fun setUpListeners() {
        binding.apply {
            readMore.setOnClickListener(this@FragmentRestaurantDetails)
            collapseDescription.setOnClickListener(this@FragmentRestaurantDetails)
            arrayOf(facebook, website, twitter, phone).forEach {
                it.setOnClickListener(this@FragmentRestaurantDetails)
            }
            mapBtn.setOnClickListener(this@FragmentRestaurantDetails)
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v?.id) {
                R.id.facebook -> startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(restaurant.facebook)
                    )
                )
                R.id.website -> startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(restaurant.website)
                    )
                )
                R.id.twitter -> startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(restaurant.twitter)
                    )
                )
                R.id.phone -> startActivity(
                    Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:${restaurant.phoneNum}")
                    )
                )
                R.id.readMore -> {
                    binding.description.maxLines = Int.MAX_VALUE
                    binding.readMore.isVisible(false)
                    binding.collapseDescription.isVisible(true)
                }
                R.id.collapseDescription -> {
                    binding.description.maxLines = 7
                    binding.readMore.isVisible(true)
                    binding.collapseDescription.isVisible(false)
                }
                R.id.mapBtn -> {
                    context?.startActivity(
                        Intent(
                            context,
                            MapActivity::class.java
                        ).putExtra("restaurant", restaurant._id)
                    )
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.stay_still
                    )
                }
            }
        } catch (e: Exception) {
            Log.d("poly", e.toString())
        }
    }

}