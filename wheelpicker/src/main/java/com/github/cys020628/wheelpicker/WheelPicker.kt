package com.github.cys020628.wheelpicker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.OptIn
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.cos

/**
 * Customizable iOS-style wheel picker built on Jetpack Compose.
 * Jetpack Compose 기반 고도로 커스터마이징 가능한 iOS 스타일 휠 피커 컴포넌트.
 *
 * @param T The type of items in the list. / 아이템 리스트의 제네릭 타입.
 * @param items The list of data to display in the wheel picker. / 화면에 표시할 데이터 리스트.
 * @param modifier The modifier to be applied to the wheel picker layout. / 레이아웃 수정을 위한 모디파이어.
 * @param initialItem The item that should be initially selected. / 최초 선택 상태로 지정할 아이템.
 * @param itemHeight The height of each item in the picker. Default is 36.dp. / 개별 아이템의 높이.
 * @param visibleCount The number of items visible simultaneously. Must be an odd number. / 동시에 보여줄 아이템 개수 (홀수 권장).
 * @param onSelected Callback invoked when an item is centered and selected. / 아이템이 중앙에 고정되어 선택 완료되었을 때 실행할 콜백.
 * @param selectedColor The color of the selected (centered) text item. / 선택된 아이템의 텍스트 색상.
 * @param unselectedColor The color of the unselected text items. / 선택되지 않은 아이템의 텍스트 색상.
 * @param textStyle The base text style to be applied to text items. / 텍스트 기본 스타일 지정.
 * @param cameraDistance 3D perspective camera distance. / 3D 원근감을 위한 카메라 거리값.
 * @param rotationXMax Maximum X-axis rotation angle in degrees. / 최대 X축 회전 각도.
 * @param enableHaptic Trigger light haptic tick when centered item changes. / 아이템 변경 시 미세한 진동 피드백 활성화 여부.
 * @param enableGradientMask Enable edge fade-out gradient mask at the top/bottom. / 상하단 페이드아웃 그라데이션 적용 여부.
 * @param maskColor Color of the gradient mask, should match background. / 그라데이션 마스크의 색상 (배경색과 일치 권장).
 * @param enableDivider Draw horizontal divider lines to highlight the selected center row. / 선택 영역을 강조하기 위한 두 개의 가로 구분선 활성화 여부.
 * @param dividerColor Color of the horizontal dividers. / 가로 구분선 색상.
 * @param dividerThickness Thickness of the horizontal dividers. / 가로 구분선 두께.
 * @param itemContent Optional composable lambda to custom render each item. / 개별 아이템 렌더링 커스터마이징을 위한 컴포저블 람다 식.
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
    selectedColor: Color = Color(0xFF007AFF), // iOS Default Blue / iOS 기본 파란색
    unselectedColor: Color = Color.Gray,
    textStyle: TextStyle = TextStyle.Default,
    cameraDistance: Float = 8f,
    rotationXMax: Float = 45f,
    enableHaptic: Boolean = true,
    enableGradientMask: Boolean = true,
    maskColor: Color = Color.White,
    enableDivider: Boolean = true,
    dividerColor: Color = Color.LightGray.copy(alpha = 0.4f),
    dividerThickness: Dp = 0.5.dp,
    itemContent: (@Composable (item: T, isSelected: Boolean) -> Unit)? = null,
) {
    // Calculate initial selection index
    // 초기 선택 인덱스 계산
    val initialIndex = items.indexOf(initialItem).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    // Access haptic feedback controller
    // 햅틱 피드백 컨트롤러 정의
    val haptic = LocalHapticFeedback.current

    // Calculate current centered item index
    // 현재 중앙에 위치한 아이템 인덱스 계산
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

    // Trigger haptic feedback when centered index changes
    // 중앙 인덱스가 변경될 때마다 햅틱 진동 피드백 실행
    LaunchedEffect(centerIndex) {
        if (enableHaptic && centerIndex != -1) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    // Trigger selected callback when scrolling stops and item is centered
    // 스크롤이 멈추고 새로운 아이템이 선택되었을 때 콜백 호출
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
                // Calculate relative distance from the center index
                // 중앙 인덱스로부터의 상대적 거리 계산
                val distanceFromCenter = if (centerIndex != -1) {
                    (index - centerIndex).toFloat()
                } else {
                    0f
                }

                // Normalize distance range [0f, 1f]
                // 거리 값 [0f, 1f] 범위로 정규화
                val normalizedDistance = min(1f, abs(distanceFromCenter))
                
                // Calculate X-axis rotation angle to simulate 3D cylinder rotation
                // 3D 원기둥 회전 효과를 위한 X축 회전 각도 계산
                val maxDistance = (visibleCount / 2).toFloat().coerceAtLeast(1f)
                val rotationXVal = (distanceFromCenter / maxDistance) * rotationXMax

                // Convert angle to radians for trigonometric calculations
                // 삼각함수 연산을 위한 호도법 라디안 변환
                val radians = rotationXVal * (Math.PI.toFloat() / 180f)
                
                // Calculate scale values (scaleY contracts based on cosine of X-rotation to simulate perspective projection)
                // 스케일 팩터 계산 (세로 스케일은 코사인 함수를 적용하여 원근 압축 효과 투영)
                val scaleXVal = 1f - 0.15f * normalizedDistance
                val scaleYVal = cos(radians) * scaleXVal

                // Calculate alpha value (center is 1f, fades out toward edges)
                // 알파 값 계산 (중앙 1f, 멀어질수록 0.3f까지 부드럽게 감쇠)
                val alpha = 0.3f + (1f - normalizedDistance) * 0.7f

                val isSelected = (index == centerIndex)

                if (itemContent != null) {
                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth()
                            .alpha(alpha)
                            .graphicsLayer {
                                rotationX = rotationXVal
                                scaleX = scaleXVal
                                scaleY = scaleYVal
                                cameraDistance = cameraDistance * density
                                transformOrigin = TransformOrigin(0.5f, 0.5f)
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
                                    rotationX = rotationXVal
                                    scaleX = scaleXVal
                                    scaleY = scaleYVal
                                    cameraDistance = cameraDistance * density
                                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                                }
                        )
                    }
                }
            }
        }

        // Horizontal Separator Lines for Selection Area
        // 선택 영역의 가로 평행선 구분선 드로잉
        if (enableDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .align(Alignment.Center)
            ) {
                // Top Divider / 상단 구분선
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dividerThickness)
                        .background(dividerColor)
                        .align(Alignment.TopCenter)
                )
                // Bottom Divider / 하단 구분선
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dividerThickness)
                        .background(dividerColor)
                        .align(Alignment.BottomCenter)
                )
            }
        }

        // Top/Bottom Edge Fade-out Gradient Masks
        // 상하단 가장자리 페이드아웃 그라데이션 마스크 처리
        if (enableGradientMask) {
            val maskHeight = itemHeight * ((visibleCount - 1) / 2)
            // Top Mask / 상단 그라데이션 마스크
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maskHeight)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(maskColor, Color.Transparent)
                        )
                    )
            )
            // Bottom Mask / 하단 그라데이션 마스크
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maskHeight)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, maskColor)
                        )
                    )
            )
        }
    }
}

/**
 * Composable alias for WheelPicker with identical configuration.
 * WheelPicker와 동일한 구성을 가지는 별칭 컴포넌트.
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
    cameraDistance: Float = 8f,
    rotationXMax: Float = 45f,
    enableHaptic: Boolean = true,
    enableGradientMask: Boolean = true,
    maskColor: Color = Color.White,
    enableDivider: Boolean = true,
    dividerColor: Color = Color.LightGray.copy(alpha = 0.4f),
    dividerThickness: Dp = 0.5.dp,
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
        cameraDistance = cameraDistance,
        rotationXMax = rotationXMax,
        enableHaptic = enableHaptic,
        enableGradientMask = enableGradientMask,
        maskColor = maskColor,
        enableDivider = enableDivider,
        dividerColor = dividerColor,
        dividerThickness = dividerThickness,
        itemContent = itemContent
    )
}
