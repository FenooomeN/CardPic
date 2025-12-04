package com.example.cardpic.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.cardpic.data.dao.CardDao
import com.example.cardpic.data.dao.TopicDao
import com.example.cardpic.data.entity.CardEntity
import com.example.cardpic.data.entity.TopicEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class RoomRepository(
    internal val topicDao: TopicDao,
    internal val cardDao: CardDao,
    private val context: Context
) {

    private val imagesDir = File(context.filesDir, "images").apply { mkdirs() }

    // ----------------------------------------------------------
    // TOPICS
    // ----------------------------------------------------------
    fun getAllTopicsFlow() = topicDao.getAllTopicsFlow()

    fun getCardsCountFlow(topicId: Int) = cardDao.getCardsCount(topicId)

    suspend fun addTopic(title: String): TopicEntity {
        val topic = TopicEntity(title = title)
        val id = topicDao.insert(topic).toInt()
        return topic.copy(id = id)
    }

    suspend fun deleteTopic(topicId: Int) {
        topicDao.deleteById(topicId)
        cardDao.deleteCardsForTopic(topicId)
    }

    // ----------------------------------------------------------
    // CARDS
    // ----------------------------------------------------------
    suspend fun getCardsForTopic(topicId: Int): List<CardEntity> =
        cardDao.getCardsForTopic(topicId)

    suspend fun getAllCards(): List<CardEntity> =
        cardDao.getAllCards()

    suspend fun addCard(card: CardEntity) {
        cardDao.insert(card)
    }

    // ----------------------------------------------------------
    // IMAGE STORAGE
    // ----------------------------------------------------------
    suspend fun saveBitmapToFile(
        bitmap: Bitmap,
        fileNameHint: String = "img_${System.currentTimeMillis()}"
    ): String = withContext(Dispatchers.IO) {

        val file = File(imagesDir, "$fileNameHint.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        }
        file.absolutePath
    }

    suspend fun downloadImageToLocalPath(imageUrl: String): String? =
        withContext(Dispatchers.IO) {

            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(imageUrl).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) return@withContext null

                val bytes = response.body?.bytes() ?: return@withContext null
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ?: return@withContext null

                val fileName = "img_${System.currentTimeMillis()}"
                saveBitmapToFile(bitmap, fileName)

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}