package io.github.ameyalaad.rasoee.utils

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.ameyalaad.rasoee.R
import io.github.ameyalaad.rasoee.analyze.RecipeApiStatus
import io.github.ameyalaad.rasoee.analyze.RecipeListAdapter


@BindingAdapter("imageBitmap")
fun bindBitmap(imageView: ImageView, bitmap: Bitmap?) {
    imageView.setImageBitmap(bitmap)
}

@BindingAdapter("imageUrl")
fun bindImageUrl(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("http").build()
        Glide.with(imageView.context).load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image)
            )
            .into(imageView)
    }
}

@BindingAdapter("recipeList")
fun bindList(recyclerView: RecyclerView, data: List<io.github.ameyalaad.rasoee.network.RecipeProperty>?) {
    val adapter = recyclerView.adapter as RecipeListAdapter
    adapter.submitList(data)
}

@BindingAdapter("apiStatus")
fun bindStatus(imageView: ImageView, status: RecipeApiStatus?){
    when(status){
        RecipeApiStatus.ERROR -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.connection_error)
        }
        RecipeApiStatus.LOADING -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.loading_animation)
        }
        else -> {
            imageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("visible")
fun bindVisiblity(view: View, boolean: Boolean?) {
    if(boolean==true){
        view.visibility = View.VISIBLE
    }else{
        view.visibility = View.INVISIBLE
    }
}