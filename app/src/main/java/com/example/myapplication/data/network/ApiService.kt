package com.example.myapplication.data.network

import com.example.myapplication.data.model.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApiService @Inject constructor(private val api: ApiClient) {
    suspend fun getPage(page: Int, name: String? = null, status: String? = null, gender: String? = null): ApiResponse {
        return withContext(Dispatchers.IO) {
            api.getPage(page, name, status, gender)
        }
    }
}