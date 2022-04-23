package musicpractice.com.coeeter.clicktoeat.fragments

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
import musicpractice.com.coeeter.clicktoeat.activities.MapActivity
import musicpractice.com.coeeter.clicktoeat.adapters.TagAdapter
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentRestaurantDetailsBinding
import musicpractice.com.coeeter.clicktoeat.repository.models.RestaurantModel


class FragmentRestaurantDetails(private val restaurant: RestaurantModel) : Fragment(),
    View.OnClickListener {
    private lateinit var binding: FragmentRestaurantDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantDetailsBinding.inflate(inflater, container, false)

        binding.description.text = restaurant.description
        binding.readMore.setOnClickListener(this)
        binding.collapseDescription.setOnClickListener(this)

        binding.openingHours.text =
            restaurant.getOpeningAndClosingHours().joinToString("\n")

        if (restaurant.facebook == null) binding.facebook.visibility = View.GONE
        if (restaurant.website == null) binding.website.visibility = View.GONE
        if (restaurant.twitter == null) binding.twitter.visibility = View.GONE
        if (restaurant.phoneNum == null) binding.phone.visibility = View.GONE
        binding.phone.text = restaurant.phoneNum
        for (link in arrayOf(binding.facebook, binding.website, binding.twitter, binding.phone)) {
            link.setOnClickListener(this)
        }

        val tagsRecyclerView = binding.tags
        tagsRecyclerView.apply {
            adapter = TagAdapter(restaurant.tags)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

        val mapBtn = binding.mapBtn

        mapBtn.setOnClickListener(this)

        binding.location.text = restaurant.address

        return binding.root
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