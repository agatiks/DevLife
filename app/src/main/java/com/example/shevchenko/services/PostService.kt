package com.example.shevchenko.services

import com.example.shevchenko.models.Post
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Response

interface PostService {
    @GET("random?json=true")
    suspend fun getPost(): Response<Post>
}