package com.example.myapplication.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Serializable
@Parcelize
data class Location(
    val name: String,
    val url: String
) : Parcelable