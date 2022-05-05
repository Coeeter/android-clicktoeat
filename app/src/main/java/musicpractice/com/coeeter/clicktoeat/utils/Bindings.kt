package musicpractice.com.coeeter.clicktoeat.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import musicpractice.com.coeeter.clicktoeat.R

@BindingAdapter("loadURL")
fun loadUrl(view: ImageView, url: String?) {
    if (url == null) return
    Picasso.with(view.context).load("${view.context.getString(R.string.base_url)}/public/url").into(view)
}