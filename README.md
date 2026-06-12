# ios-style-wheel-picker

Jetpack Compose로 구축된 안드로이드용의 모던하고 고도로 커스터마이징 가능한 iOS 스타일 휠 피커 라이브러리입니다.

## 주요 기능
- **iOS 스타일 스크롤 효과:** Compose Foundation의 스냅 가이드를 활용한 부드럽고 끈끈한 스크롤 동작 지원.
- **시각적 입체감 효과:** 중앙과의 거리에 따라 아이템 크기(Scale)와 투명도(Alpha)를 동적으로 변환하여 3D 실린더 휠 형태 제공.
- **제네릭 타입 지원:** 어떤 형태의 데이터 객체 리스트(`List<T>`)도 즉시 바인딩 가능.
- **완벽한 스타일 제어:** 텍스트 크기, 폰트 스타일, 중앙 하이라이트 색상 설정 가능 및 사용자 정의 레이아웃 렌더링 람다(`itemContent`) 지원.

---

## 설치 방법

### Step 1. Settings 설정
프로젝트 루트 폴더의 `settings.gradle.kts` (또는 `build.gradle`) 파일 내 repositories 블록에 JitPack을 등록합니다.

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // <-- 이 줄 추가
    }
}
```

### Step 2. 라이브러리 의존성 추가
앱 모듈의 `build.gradle.kts` (또는 `build.gradle`) 파일에 라이브러리를 추가합니다.

```kotlin
dependencies {
    implementation("com.github.cys020628:ios-style-wheel-picker:v1.0.2")
}
```

---

## 사용 방법

### 1. 기본 텍스트 피커 (예: 연도 선택)
`IosStylePicker` 또는 `WheelPicker` 컴포넌트를 둘 다 호출해 사용할 수 있습니다.

```kotlin
val years = (1990..2030).toList()
var selectedYear by remember { mutableStateOf(2026) }

IosStylePicker(
    items = years,
    initialItem = selectedYear,
    itemHeight = 36.dp,
    visibleCount = 3,
    selectedColor = Color(0xFF007AFF), // iOS 파란색
    unselectedColor = Color.Gray,
    textStyle = TextStyle(fontSize = 18.sp),
    onSelected = { year ->
        selectedYear = year
    }
)
```

### 2. 다중 피커 결합 (예: 시간 선택)

```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    // 오전/오후 선택 휠
    IosStylePicker(
        items = listOf("오전", "오후"),
        initialItem = "오전",
        modifier = Modifier.weight(1f),
        onSelected = { ampm -> /* 선택 값 처리 */ }
    )
    // 시(Hour) 선택 휠
    IosStylePicker(
        items = (1..12).toList(),
        initialItem = 9,
        modifier = Modifier.weight(1f),
        onSelected = { hour -> /* 선택 값 처리 */ }
    )
    // 분(Minute) 선택 휠
    IosStylePicker(
        items = (0..59).map { String.format("%02d", it) },
        initialItem = "00",
        modifier = Modifier.weight(1f),
        onSelected = { minute -> /* 선택 값 처리 */ }
    )
}
```

### 3. 아이템 레이아웃 커스터마이징 (예: 이모지 / 커스텀 카드)

`itemContent` 컴포저블 람다식을 정의하면 기본 텍스트 렌더링 대신 임의의 UI를 렌더링할 수 있습니다.

```kotlin
val emojis = listOf("😀", "🥰", "😎", "🤔", "🥳")

IosStylePicker(
    items = emojis,
    initialItem = "😀",
    itemHeight = 44.dp,
    onSelected = { emoji -> /* 선택 값 처리 */ }
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

## 라이선스
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
