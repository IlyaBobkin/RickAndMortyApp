package com.example.myapplication.data.model

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Entity(tableName = "characters")
@Serializable
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val created: String,
    val episode: String,
    val gender: String,
    val image: String,
    val locationName: String,
    val locationUrl: String,
    val name: String,
    val originName: String,
    val originUrl: String,
    val species: String,
    val status: String,
    val type: String,
    val url: String,
) {
    fun toCharacter() = Character(
        id = id,
        created = created,
        episode = episode.split(",").filter { it.isNotBlank() },
        gender = gender,
        image = image,
        location = Location(name = locationName, url = locationUrl),
        name = name,
        origin = Origin(name = originName, url = originUrl),
        species = species,
        status = status,
        type = type,
        url = url,
    )
}