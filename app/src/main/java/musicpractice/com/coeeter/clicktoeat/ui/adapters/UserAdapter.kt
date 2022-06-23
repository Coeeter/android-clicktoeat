package musicpractice.com.coeeter.clicktoeat.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.data.models.User
import musicpractice.com.coeeter.clicktoeat.databinding.RecyclerUserListBinding

class UserAdapter(
    private val context: Context,
    private val userList: List<User>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerUserListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        return ViewHolder(
            RecyclerUserListBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = userList[position]
        Picasso.with(context)
            .load(context.getString(R.string.base_url) + "upload/" + user.imagePath)
            .into(holder.binding.profilePicture)
        holder.binding.username.text = user.username
    }

    override fun getItemCount() = userList.size

}