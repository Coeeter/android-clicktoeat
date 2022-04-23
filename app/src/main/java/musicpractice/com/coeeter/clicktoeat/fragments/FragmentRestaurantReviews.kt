package musicpractice.com.coeeter.clicktoeat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.adapters.CommentAdapter
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentRestaurantReviewsBinding
import musicpractice.com.coeeter.clicktoeat.repository.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.repository.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.CommentViewModel
import musicpractice.com.coeeter.clicktoeat.repository.viewmodels.UserViewModel

class FragmentRestaurantReviews(private val restaurant: RestaurantModel) : Fragment() {
    private lateinit var binding: FragmentRestaurantReviewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantReviewsBinding.inflate(inflater, container, false)

        val commentAdapter = CommentAdapter(requireContext(), restaurant)

        CommentViewModel.getAllComments().observe(viewLifecycleOwner, Observer {
            val avgRating = restaurant.getAvgRatingAndCount(it)
            binding.avgRating.text = avgRating["average"]
            binding.count.text = avgRating["count"]
            val commentList = ArrayList<CommentModel>()
            for (comment in it) {
                if (comment.restaurantId == restaurant._id) {
                    commentList.add(comment)
                }
            }
            binding.nothingDisplay.visibility = View.GONE
            binding.comments.visibility = View.VISIBLE
            if (commentList.size == 0) {
                binding.nothingDisplay.visibility = View.VISIBLE
                binding.comments.visibility = View.GONE
                return@Observer
            }
            commentAdapter.setCommentList(commentList)
        })

        UserViewModel.getUserList().observe(viewLifecycleOwner, Observer {
            commentAdapter.setUserList(it)
        })

        binding.comments.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(context)
        }

        return binding.root
    }
}