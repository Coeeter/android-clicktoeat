package musicpractice.com.coeeter.clicktoeat.ui.adapters

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.CommentLayoutBinding
import musicpractice.com.coeeter.clicktoeat.databinding.UpdateCommentDialogBinding
import musicpractice.com.coeeter.clicktoeat.models.CommentModel
import musicpractice.com.coeeter.clicktoeat.models.LikesAndDislikesModel
import musicpractice.com.coeeter.clicktoeat.models.RestaurantModel
import musicpractice.com.coeeter.clicktoeat.models.UserModel
import musicpractice.com.coeeter.clicktoeat.utils.InjectorUtils
import musicpractice.com.coeeter.clicktoeat.utils.isVisible
import musicpractice.com.coeeter.clicktoeat.viewmodels.CommentViewModel

class CommentAdapter(
    private val context: Context,
    private val restaurant: RestaurantModel
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private var commentList = ArrayList<CommentModel>()
    private var userList = ArrayList<UserModel>()
    private var likesAndDislikesList = ArrayList<LikesAndDislikesModel>()
    private lateinit var token: String

    fun setCommentList(commentList: ArrayList<CommentModel>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }

    fun setUserList(userList: ArrayList<UserModel>) {
        this.userList = userList
        notifyDataSetChanged()
    }

    fun setLikesAndDislikesList(likesAndDislikesList: ArrayList<LikesAndDislikesModel>) {
        this.likesAndDislikesList = likesAndDislikesList
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: CommentLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val binding =
            CommentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        val comment = commentList[position]
        token = context.getSharedPreferences("memory", Context.MODE_PRIVATE)
            .getString("token", "")
            .toString()
        var commentedUser: UserModel? = null
        for (user in userList) {
            if (user.username == comment.username) {
                commentedUser = user
                break
            }
        }
        if (commentedUser == null) return

        if (commentedUser.imagePath != null) {
            val imagePath = context.getString(R.string.base_url) +
                    "/upload/${commentedUser.imagePath}"
            Picasso.with(context).load(imagePath).into(holder.binding.profileImage)
        }

        holder.binding.username.text = commentedUser.username
        holder.binding.datePosted.text = comment.datePosted
        holder.binding.review.text = comment.review

        val starArray = arrayOf(
            holder.binding.star1,
            holder.binding.star2,
            holder.binding.star3,
            holder.binding.star4,
            holder.binding.star5
        )

        for (i in 0 until comment.rating)
            starArray[i].setImageResource(R.drawable.ic_star)

        if ((5 - comment.rating) != 0) {
            for (i in comment.rating until 5) {
                starArray[i].setImageResource(R.drawable.ic_star_outline)
            }
        }

        val loggedInUserProfile = context.getSharedPreferences(
            "memory",
            Context.MODE_PRIVATE
        ).getString("profile", "")

        val loggedInUserAccount = Gson().fromJson(loggedInUserProfile, UserModel::class.java)

        holder.binding.edit.isVisible(false)
        if (loggedInUserAccount.username == commentedUser.username) {
            val viewModel = ViewModelProvider(
                (context as AppCompatActivity),
                InjectorUtils.provideCommentViewModelFactory()
            )[CommentViewModel::class.java]
            holder.binding.edit.isVisible(true)
            holder.binding.edit.setOnClickListener {
                val popup = PopupMenu(context, it)
                popup.menuInflater.inflate(R.menu.dropdown_comment, popup.menu)
                popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                    when (menuItem.itemId) {
                        R.id.miEdit -> editComment(viewModel, comment)
                        R.id.miDelete -> deleteComment(viewModel, token, comment.id)
                    }
                    true
                }
                popup.show()
            }
        }
    }

    private fun deleteComment(viewModel: CommentViewModel, token: String, commentId: Int) {
        viewModel.getDeleteCommentResult().observe((context as AppCompatActivity), Observer {
            if (it.affectedRows != 1) return@Observer
            viewModel.getAllComments()
        })
        viewModel.removeComment(token, commentId)
    }

    private fun editComment(viewModel: CommentViewModel, comment: CommentModel) {
        val dialog = Dialog(context)
        val width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()

        val binding = UpdateCommentDialogBinding
            .inflate(LayoutInflater.from(context))

        dialog.setCancelable(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT)

        val starArray = arrayOf(
            binding.star1,
            binding.star2,
            binding.star3,
            binding.star4,
            binding.star5
        )
        for (i in 0 until comment.rating) {
            starArray[i].setImageResource(R.drawable.ic_star)
            starArray[i].tag = "Checked"
        }

        if ((5 - comment.rating) != 0) {
            for (i in comment.rating until 5) {
                starArray[i].setImageResource(R.drawable.ic_star_outline)
                starArray[i].tag = "Unchecked"
            }
        }

        for (star in starArray) {
            star.setOnClickListener {
                for (toBeCheckedStar in starArray) {
                    toBeCheckedStar.setImageResource(R.drawable.ic_star_outline)
                    toBeCheckedStar.tag = "Unchecked"
                }

                val index = getStarIndex(star)
                for (i in 0 until index + 1) {
                    starArray[i].setImageResource(R.drawable.ic_star)
                    starArray[i].tag = "Checked"
                }

                val rating = index + 1
                if (rating == comment.rating) {
                    binding.submitBtn.isEnabled = false
                    return@setOnClickListener
                }
                binding.submitBtn.isEnabled = true
            }
        }

        binding.submitBtn.isEnabled = false

        binding.editComment.setText(comment.review)
        binding.editComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == comment.review) {
                    binding.submitBtn.isEnabled = false
                    return
                }
                binding.submitBtn.isEnabled = true
            }
        })

        binding.cancel.setOnClickListener { dialog.dismiss() }

        binding.submitBtn.setOnClickListener {
            val updatedReview = binding.editComment.text.toString()
            var rating = 0
            for (star in starArray) {
                if (star.tag == "Checked") {
                    rating += 1
                }
            }
            viewModel.getEditCommentResult().observe((context as AppCompatActivity), Observer {
                if (it.affectedRows != 1) return@Observer
                viewModel.getAllComments()
            })
            viewModel.editComment(
                token,
                comment.id,
                restaurant,
                updatedReview,
                rating
            )
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun getItemCount(): Int = commentList.size

    private fun getStarIndex(star: ImageView): Int = when (star.id) {
        R.id.star1 -> 0
        R.id.star2 -> 1
        R.id.star3 -> 2
        R.id.star4 -> 3
        R.id.star5 -> 4
        else -> -1
    }

}