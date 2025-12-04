package com.example.cardpic.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("topicId")]
)
/**
 * Модель карточки слова.
 * frontText      - слово на иностранном языке (видно на фронте)
 * translation - перевод (на обратной стороне)
 * description - доп. информация (на обратной стороне)
 * imagePath - путь к скачанному изображению в internal storage (опционально)
 */
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topicId: Int,
    val frontText: String,
    val translation: String,
    val description: String,
    val imagePath: String? = null
)