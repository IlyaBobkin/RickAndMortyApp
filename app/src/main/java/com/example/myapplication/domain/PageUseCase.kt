package com.example.myapplication.domain

import com.example.myapplication.data.Repository
import com.example.myapplication.data.model.ApiResponse
import javax.inject.Inject

class PageUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(page: Int): ApiResponse =
        repository.getPage(page)
}