package com.example.shevchenko.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostCached (
    @PrimaryKey var id: Int,
    @ColumnInfo var descr: String,
    @ColumnInfo var url: String
) {
    constructor(id: Int, post: Post) : this(id, post.description, post.gifURL)
}