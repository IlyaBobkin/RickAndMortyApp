package com.example.myapplication.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.myapplication.data.model.Character
import com.example.myapplication.data.model.CharacterEntity
import com.example.myapplication.data.network.ApiService
import com.example.myapplication.room.CharacterDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val characterDao: CharacterDao,
    private val apiService: ApiService
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterStatus = MutableStateFlow("")
    val filterStatus = _filterStatus.asStateFlow()

    private val _filterGender = MutableStateFlow("")
    val filterGender = _filterGender.asStateFlow()

    private val _filterSpecies = MutableStateFlow("")
    val filterSpecies = _filterSpecies.asStateFlow()

    private val _filterType = MutableStateFlow("")
    val filterType = _filterType.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(42)
    val totalPages = _totalPages.asStateFlow()

    companion object {
        const val PAGE_SIZE = 20
    }

    init {
        viewModelScope.launch {
            val count = characterDao.getCharacterCount(
                name = "",
                status = "",
                gender = "",
                species = "",
                type = ""
            )
            if (count == 0) {
                loadAllPages()
            } else {
                updateTotalPages()
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _currentPage.value = 1
        updateTotalPages()
    }

    fun setFilterStatus(status: String) {
        _filterStatus.value = status
        _currentPage.value = 1
        updateTotalPages()
    }

    fun setFilterGender(gender: String) {
        _filterGender.value = gender
        _currentPage.value = 1
        updateTotalPages()
    }

    fun setFilterSpecies(species: String) {
        _filterSpecies.value = species
        _currentPage.value = 1
        updateTotalPages()
    }

    fun setFilterType(type: String) {
        _filterType.value = type
        _currentPage.value = 1
        updateTotalPages()
    }

    fun nextPage() {
        _currentPage.value += 1
        loadPage(_currentPage.value)
        updateTotalPages()
    }

    fun previousPage() {
        if (_currentPage.value > 1) {
            _currentPage.value -= 1
            loadPage(_currentPage.value)
            updateTotalPages()
        }
    }

    val characters: Flow<PagingData<Character>> = Pager(
        PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)
    ) {
        characterDao.getCharacters(
            name = searchQuery.value,
            status = filterStatus.value,
            gender = filterGender.value,
            species = filterSpecies.value,
            type = filterType.value,
            pageSize = PAGE_SIZE,
            offset = (_currentPage.value - 1) * PAGE_SIZE
        )
    }.flow.map { pagingData ->
        pagingData.map { it.toCharacter() }
    }.cachedIn(viewModelScope)

    fun refreshCharacters() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val response = apiService.getPage(1)
                val charactersWithImages = response.results.map { character ->
                    character.toEntity()
                }
                characterDao.clearAll()
                characterDao.insertAll(charactersWithImages)
                _currentPage.value = 1
            } catch (e: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getPage(page)
                val charactersWithImages = response.results.map { character ->
                    character.toEntity()
                }
                characterDao.insertAll(charactersWithImages)
            } catch (e: Exception) {
            }
        }
    }

    private fun loadAllPages() {
        viewModelScope.launch {
            try {
                var page = 1
                var hasNextPage = true
                characterDao.clearAll()
                while (hasNextPage) {
                    val response = apiService.getPage(page)
                    val charactersWithImages = response.results.map { character ->
                        character.toEntity()
                    }
                    characterDao.insertAll(charactersWithImages)
                    hasNextPage = response.info.next != null
                    page++
                    println("Loaded page $page with ${charactersWithImages.size} characters")
                }
            } catch (e: Exception) {
                println("Error loading pages: ${e.message}")
            }
        }
    }
    private fun updateTotalPages() {
        viewModelScope.launch {
            val count = characterDao.getCharacterCount(
                name = searchQuery.value,
                status = filterStatus.value,
                gender = filterGender.value,
                species = filterSpecies.value,
                type = filterType.value
            )
            _totalPages.value = (count + PAGE_SIZE - 1) / PAGE_SIZE
            println("Total pages: ${_totalPages.value}, count: $count")
        }
    }

    fun getCharacterById(id: Int): Flow<Character?> {
        return characterDao.getCharacterById(id).map { it?.toCharacter() }
    }

    private fun Character.toEntity() = CharacterEntity(
        id = id,
        created = created,
        episode = episode.joinToString(","),
        gender = gender,
        image = image,
        locationName = location.name,
        locationUrl = location.url,
        name = name,
        originName = origin.name,
        originUrl = origin.url,
        species = species,
        status = status,
        type = type,
        url = url,
    )
}