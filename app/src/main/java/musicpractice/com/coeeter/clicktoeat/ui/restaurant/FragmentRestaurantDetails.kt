package musicpractice.com.coeeter.clicktoeat.ui.restaurant

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
import musicpractice.com.coeeter.clicktoeat.data.models.Restaurant
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentRestaurantDetailsBinding
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
                    binding.readMore.visibility = View.GONE
                    binding.collapseDescription.visibility = View.VISIBLE
                }
                R.id.collapseDescription -> {
                    binding.description.maxLines = 7
                    binding.readMore.visibility = View.VISIBLE
                    binding.collapseDescription.visibility = View.GONE
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