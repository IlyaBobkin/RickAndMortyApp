package com.example.myapplication.data

import com.example.myapplication.data.model.ApiResponse
import com.example.myapplication.data.network.ApiService
import javax.inject.Inject

class Repository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getPage(page: Int, name: String? = null, status: String? = null, gender: String? = null): ApiResponse {
        return api.getPage(page, name, status, gender)
    }
}