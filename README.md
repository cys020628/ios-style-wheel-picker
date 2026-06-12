# ios-style-wheel-picker

Jetpack Compose로 구축된 안드로이드용의 모던하고 고도로 커스터마이징 가능한 iOS 스타일 휠 피커 라이브러리입니다. 3D 입체 원기둥 회전 효과, 부드러운 자석 스냅 스크롤, 햅틱 피드백 등을 기본 제공하며 모든 요소를 자유롭게 커스텀할 수 있습니다.

---

## ⚙️ 설치 방법

### Step 1. Settings 설정
프로젝트 루트 폴더의 `settings.gradle.kts` 파일 내 repositories 블록에 JitPack을 등록합니다.

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
앱 모듈의 `build.gradle.kts` 파일에 의존성을 추가합니다.

```kotlin
dependencies {
    implementation("com.github.cys020628:ios-style-wheel-picker:v1.0.2")
}
```

---

## 🎨 모든 커스터마이징 매개변수 (Parameters)

`IosStylePicker`(또는 `WheelPicker`)는 누구나 쉽게 사용할 수 있도록 합리적인 기본값을 제공하며, 다음과 같은 매개변수들을 제공하여 완벽한 커스터마이징이 가능합니다.

| 매개변수명 (Parameter) | 데이터 타입 (Type) | 기본값 (Default) | 설명 (Description) |
|:---|:---|:---|:---|
| **`items`** | `List<T>` | **필수 (Required)** | 화면에 표시할 데이터 리스트 (String, Int 등 어떤 제네릭 타입이든 가능) |
| **`initialItem`** | `T` | **필수 (Required)** | 피커 로드 시 최초로 중앙에 선택된 상태로 둘 아이템 |
| **`onSelected`** | `(T) -> Unit` | **필수 (Required)** | 스크롤이 멈춰서 새로운 아이템이 중앙에 위치(선택)했을 때 선택된 값을 반환하는 콜백 함수 |
| `modifier` | `Modifier` | `Modifier` | 피커 컨테이너의 가로/세로 크기, 마진 등을 조절하기 위한 Modifier |
| `itemHeight` | `Dp` | `36.dp` | 개별 아이템 행(Row) 하나의 가로 세로 영역 중 세로 높이 |
| `visibleCount` | `Int` | `3` | 동시에 보여줄 행의 개수 (상하 대칭 정렬을 위해 3, 5, 7 등 **홀수** 권장) |
| `selectedColor` | `Color` | `Color(0xFF007AFF)` | 기본 텍스트 사용 시 **선택된(중앙)** 아이템의 텍스트 색상 (iOS 기본 블루) |
| `unselectedColor` | `Color` | `Color.Gray` | 기본 텍스트 사용 시 **선택되지 않은(상/하단)** 아이템들의 텍스트 색상 |
| `textStyle` | `TextStyle` | `TextStyle.Default` | 기본 텍스트에 적용될 전반적인 폰트 스타일 (크기, 행간 등) |
| `cameraDistance` | `Float` | `8f` | 3D 입체 원근감을 표현할 카메라 거리값 (작을수록 원근 왜곡이 크고, 클수록 평평해짐) |
| `rotationXMax` | `Float` | `45f` | 상하 가장자리에 도달했을 때 가질 최대 X축 회전 각도 (`0f` 설정 시 3D 회전 없는 평면 모드) |
| `enableHaptic` | `Boolean` | `true` | 스크롤 중 새로운 아이템이 중앙에 걸릴 때마다 미세한 햅틱 진동 피드백 출력 여부 |
| `enableGradientMask` | `Boolean` | `true` | 상하단 아이템이 끝자락에서 자연스럽게 사라지도록 그라데이션 페이드아웃 오버레이를 덮을지 여부 |
| `maskColor` | `Color` | `Color.White` | 그라데이션 마스크의 색상 (**다크모드 지원 앱에서는 다크 배경색과 맞춰서 변경 권장**) |
| `enableDivider` | `Boolean` | `true` | 선택 영역(중앙행)의 경계를 구분 짓기 위한 가로 평행 가이드선 표시 여부 |
| `dividerColor` | `Color` | `Color.LightGray.copy(0.4f)` | 중앙 구분 가이드선의 색상 |
| `dividerThickness` | `Dp` | `0.5.dp` | 중앙 구분 가이드선의 두께 |
| `itemContent` | `@Composable (T, Boolean) -> Unit` | `null` | 기본 텍스트를 대체하여 이미지, 이모지, 커스텀 카드 형태의 커스텀 아이템 UI를 그리는 컴포저블 람다 |

---

## 💡 활용 예제 코드 (Examples)

### 1. 다크모드 대응 커스텀 피커
피커의 뒷배경이 어두울 경우, 마스크 색상(`maskColor`)과 텍스트 및 구분선 색상을 다크 테마에 맞춰 커스텀합니다.

```kotlin
IosStylePicker(
    items = listOf("2025", "2026", "2027", "2028"),
    initialItem = "2026",
    onSelected = { year -> /* 값 처리 */ },
    
    // 다크모드 커스텀 테마 적용
    selectedColor = Color.White,
    unselectedColor = Color.DarkGray,
    maskColor = Color(0xFF1C1C1E), // 다크 배경과 맞춘 마스킹 처리
    dividerColor = Color.Gray.copy(alpha = 0.5f),
    dividerThickness = 1.dp
)
```

### 2. 고강도 3D 원기둥 회전 효과 설정
X축 회전각(`rotationXMax`)을 키우고 카메라 거리(`cameraDistance`)를 조절해 좀 더 입체적이고 볼록한 원형 휠 효과를 극대화할 수 있습니다.

```kotlin
IosStylePicker(
    items = (1..12).toList(),
    initialItem = 1,
    onSelected = { month -> /* 값 처리 */ },
    
    // 원통 입체감 강조 설정
    rotationXMax = 60f,      // 더 가파르게 기울임 설정
    cameraDistance = 4f,     // 카메라 거리를 가깝게 하여 3D 투영 왜곡 극대화
    visibleCount = 5         // 더 많은 아이템을 보여줘서 입체 형태 구현
)
```

### 3. 평면 자석형(Snapping) 피커 모드
3D 입체 효과가 필요하지 않고 깔끔한 2D 플랫 피커를 구현하고 싶은 경우 `rotationXMax`를 `0f`로 설정하고 구분선을 조절합니다.

```kotlin
IosStylePicker(
    items = listOf("오전", "오후"),
    initialItem = "오전",
    onSelected = { ampm -> /* 값 처리 */ },
    
    // 평면 모드로 가볍게 설정
    rotationXMax = 0f,
    enableGradientMask = false, // 상하단 페이드 마스크 비활성화
    dividerColor = Color(0xFF007AFF), // 구분선을 포인트 컬러로 설정
    dividerThickness = 1.5.dp
)
```

### 4. 렌더링 커스터마이징 (이모지 & 텍스트 크기 가변화)
`itemContent`를 사용하면 텍스트뿐만 아니라 어떤 컴포저블 레이아웃도 휠 형태로 스크롤 할 수 있으며, 3D 원근 투영 및 투명도 페이드 아웃 효과는 컴포저블 컨테이너에 자동으로 반영됩니다.

```kotlin
IosStylePicker(
    items = listOf("😀", "🥰", "😎", "🤔", "🥳"),
    initialItem = "😀",
    itemHeight = 48.dp,
    onSelected = { emoji -> /* 값 처리 */ }
) { emoji, isSelected ->
    // 커스텀 컴포저블 배치
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isSelected) Color(0xFFFFD60A) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(6.dp)
        ) {
            Text(text = emoji, fontSize = 24.sp)
        }
    }
}
```

---

## 📄 라이선스 (License)
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
