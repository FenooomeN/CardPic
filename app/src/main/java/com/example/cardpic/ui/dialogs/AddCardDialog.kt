package com.example.cardpic.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cardpic.data.RoomRepository
import com.example.cardpic.data.entity.CardEntity
import kotlinx.coroutines.launch

/**
 * Диалог для добавления карточки.
 * Поле imageUrl опционально — при наличии ссылки изображение скачивается и привязывается к карточке.
 */
@Composable
fun AddCardDialog(repo: RoomRepository, topicId: Int, onDismiss: () -> Unit) {
    var front by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var isDownloading by remember { mutableStateOf(false) }
    var downloadedLocalPath by remember { mutableStateOf<String?>(null) }
    var downloadError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Добавить карточку",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                TextField(
                    value = front,
                    onValueChange = { front = it },
                    placeholder = { Text("Слово (иностранное)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = translation,
                    onValueChange = { translation = it },
                    placeholder = { Text("Перевод") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    placeholder = { Text("Ссылка на картинку (опционально)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (isDownloading) {
                    Spacer(Modifier.height(8.dp))
                    Text("Скачивание изображения...")
                }
                if (downloadedLocalPath != null) {
                    Spacer(Modifier.height(8.dp))
                    Text("Картинка загружена")
                }
                if (downloadError != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = downloadError ?: "Ошибка",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            scope.launch {
                                // Если задан URL и ещё не скачан — попытаемся скачать
                                if (imageUrl.isNotBlank() && downloadedLocalPath == null) {
                                    isDownloading = true
                                    downloadError = null
                                    val path = try {
                                        repo.downloadImageToLocalPath(imageUrl)
                                    } catch (e: Exception) {
                                        null
                                    }
                                    if (path != null) downloadedLocalPath = path
                                    else downloadError = "Не удалось скачать изображение"
                                    isDownloading = false
                                }

                                if (front.isNotBlank()) {

                                    val c = CardEntity(
                                        id = 0, // Будет сгенерировано автоматически
                                        topicId = topicId,
                                        frontText = front.trim(),
                                        translation = translation.ifBlank { "" }, // backText не может быть null
                                        description = description,
                                        imagePath = downloadedLocalPath
                                    )
                                    repo.addCard(c)
                                    onDismiss()
                                }
                            }
                        }
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}