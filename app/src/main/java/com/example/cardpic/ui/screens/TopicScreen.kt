package com.example.cardpic.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cardpic.data.RoomRepository
import com.example.cardpic.ui.components.FlipCard
import com.example.cardpic.ui.dialogs.AddCardDialog

/**
 * Экран выбранной темы. Показывает карточки темы и позволяет добавлять новые карточки.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    repo: RoomRepository,
    topicId: Int,
    topicTitle: String,
    onBack: () -> Unit
) {
    var showAddCard by remember { mutableStateOf(false) }

    // Подписка на карточки через Flow
    val cards by repo.cardDao
        .getCardsForTopicFlow(topicId)
        .collectAsState(initial = emptyList())

    // состояние переворота карточек
    val flippedStates = remember { mutableStateMapOf<Int, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topicTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddCard = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить карточку")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (cards.isEmpty()) {
                Text("Нет карточек. Нажмите + чтобы добавить.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(cards) { card ->
                        val isFlipped = flippedStates[card.id] ?: false

                        FlipCard(
                            card = card,
                            isFlipped = isFlipped,
                            onFlip = {
                                flippedStates[card.id] = !isFlipped
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (showAddCard) {
            AddCardDialog(
                repo = repo,
                topicId = topicId,
                onDismiss = { showAddCard = false }
            )
        }
    }
}
