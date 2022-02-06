package com.example.shevchenko.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shevchenko.models.Post
import com.example.shevchenko.models.PostCached

@Dao
interface CacheService {
    @Insert
    suspend fun insertAll(posts: MutableList<PostCached>)
    @Query("SELECT * FROM postcached")
    suspend fun allCachedPosts(): MutableList<PostCached>
    @Query("SELECT * FROM postcached ORDER BY id DESC LIMIT 1")
    suspend fun getLastPost(): PostCached
    suspend fun countPosts(): Int {
        return allCachedPosts().size
    }
}