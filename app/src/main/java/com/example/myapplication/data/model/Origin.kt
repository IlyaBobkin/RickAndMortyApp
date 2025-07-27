package com.example.myapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Origin(
    val name: String,
    val url: String
) : Parcelable