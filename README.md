# ios-style-wheel-picker

A modern, highly customizable iOS-style wheel picker for Android built with Jetpack Compose.

## Features
- **iOS-Style Physics:** Smooth snapping scroll behavior mimicking native iOS picker behavior.
- **Visual Depth Effect:** Dynamically scales and fades out items based on their distance from the center.
- **Fully Generic:** Supports lists of any data type (`List<T>`).
- **Completely Customizable:** Custom colors, text styles, sizes, and support for completely custom item layouts (e.g., emojis, icons, custom cards).

---

## Installation

### Step 1: Add the JitPack repository to your settings
In your root `settings.gradle.kts` (or `build.gradle`):

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // <-- Add this line
    }
}
```

### Step 2: Add the dependency
In your app module `build.gradle.kts` (or `build.gradle`):

```kotlin
dependencies {
    implementation("com.github.cys020628:ios-style-wheel-picker:v1.0.0")
}
```

---

## Usage

### 1. Simple Text Picker (e.g., Year Picker)

```kotlin
val years = (1990..2030).toList()
var selectedYear by remember { mutableStateOf(2026) }

WheelPicker(
    items = years,
    initialItem = selectedYear,
    itemHeight = 36.dp,
    visibleCount = 3,
    selectedColor = Color(0xFF007AFF), // iOS Blue
    unselectedColor = Color.Gray,
    textStyle = TextStyle(fontSize = 18.sp),
    onSelected = { year ->
        selectedYear = year
    }
)
```

### 2. Time Picker (Combining Pickers)

```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    // AM/PM Picker
    WheelPicker(
        items = listOf("오전", "오후"),
        initialItem = "오전",
        modifier = Modifier.weight(1f),
        onSelected = { ampm -> /* Handle selection */ }
    )
    // Hour Picker
    WheelPicker(
        items = (1..12).toList(),
        initialItem = 9,
        modifier = Modifier.weight(1f),
        onSelected = { hour -> /* Handle selection */ }
    )
    // Minute Picker
    WheelPicker(
        items = (0..59).map { String.format("%02d", it) },
        initialItem = "00",
        modifier = Modifier.weight(1f),
        onSelected = { minute -> /* Handle selection */ }
    )
}
```

### 3. Custom Item Rendering (e.g., Emoji / Complex Layouts)

You can pass an `itemContent` composable lambda to render anything you want:

```kotlin
val emojis = listOf("😀", "🥰", "😎", "🤔", "🥳")

WheelPicker(
    items = emojis,
    initialItem = "😀",
    itemHeight = 44.dp,
    onSelected = { emoji -> /* Handle selection */ }
) { emoji, isSelected ->
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color(0xFFFFD60A) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text = emoji, fontSize = 24.sp)
    }
}
```

---

## License
```
Copyright 2026 cys020628

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
