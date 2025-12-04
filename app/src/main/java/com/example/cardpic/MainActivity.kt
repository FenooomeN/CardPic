package com.example.cardpic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.cardpic.data.AppDatabase
import com.example.cardpic.data.RoomRepository
import com.example.cardpic.ui.navigation.Screen
import com.example.cardpic.ui.screens.HomeScreen
import com.example.cardpic.ui.screens.TopicScreen
import com.example.cardpic.ui.screens.TrainingScreen

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация Room DB
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "cardpic.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        setContent {

            // RoomRepository создаётся с DAO и контекстом
            val repo by remember {
                mutableStateOf(
                    RoomRepository(
                        topicDao = database.topicDao(),
                        cardDao = database.cardDao(),
                        context = applicationContext
                    )
                )
            }

            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("CardPic") }
                        )
                    }
                ) { innerPadding ->
                    when (val s = currentScreen) {

                        is Screen.Home -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            HomeScreen(
                                repo = repo,
                                onOpenTopic = { id, title ->
                                    currentScreen = Screen.TopicScreen(id, title)
                                },
                                onStartTraining = {
                                    currentScreen = Screen.Training
                                }
                            )
                        }

                        is Screen.TopicScreen -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            TopicScreen(
                                repo = repo,
                                topicId = s.topicId,
                                topicTitle = s.topicTitle,
                                onBack = {
                                    currentScreen = Screen.Home
                                }
                            )
                        }

                        is Screen.Training -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            TrainingScreen(
                                repo = repo,
                                onDone = {
                                    currentScreen = Screen.Home
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}