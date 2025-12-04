package com.example.cardpic.data.dao

import androidx.room.*
import com.example.cardpic.data.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Insert
    suspend fun insert(topic: TopicEntity): Long

    @Query("SELECT * FROM topics")
    fun getAllTopicsFlow(): Flow<List<TopicEntity>>

    @Query("DELETE FROM topics WHERE id = :id")
    suspend fun deleteById(id: Int)
}