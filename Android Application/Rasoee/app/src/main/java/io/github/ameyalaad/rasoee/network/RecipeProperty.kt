package io.github.ameyalaad.rasoee.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RecipeProperty(
    val title: String,
    @Json(name = "href") val recipe_link: String,
    val ingredients: String,
    val thumbnail: String
) :Parcelable