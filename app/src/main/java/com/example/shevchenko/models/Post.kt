package com.example.shevchenko.models

import com.squareup.moshi.Json

data class Post (
    @Json(name = "id") val id: String,
    @Json(name = "description") val description: String,
    @Json(name = "gifURL") val gifURL: String)