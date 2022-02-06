package com.example.shevchenko

import android.app.Application
import androidx.room.Room
import com.example.shevchenko.services.CacheService
import com.example.shevchenko.services.PostService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class GlobalApp: Application() {
    lateinit var service: PostService
        private set
    lateinit var databaseService: CacheService
        private  set

    override fun onCreate() {
        super.onCreate()
        instance = this
        val retrofit = Retrofit.Builder()
            .baseUrl("https://developerslife.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(PostService::class.java)
        databaseService = Room.databaseBuilder(this,
            PostDatabase::class.java, "database").build().dao()
    }
    companion object{
        lateinit var instance: GlobalApp
            private set
    }
}