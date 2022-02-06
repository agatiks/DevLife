package com.example.shevchenko

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shevchenko.models.PostCached
import com.example.shevchenko.services.CacheService

@Database(entities = [PostCached::class], version = 1, exportSchema = false)
abstract class PostDatabase : RoomDatabase() {
    abstract fun dao(): CacheService
}