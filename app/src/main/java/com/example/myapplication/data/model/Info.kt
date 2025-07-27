package com.example.myapplication.data.model

import java.io.Serializable

data class Info(
    val count: Int,
    val next: String?,
    val prev: String?,
    val pages: Int,
) : Serializable