# EmergencyMesh Android Application

This is the Android application for EmergencyMesh - a decentralized emergency communication system using Bluetooth Low Energy (BLE).

## Project Structure

```
android/
├── app/
│   ├── build.gradle.kts          # App module build configuration
│   ├── proguard-rules.pro        # ProGuard rules
│   └── src/main/
│       ├── AndroidManifest.xml   # App manifest with permissions
│       ├── java/com/emergencymesh/app/
│       │   ├── MainActivity.kt   # Main activity with navigation
│       │   ├── data/
│       │   │   ├── entity/       # Room entities
│       │   │   │   ├── Peer.kt
│       │   │   │   ├── Message.kt
│       │   │   │   └── SeenMessageId.kt
│       │   │   ├── dao/          # Room DAOs
│       │   │   │   ├── PeerDao.kt
│       │   │   │   ├── MessageDao.kt
│       │   │   │   └── SeenMessageIdDao.kt
│       │   │   └── database/     # Room database
│       │   │       ├── AppDatabase.kt
│       │   │       └── Converters.kt
│       │   ├── viewmodel/        # ViewModels
│       │   │   ├── OnboardingViewModel.kt
│       │   │   ├── HomeViewModel.kt
│       │   │   └── SettingsViewModel.kt
│       │   └── ui/
│       │       ├── screens/      # Compose screens
│       │       │   ├── OnboardingScreen.kt
│       │       │   ├── HomeScreen.kt
│       │       │   └── SettingsScreen.kt
│       │       └── theme/        # Material 3 theme
│       │           ├── Color.kt
│       │           ├── Theme.kt
│       │           └── Type.kt
│       └── res/                  # Resources
│           ├── values/
│           │   ├── strings.xml
│           │   ├── themes.xml
│           │   └── colors.xml
│           └── mipmap-*/         # Launcher icons
├── gradle/
│   └── wrapper/                  # Gradle wrapper
├── build.gradle.kts              # Project build configuration
├── settings.gradle.kts           # Project settings
├── gradle.properties             # Gradle properties
└── gradlew                       # Gradle wrapper script
```

## Features Implemented

### ✅ Database Layer (Room)
- **Peer Entity**: Tracks discovered and connected peers with connection status
- **Message Entity**: Stores messages with TTL, sender info, and local/received flag
- **SeenMessageId Entity**: Prevents message duplication
- **DAOs**: Full CRUD operations with Flow for reactive updates

### ✅ UI Screens (Jetpack Compose + Material 3)
- **OnboardingScreen**: Set nickname, create/join groups
- **HomeScreen**: Relay toggle, peer count, Bluetooth status, message feed
- **SettingsScreen**: Data retention, scanning frequency settings

### ✅ ViewModels
- **OnboardingViewModel**: Manages onboarding state and saves user preferences
- **HomeViewModel**: Manages home screen state, messages, and relay toggle
- **SettingsViewModel**: Manages app settings with persistence

### ✅ Navigation
- Navigation Component with Compose
- Conditional start destination based on onboarding completion
- Proper back stack management

### ✅ Permissions (AndroidManifest.xml)
- Android 12+ permissions: `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, `BLUETOOTH_ADVERTISE`
- Legacy permissions for Android 11 and below
- Foreground service permissions
- All permissions properly scoped with `maxSdkVersion` and `neverForLocation`

### ✅ Theme
- Material 3 design system
- Emergency-themed color scheme (Red primary, Green secondary)
- Dynamic color support on Android 12+
- Dark/Light theme support

## Technical Specifications

- **Target SDK**: 34 (Android 14)
- **Min SDK**: 23 (Android 6.0 Marshmallow)
- **Language**: Kotlin 1.9.20
- **Build System**: Gradle 8.2 with Kotlin DSL
- **Jetpack Libraries**:
  - Compose (UI framework)
  - Room 2.6.1 (Database)
  - Navigation Compose 2.7.6
  - Lifecycle ViewModel 2.7.0
- **Coroutines**: 1.7.3 for async operations

## Building the Project

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 34
- Android SDK Build Tools

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Run Android instrumentation tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean build
```

## Architecture

This application follows the architecture documented in [ARCHITECTURE.md](../ARCHITECTURE.md):

- **UI Layer**: Jetpack Compose screens with Material 3
- **Application Layer**: ViewModels manage state and business logic
- **Data Layer**: Room database for persistence
- **BLE Layer**: (To be implemented) Scanner, Advertiser, GATT Server/Client

## Current Status

### ✅ Completed
- Project structure and Gradle configuration
- Room database with all entities and DAOs
- All UI screens with Material 3 design
- ViewModels with StateFlow
- Navigation setup
- Permissions in AndroidManifest
- Material 3 theme

### 🔨 To Be Implemented
- BLE Scanner service
- BLE Advertiser service
- GATT Server implementation
- GATT Client implementation
- Message relay logic
- Peer discovery and management
- Message encryption (future)
- Background foreground service

## Permissions

See [docs/permissions.md](../docs/permissions.md) for detailed information about all permissions required by this app.

Key permissions:
- **Bluetooth**: Required for peer-to-peer communication
- **Location** (Android < 12): System requirement for BLE scanning
- **Foreground Service**: For background message relay
- **Notifications**: For message alerts

## Testing

### Manual Testing
1. Install on two or more Android devices
2. Complete onboarding on each device
3. Enable relay mode
4. Grant Bluetooth permissions
5. Send messages and verify delivery

### Unit Tests
```bash
./gradlew test
```

### UI Tests
```bash
./gradlew connectedAndroidTest
```

## Development Notes

- The app uses SharedPreferences for simple key-value storage (nickname, settings)
- Room database is used for messages, peers, and seen message IDs
- All database operations use Kotlin Coroutines with proper dispatchers
- UI state is managed with StateFlow for reactive updates
- Navigation uses Compose Navigation component

## License

See [LICENSE](../LICENSE) for details.

## Contributing

See [CONTRIBUTING.md](../CONTRIBUTING.md) for guidelines.

## Security

See [SECURITY.md](../SECURITY.md) for security considerations.
