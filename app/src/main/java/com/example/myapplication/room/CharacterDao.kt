package com.example.myapplication.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.model.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>): List<Long>

    @Query("SELECT * FROM characters WHERE " +
            "name LIKE '%' || :name || '%' " +
            "AND (:status = '' OR status = :status) " +
            "AND (:gender = '' OR gender = :gender) " +
            "AND (:species = '' OR species = :species) " +
            "AND (:type = '' OR type = :type) " +
            "LIMIT :pageSize OFFSET :offset")
    fun getCharacters(
        name: String,
        status: String,
        gender: String,
        species: String,
        type: String,
        pageSize: Int,
        offset: Int
    ): PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM characters WHERE " +
            "name LIKE '%' || :name || '%' " +
            "AND (:status = '' OR status = :status) " +
            "AND (:gender = '' OR gender = :gender) " +
            "AND (:species = '' OR species = :species) " +
            "AND (:type = '' OR type = :type)")
    fun searchCharacters(
        name: String,
        status: String,
        gender: String,
        species: String,
        type: String
    ): PagingSource<Int, CharacterEntity>

    @Query("DELETE FROM characters")
    suspend fun clearAll(): Int

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterById(id: Int): Flow<CharacterEntity?>

    @Query("SELECT COUNT(*) FROM characters WHERE " +
            "name LIKE '%' || :name || '%' " +
            "AND (:status = '' OR status = :status) " +
            "AND (:gender = '' OR gender = :gender) " +
            "AND (:species = '' OR species = :species) " +
            "AND (:type = '' OR type = :type)")
    suspend fun getCharacterCount(
        name: String,
        status: String,
        gender: String,
        species: String,
        type: String
    ): Int
}