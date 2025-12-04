package com.example.cardpic.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cardpic.data.RoomRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repo: RoomRepository,
    onOpenTopic: (Int, String) -> Unit,
    onStartTraining: () -> Unit
) {
    var showCreateTopic by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Получаем список тем из Room - используем публичный метод
    val topics by repo
        .getAllTopicsFlow()
        .collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { showCreateTopic = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить тему")
            }
            Spacer(Modifier.weight(1f))
            Button(onClick = onStartTraining) {
                Text("Тренировка")
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(topics) { topic ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onOpenTopic(topic.id, topic.title) }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = topic.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Карточки внутри темы - используем публичный метод
                        val cardCount by repo
                            .getCardsCountFlow(topic.id)
                            .collectAsState(initial = 0)

                        if (cardCount > 0) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Карточек: $cardCount",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateTopic) {
        AlertDialog(
            onDismissRequest = { showCreateTopic = false },
            title = { Text("Создать тему") },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Название темы") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            scope.launch {
                                repo.addTopic(newTitle.trim())
                                newTitle = ""
                                showCreateTopic = false
                            }
                        }
                    }
                ) {
                    Text("Создать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTopic = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}