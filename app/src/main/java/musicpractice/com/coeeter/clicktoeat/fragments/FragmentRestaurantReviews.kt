package musicpractice.com.coeeter.clicktoeat.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.activities.LoginActivity
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

        val token = requireActivity().getSharedPreferences("memory", Context.MODE_PRIVATE)
            .getString("token", "")!!

        binding.commentReview.addTextChangedListener {
            if (it.contentEquals("")) {
                binding.createCommentBtn.isEnabled = false
                return@addTextChangedListener
            }
            binding.createCommentBtn.isEnabled = true
        }

        val starArray =
            arrayOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
        for (star in starArray) {
            star.tag = "Unchecked"
            star.setOnClickListener {
                for (i in starArray.indices) {
                    starArray[i].setImageResource(R.drawable.ic_star_outline)
                    starArray[i].tag = "Unchecked"
                }
                val index = when (it.id) {
                    R.id.star1 -> 0
                    R.id.star2 -> 1
                    R.id.star3 -> 2
                    R.id.star4 -> 3
                    R.id.star5 -> 4
                    else -> -1
                }
                if (index == -1) return@setOnClickListener
                for (i in 0 until index + 1) {
                    starArray[i].setImageResource(R.drawable.ic_star)
                    starArray[i].tag = "Checked"
                }
            }
        }

        binding.createCommentBtn.setOnClickListener {
            LoginActivity.hideKeyboard(requireActivity())
            val review = binding.commentReview.text.toString()
            var rating = 0
            for (star in starArray) {
                if (star.tag == "Checked") rating++
            }

            CommentViewModel.createComment(
                token,
                restaurant,
                review,
                rating
            )

            binding.commentReview.setText("")
            binding.commentReview.clearFocus()
            for (star in starArray) {
                star.setImageResource(R.drawable.ic_star_outline)
                star.tag = "Unchecked"
            }
        }

        return binding.root
    }
}