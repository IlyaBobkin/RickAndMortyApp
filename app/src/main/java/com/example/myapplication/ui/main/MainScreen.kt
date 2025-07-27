package com.example.myapplication.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import com.example.myapplication.data.model.Character
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(viewModel: MainViewModel, onNavigateToDetail: (Character) -> Unit) {
    val characters: LazyPagingItems<Character> = viewModel.characters.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val filterGender by viewModel.filterGender.collectAsState()
    val filterSpecies by viewModel.filterSpecies.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showFilterDialog by remember { mutableStateOf(false) }
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val totalPages by viewModel.totalPages.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Персонажи") },
            )
        }
    ) { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                coroutineScope.launch {
                    viewModel.refreshCharacters()
                    characters.refresh()
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        viewModel.setSearchQuery(query)
                        characters.refresh()
                    },
                    label = { Text("Поиск по имени") },
                    trailingIcon = {IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Фильтр")
                    }},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )

                // Кнопки пагинации
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.previousPage() },
                        enabled = currentPage > 1 && searchQuery.isEmpty()
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Назад")
                    }
                    Button(
                        onClick = { viewModel.nextPage() },
                        enabled = currentPage < totalPages && searchQuery.isEmpty()
                    ) {
                        Text("Вперед")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Вперед")
                    }
                }

                // Обработка состояния загрузки
                when (characters.loadState.refresh) {
                    is LoadState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                    }
                    is LoadState.Error -> {
                        Text(
                            text = "Ошибка загрузки",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                    }
                    else -> {
                        CharacterGrid(characters = characters, onClickListener = onNavigateToDetail)
                    }
                }
            }
        }
    }

    // Диалог фильтров
    if (showFilterDialog) {
        FilterDialog(
            currentStatus = filterStatus,
            currentGender = filterGender,
            currentSpecies = filterSpecies,
            currentType = filterType,
            onStatusChange = { status ->
                viewModel.setFilterStatus(status)
                characters.refresh()
            },
            onGenderChange = { gender ->
                viewModel.setFilterGender(gender)
                characters.refresh()
            },
            onSpeciesChange = { species ->
                viewModel.setFilterSpecies(species)
                characters.refresh()
            },
            onTypeChange = { type ->
                viewModel.setFilterType(type)
                characters.refresh()
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
fun CharacterGrid(
    characters: LazyPagingItems<Character>,
    onClickListener: (Character) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(characters.itemCount) { index ->
            characters[index]?.let { character ->
                CharacterItem(character = character, onClickListener = onClickListener)
            }
        }
    }
}

@Composable
fun CharacterItem(character: Character, onClickListener: (Character) -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clickable { onClickListener(character) },
        shape = RoundedCornerShape(16.dp)
    ) {
        Surface(color = Color.LightGray, modifier = Modifier) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = character.image,
                        imageLoader = ImageLoader.Builder(context)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build()

                    ),
                    contentDescription = character.name,
                    modifier = Modifier.size(200.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.DarkGray)
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                )
                {
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.LightGray
                    )
                    Text(
                        text = "Вид: ${character.species}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                    Text(
                        text = "Статус: ${character.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (character.status == "Alive") Color.Green else if (character.status == "Dead") Color.Red else Color.Yellow
                    )
                    Text(
                        text = "Пол: ${character.gender}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDialog(
    currentStatus: String,
    currentGender: String,
    currentSpecies: String,
    currentType: String,
    onStatusChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSpeciesChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Фильтры") },
        text = {
            Column() {
                // Статус
                Text("Статус")
                Row(modifier = Modifier.wrapContentWidth()) {
                    FilterChip(
                        selected = currentStatus == "",
                        onClick = { onStatusChange("") },
                        label = { Text("Все") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = currentStatus == "Alive",
                        onClick = { onStatusChange("Alive") },
                        label = { Text("Жив") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = currentStatus == "Dead",
                        onClick = { onStatusChange("Dead") },
                        label = { Text("Мертв") }
                    )
                }
                FilterChip(
                    selected = currentStatus == "unknown",
                    onClick = { onStatusChange("unknown") },
                    label = { Text("Неизвестно") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Пол
                Text("Пол")
                Row {
                    FilterChip(
                        selected = currentGender == "",
                        onClick = { onGenderChange("") },
                        label = { Text("Все") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = currentGender == "Male",
                        onClick = { onGenderChange("Male") },
                        label = { Text("Мужской") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = currentGender == "Female",
                        onClick = { onGenderChange("Female") },
                        label = { Text("Женский") }
                    )
                }
                Row {
                    FilterChip(
                        selected = currentGender == "Genderless",
                        onClick = { onGenderChange("Genderless") },
                        label = { Text("Бесполый") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = currentGender == "unknown",
                        onClick = { onGenderChange("unknown") },
                        label = { Text("Неизвестно") }
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Вид
                Text("Вид")
                OutlinedTextField(
                    value = currentSpecies,
                    onValueChange = onSpeciesChange,
                    label = { Text("Введите вид") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Тип
                Text("Тип")
                OutlinedTextField(
                    value = currentType,
                    onValueChange = onTypeChange,
                    label = { Text("Введите тип") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("ОК")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

