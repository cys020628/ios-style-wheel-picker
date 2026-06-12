package com.github.cys020628.wheelpicker.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cys020628.wheelpicker.WheelPicker
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF2F2F7) // iOS light background
                ) {
                    SampleScreen()
                }
            }
        }
    }
}

@Composable
fun SampleScreen() {
    val selectedYear = remember { mutableStateOf(2026) }
    val selectedAmPm = remember { mutableStateOf("오전") }
    val selectedHour = remember { mutableStateOf(9) }
    val selectedMinute = remember { mutableStateOf(0) }
    val selectedEmoji = remember { mutableStateOf("😀") }

    val years = (1950..2100).toList()
    val amPmList = listOf("오전", "오후")
    val hours = (1..12).toList()
    val minutes = (0..59).toList()
    val emojis = listOf("😀", "😂", "🥰", "😎", "🤔", "😱", "🥳", "👻", "🐶", "🚀")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "iOS Style Wheel Picker Demo",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // 1. Year Picker Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "1. 연도 선택 피커 (선택: ${selectedYear.value}년)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Wheel highlight background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(Color(0xFFE5E5EA), shape = RoundedCornerShape(8.dp))
                    )
                    WheelPicker(
                        items = years,
                        initialItem = selectedYear.value,
                        itemHeight = 36.dp,
                        visibleCount = 3,
                        selectedColor = Color(0xFF007AFF),
                        unselectedColor = Color.Gray,
                        textStyle = TextStyle(fontSize = 18.sp),
                        onSelected = { year ->
                            selectedYear.value = year
                        }
                    )
                }
            }
        }

        // 2. Time Picker Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "2. 시간 선택 피커 (선택: ${selectedAmPm.value} ${selectedHour.value}시 ${String.format(Locale.US, "%02d", selectedMinute.value)}분)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(Color(0xFFE5E5EA), shape = RoundedCornerShape(8.dp))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WheelPicker(
                            items = amPmList,
                            initialItem = selectedAmPm.value,
                            modifier = Modifier.weight(1f),
                            onSelected = { amPm -> selectedAmPm.value = amPm }
                        )
                        WheelPicker(
                            items = hours,
                            initialItem = selectedHour.value,
                            modifier = Modifier.weight(1f),
                            onSelected = { hour -> selectedHour.value = hour }
                        )
                        Text(
                            text = ":",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007AFF),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        val minuteStrings = minutes.map { String.format(Locale.US, "%02d", it) }
                        WheelPicker(
                            items = minuteStrings,
                            initialItem = String.format(Locale.US, "%02d", selectedMinute.value),
                            modifier = Modifier.weight(1f),
                            onSelected = { minuteStr -> selectedMinute.value = minuteStr.toInt() }
                        )
                    }
                }
            }
        }

        // 3. Custom Content Picker Card (Emoji)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "3. 커스텀 아이템 피커 (선택: ${selectedEmoji.value})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(Color(0xFFFFD60A).copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    )
                    WheelPicker(
                        items = emojis,
                        initialItem = selectedEmoji.value,
                        itemHeight = 44.dp,
                        visibleCount = 3,
                        onSelected = { emoji -> selectedEmoji.value = emoji }
                    ) { emoji, isSelected ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) Color(0xFFFFD60A) else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
