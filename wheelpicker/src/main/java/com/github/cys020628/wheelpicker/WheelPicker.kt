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
import kotlin.OptIn
import kotlin.math.abs
import kotlin.math.min

/**
 * Jetpack Compose 기반 커스텀 iOS 스타일 휠 피커 컴포넌트
 *
 * @param T 아이템 리스트의 제네릭 타입
 * @param items 화면에 표시할 데이터 리스트
 * @param modifier 레이아웃 수정을 위한 모디파이어
 * @param initialItem 최초 선택 상태로 지정할 아이템
 * @param itemHeight 개별 아이템의 높이 (기본값: 36.dp)
 * @param visibleCount 동시에 보여줄 아이템 개수 (중앙 정렬을 위해 홀수 권장)
 * @param onSelected 아이템이 중앙에 고정되어 선택 완료되었을 때 실행할 콜백
 * @param selectedColor 선택된 아이템의 텍스트 색상
 * @param unselectedColor 선택되지 않은 아이템의 텍스트 색상
 * @param textStyle 텍스트 기본 스타일 지정
 * @param itemContent 개별 아이템 렌더링 커스터마이징을 위한 람다 식
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
    selectedColor: Color = Color(0xFF007AFF), // iOS 기본 블루 색상
    unselectedColor: Color = Color.Gray,
    textStyle: TextStyle = TextStyle.Default,
    itemContent: (@Composable (item: T, isSelected: Boolean) -> Unit)? = null,
) {
    /** 초기 선택 인덱스 계산 */
    val initialIndex = items.indexOf(initialItem).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    /** 현재 중앙에 위치한 아이템 인덱스 계산 */
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

    /** 스크롤이 멈추고 새로운 아이템이 선택되었을 때 콜백 호출 */
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
                /** 중앙 인덱스로부터의 상대적 거리 계산 */
                val distanceFromCenter = if (centerIndex != -1) {
                    (index - centerIndex).toFloat()
                } else {
                    0f
                }

                /** 거리 값 [0f, 1f] 범위로 정규화 */
                val normalizedDistance = min(1f, abs(distanceFromCenter))
                
                /** 스케일 팩터 계산 (중앙 1f, 멀어질수록 0.85f까지 축소) */
                val scale = 1f - 0.15f * normalizedDistance

                /** 알파 값 계산 (중앙 1f, 멀어질수록 0.3f까지 투명도 적용) */
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

/**
 * WheelPicker의 별칭 컴포넌트 (IosStylePicker 라는 이름으로도 동일하게 호출 가능)
 */
@Composable
fun <T> IosStylePicker(
    items: List<T>,
    modifier: Modifier = Modifier,
    initialItem: T,
    itemHeight: Dp = 36.dp,
    visibleCount: Int = 3,
    onSelected: (T) -> Unit,
    selectedColor: Color = Color(0xFF007AFF),
    unselectedColor: Color = Color.Gray,
    textStyle: TextStyle = TextStyle.Default,
    itemContent: (@Composable (item: T, isSelected: Boolean) -> Unit)? = null,
) {
    WheelPicker(
        items = items,
        modifier = modifier,
        initialItem = initialItem,
        itemHeight = itemHeight,
        visibleCount = visibleCount,
        onSelected = onSelected,
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        textStyle = textStyle,
        itemContent = itemContent
    )
}
