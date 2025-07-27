package com.example.myapplication.ui.detail

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import com.example.myapplication.data.model.Character
import com.example.myapplication.ui.main.MainViewModel
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailScreen(characterId: Int, viewModel: MainViewModel, navController: NavController) {
    val characterFlow: Flow<Character?> = viewModel.getCharacterById(characterId)
    val character = characterFlow.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character.value?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        character.let { char ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = char.value?.image,
                        imageLoader = ImageLoader.Builder(LocalContext.current)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build()
                    ),
                    contentDescription = char.value?.name,
                    modifier = Modifier.size(350.dp).shadow(20.dp).clip(RoundedCornerShape(10.dp)).border(2.dp,Color.LightGray, shape = RoundedCornerShape(10.dp)))
                Spacer(modifier = Modifier.height(16.dp))
                Card(elevation = CardDefaults.cardElevation(20.dp), modifier = Modifier.border(2.dp,Color.LightGray, shape = RoundedCornerShape(10.dp))) {Column(modifier = Modifier.padding(20.dp).width(310.dp)) {
                    Text(text = "Имя: ${char.value?.name}", style = MaterialTheme.typography.titleLarge)
                    Text(text = "Вид: ${char.value?.species}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Статус: ${char.value?.status}", style = MaterialTheme.typography.bodyLarge,color = if (char.value?.status == "Alive") Color.Green else if (char.value?.status == "Dead") Color.Red else Color.Yellow
                    )
                    Text(text = "Пол: ${char.value?.gender}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Местоположение: ${char.value?.location?.name}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Происхождение: ${char.value?.origin?.name}", style = MaterialTheme.typography.bodyLarge)
                }  }

            }
        }
    }
}