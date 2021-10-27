package com.areeb.okhttpissueslist.network

import com.areeb.okhttpissueslist.models.CommentsListResponse
import com.areeb.okhttpissueslist.models.IssueListResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

//initialize base url
private const val BASE_URL = "https://api.github.com/repos/square/okhttp/"

//initialize Moshi object
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

//initialize retrofit
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    //fetch OkHTTP issue list list
    @GET("issues")
    suspend fun fetchIssuesList(): Response<List<IssueListResponse>>

    //fetch comments
    @GET("issues/{issueNumber}/comments")
    suspend fun fetchCommentsList(
        @Path(value = "issueNumber", encoded = false) issueNumber: String
    ): Response<List<CommentsListResponse>>
}

//declare api object
object PesaCashApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}