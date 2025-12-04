package com.example.cardpic.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardpic.data.entity.CardEntity

/**
 * FlipCard — отображает карточку со словом на фронте и
 * подробностями + картинкой на обратной стороне.
 * Нажатием карточка переворачивается.
 *
 * @param card Карточка для отображения
 * @param isFlipped Управляемое состояние переворота
 * @param onFlip Callback при нажатии на карточку
 * @param modifier Модификатор компоновки
 */
@Composable
fun FlipCard(
    card: CardEntity,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        label = "cardRotation"
    )
    val density = LocalDensity.current.density

    // Вычисляем видимость сторон на основе поворота
    val frontVisible = rotation <= 90f
    val backVisible = rotation > 90f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onFlip() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            // Front side
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                        alpha = if (frontVisible) 1f else 0f
                    }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = card.frontText, fontSize = 24.sp)
            }

            // Back side
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = rotation - 180f // Поворачиваем в противоположную сторону
                        cameraDistance = 12f * density
                        alpha = if (backVisible) 1f else 0f
                    }
                    .padding(12.dp)
            ) {
                Text(text = card.translation ?: "", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = card.description ?: "")
                Spacer(modifier = Modifier.height(8.dp))

                card.imagePath?.let { path ->
                    val bitmap = remember(path) { BitmapFactory.decodeFile(path) }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        )
                    }
                }
            }
        }
    }
}