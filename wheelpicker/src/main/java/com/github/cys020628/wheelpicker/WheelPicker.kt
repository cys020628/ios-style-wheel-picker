package com.github.cys020628.wheelpicker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlin.OptIn
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.min

/**
 * A customizable iOS-style wheel picker built on Jetpack Compose.
 *
 * @param T The type of items in the list.
 * @param items The list of data to display in the wheel picker.
 * @param modifier The modifier to be applied to the wheel picker layout.
 * @param initialItem The item that should be initially selected.
 * @param itemHeight The height of each item in the picker. Default is 36.dp.
 * @param visibleCount The number of items visible simultaneously. Must be an odd number (e.g., 3, 5, 7) for symmetrical alignment.
 * @param onSelected Callback invoked when an item is centered and selected.
 * @param selectedColor The color of the selected (centered) text item.
 * @param unselectedColor The color of the unselected text items.
 * @param textStyle The base text style to be applied to text items.
 * @param itemContent Optional composable lambda to custom render each item. If provided, default text rendering is bypassed.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    items: List<T>,
    modifier: Modifier = Modifier,
    initialItem: T,
    itemHeight: Dp = 36.dp,
    visibleCount: Int = 3,
    onSelected: (T) -> Unit,
    selectedColor: Color = Color(0xFF007AFF), // iOS default blue
    unselectedColor: Color = Color.Gray,
    textStyle: TextStyle = TextStyle.Default,
    itemContent: (@Composable (item: T, isSelected: Boolean) -> Unit)? = null,
) {
    /** Calculate initial index */
    val initialIndex = items.indexOf(initialItem).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    /** Calculate current centered item index */
    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf -1

            val viewportCenter =
                (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2 +
                        layoutInfo.viewportStartOffset

            visibleItems.minByOrNull { item ->
                val itemCenter = item.offset + (item.size / 2)
                abs(viewportCenter - itemCenter)
            }?.index ?: -1
        }
    }

    /** Trigger callback when scrolling stops and item is centered */
    LaunchedEffect(listState.isScrollInProgress, centerIndex) {
        if (!listState.isScrollInProgress && centerIndex in items.indices) {
            onSelected(items[centerIndex])
        }
    }

    Box(
        modifier = modifier.height(itemHeight * visibleCount),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeight * ((visibleCount - 1) / 2))
        ) {
            items(items.size) { index ->
                val item = items[index]
                /** Relative distance from the center index */
                val distanceFromCenter = if (centerIndex != -1) {
                    (index - centerIndex).toFloat()
                } else {
                    0f
                }

                /** Normalize distance range [0f, 1f] */
                val normalizedDistance = min(1f, abs(distanceFromCenter))
                
                /** Scale factor (1f at center, decreasing to 0.85f) */
                val scale = 1f - 0.15f * normalizedDistance

                /** Alpha factor (1f at center, decreasing to 0.3f) */
                val alpha = 0.3f + (1f - normalizedDistance) * 0.7f

                val isSelected = (index == centerIndex)

                if (itemContent != null) {
                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth()
                            .alpha(alpha)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        itemContent(item, isSelected)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.toString(),
                            style = textStyle.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) selectedColor else unselectedColor,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .alpha(alpha)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                        )
                    }
                }
            }
        }
    }
}
