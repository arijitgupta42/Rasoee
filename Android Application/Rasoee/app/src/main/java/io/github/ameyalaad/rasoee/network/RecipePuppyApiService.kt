package io.github.ameyalaad.rasoee.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://www.recipepuppy.com/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(io.github.ameyalaad.rasoee.network.moshi))
    .baseUrl(io.github.ameyalaad.rasoee.network.BASE_URL)
    .build()

interface RecipePuppyApiService{
    @GET("api")
    suspend fun getProperties(@Query("q") name: String): io.github.ameyalaad.rasoee.network.ResultObject
}

data class ResultObject(val title:String, val version:Float, val href:String, val results:List<io.github.ameyalaad.rasoee.network.RecipeProperty>)

object RecipePuppyApi{
    val retrofitService: io.github.ameyalaad.rasoee.network.RecipePuppyApiService by lazy { io.github.ameyalaad.rasoee.network.retrofit.create(
        io.github.ameyalaad.rasoee.network.RecipePuppyApiService::class.java) }
}