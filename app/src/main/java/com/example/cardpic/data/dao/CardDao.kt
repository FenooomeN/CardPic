package com.example.cardpic.data.dao

import androidx.room.*
import com.example.cardpic.data.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM cards WHERE topicId = :topicId")
    suspend fun getCardsForTopic(topicId: Int): List<CardEntity>

    @Query("SELECT * FROM cards")
    suspend fun getAllCards(): List<CardEntity>

    @Insert
    suspend fun insert(card: CardEntity): Long

    @Query("DELETE FROM cards WHERE topicId = :topicId")
    suspend fun deleteCardsForTopic(topicId: Int)

    @Delete
    suspend fun delete(card: CardEntity)

    @Query("SELECT COUNT(*) FROM cards WHERE topicId = :topicId")
    fun getCardsCount(topicId: Int): Flow<Int>

    @Query("SELECT * FROM cards WHERE topicId = :topicId")
    fun getCardsForTopicFlow(topicId: Int): Flow<List<CardEntity>>
}
