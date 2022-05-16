package musicpractice.com.coeeter.clicktoeat.ui.restaurant

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.models.Comment
import musicpractice.com.coeeter.clicktoeat.data.models.Restaurant
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentRestaurantReviewsBinding
import musicpractice.com.coeeter.clicktoeat.ui.adapters.CommentAdapter
import musicpractice.com.coeeter.clicktoeat.utils.createSnackBar
import musicpractice.com.coeeter.clicktoeat.utils.getStringFromSharedPref
import musicpractice.com.coeeter.clicktoeat.utils.hideKeyboard
import musicpractice.com.coeeter.clicktoeat.utils.isVisible

@AndroidEntryPoint
class FragmentRestaurantReviews(
    private val restaurant: Restaurant
) : Fragment(),
    CommentAdapter.CommentDataToChangeListener {
    private lateinit var binding: FragmentRestaurantReviewsBinding
    private lateinit var token: String
    private val restaurantViewModel: RestaurantViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantReviewsBinding.inflate(inflater, container, false)
        initVariables()
        setUpListeners()
        return binding.root
    }

    private fun initVariables() {
        token = requireActivity().getStringFromSharedPref(
            getString(R.string.sharedPrefName),
            getString(R.string.sharedPrefToken)
        )
        binding.comments.apply {
            adapter = CommentAdapter(
                (activity as Context),
                restaurant
            ).apply { dataSetChangeListener = this@FragmentRestaurantReviews }
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setUpListeners() {
        restaurantViewModel.apply {
            commentList.observe(viewLifecycleOwner) {
                val commentList = it.filter { comment -> comment.restaurantId == restaurant._id }
                (binding.comments.adapter as CommentAdapter).setCommentList(commentList as ArrayList<Comment>)
                val averageRating = restaurant.getAvgRatingAndCount(it)
                binding.apply {
                    avgRating.text = averageRating["average"]
                    count.text = averageRating["count"]
                }
                if (commentList.isEmpty()) return@observe binding.run {
                    nothingDisplay.isVisible(true)
                    comments.isVisible(false)
                }
                binding.apply {
                    nothingDisplay.isVisible(false)
                    comments.isVisible(true)
                }
            }
            getComments()
            userList.observe(viewLifecycleOwner) {
                (binding.comments.adapter as CommentAdapter).setUserList(it)
            }
            getUsers()
            error.observe(viewLifecycleOwner) { binding.root.createSnackBar(it) }
        }

        binding.apply {
            commentReview.addTextChangedListener {
                if (it.contentEquals(""))
                    return@addTextChangedListener binding.createCommentBtn.run { isEnabled = false }
                binding.createCommentBtn.isEnabled = true
            }
            val starArray = arrayOf(star1, star2, star3, star4, star5)
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
            createCommentBtn.setOnClickListener {
                requireActivity().hideKeyboard()
                val review = commentReview.text.toString()
                var rating = 0
                for (star in starArray) {
                    if (star.tag == "Checked") rating++
                }

                restaurantViewModel.createComment(
                    token,
                    Comment(
                        restaurantId = restaurant._id,
                        restaurantName = restaurant.name,
                        review = review,
                        rating = rating
                    )
                )

                binding.commentReview.setText("")
                binding.commentReview.clearFocus()
                for (star in starArray) {
                    star.setImageResource(R.drawable.ic_star_outline)
                    star.tag = "Unchecked"
                }
            }
        }
    }

    override fun editComment(comment: Comment) =
        restaurantViewModel.updateComment(token, comment)

    override fun deleteComment(commentId: Int) =
        restaurantViewModel.deleteComment(token, commentId)

}