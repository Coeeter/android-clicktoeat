package musicpractice.com.coeeter.clicktoeat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import musicpractice.com.coeeter.clicktoeat.R

class TagAdapter(private val tags: List<String>) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tag: TextView = view.findViewById<TextView>(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.tag_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tag.text = tags[position]
    }

    override fun getItemCount(): Int {
        return tags.size
    }
}