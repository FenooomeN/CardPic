package com.example.cardpic.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cardpic.data.RoomRepository
import com.example.cardpic.data.entity.CardEntity
import com.example.cardpic.ui.components.FlipCard
import kotlinx.coroutines.launch

/**
 * Экран тренировки: берет все карточки, перемешивает и показывает по одной.
 * По нажатию карточка переворачивается (FlipCard реализует поворот).
 */
@Composable
fun TrainingScreen(repo: RoomRepository, onDone: () -> Unit) {
    var cards by remember { mutableStateOf(emptyList<CardEntity>()) }
    var index by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    // Состояние для отслеживания переворота текущей карточки
    var isFlipped by remember { mutableStateOf(false) }

    // Создаем coroutine scope для обработки асинхронных операций
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        cards = repo.getAllCards().shuffled()
        isLoading = false
    }

    // Сбрасываем состояние переворота при изменении индекса
    LaunchedEffect(index) {
        isFlipped = false
    }

    if (isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (cards.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Нет карточек для тренировки")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onDone) { Text("Вернуться") }
        }
        return
    }

    val current = cards.getOrNull(index)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Тренировка", style = MaterialTheme.typography.titleMedium)
            Button(onClick = onDone) { Text("Завершить") }
        }

        Spacer(Modifier.height(12.dp))

        current?.let { c ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FlipCard(
                    card = c,
                    isFlipped = isFlipped,
                    onFlip = { isFlipped = !isFlipped },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Spacer(Modifier.height(12.dp))
                Row {
                    Button(onClick = {
                        index = (index + 1) % cards.size
                    }) {
                        Text("Следующий")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            // Используем coroutine scope для вызова suspend функции
                            scope.launch {
                                cards = repo.getAllCards().shuffled()
                                index = 0
                                isFlipped = false
                            }
                        }
                    ) {
                        Text("Перемешать")
                    }
                }
            }
        }
    }
}