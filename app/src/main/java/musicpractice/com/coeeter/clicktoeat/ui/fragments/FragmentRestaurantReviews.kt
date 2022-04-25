package musicpractice.com.coeeter.clicktoeat.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentRestaurantReviewsBinding
import musicpractice.com.coeeter.clicktoeat.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.ui.adapters.CommentAdapter
import musicpractice.com.coeeter.clicktoeat.utils.InjectorUtils
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import musicpractice.com.coeeter.clicktoeat.utils.isVisible
import musicpractice.com.coeeter.clicktoeat.viewmodels.CommentViewModel
import musicpractice.com.coeeter.clicktoeat.viewmodels.UserViewModel

class FragmentRestaurantReviews(private val restaurant: RestaurantModel) : Fragment() {
    private lateinit var binding: FragmentRestaurantReviewsBinding
    private lateinit var commentViewModel: CommentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantReviewsBinding.inflate(inflater, container, false)

        val commentAdapter = CommentAdapter(requireContext(), restaurant)

        commentViewModel = ViewModelProvider(
            this,
            InjectorUtils.provideCommentViewModelFactory()
        )[CommentViewModel::class.java]
        val userViewModel = ViewModelProvider(
            this,
            InjectorUtils.provideUserViewModelFactory()
        )[UserViewModel::class.java]

        commentViewModel.getCommentList().observe(viewLifecycleOwner, Observer {
            val avgRating = restaurant.getAvgRatingAndCount(it)
            binding.avgRating.text = avgRating["average"]
            binding.count.text = avgRating["count"]
            val commentList = ArrayList<CommentModel>()
            for (comment in it) {
                if (comment.restaurantId == restaurant._id) {
                    commentList.add(comment)
                }
            }
            binding.nothingDisplay.isVisible(false)
            binding.comments.isVisible(true)
            if (commentList.size == 0) {
                binding.nothingDisplay.isVisible(true)
                binding.comments.isVisible(false)
                return@Observer
            }
            commentAdapter.setCommentList(commentList)
        })
        commentViewModel.getAllComments()

        userViewModel.getUserList().observe(viewLifecycleOwner, Observer {
            commentAdapter.setUserList(it)
        })
        userViewModel.getAllUsers()

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
                requireActivity().hideKeyboard()
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
            requireActivity().hideKeyboard()
            val review = binding.commentReview.text.toString()
            var rating = 0
            for (star in starArray) {
                if (star.tag == "Checked") rating++
            }

            commentViewModel.getAddCommentResult().observe(viewLifecycleOwner, Observer {
                if (it.affectedRows != 1 || it.insertId == null) return@Observer
                commentViewModel.getAllComments()
            })
            commentViewModel.addComment(token, restaurant, review, rating)

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