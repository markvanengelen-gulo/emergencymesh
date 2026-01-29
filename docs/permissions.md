# Android Permissions Guide

This document explains the Android permissions required by EmergencyMesh, why they're needed, and how they're used.

## Overview

EmergencyMesh requires several runtime permissions to function properly. All permissions are used solely for emergency communication functionality and are never used for tracking, advertising, or data collection.

## Required Permissions

### 1. Bluetooth Permissions

#### Android 12+ (API 31+)

**BLUETOOTH_SCAN**
```xml
<uses-permission android:name="android.permission.BLUETOOTH_SCAN"
    android:usesPermissionFlags="neverForLocation" />
```
- **Purpose**: Scan for nearby EmergencyMesh devices
- **When Used**: Continuously when app is active and relay mode is enabled
- **Alternative**: None - required for peer discovery
- **Privacy**: We set `neverForLocation` flag to indicate we don't use BLE for location tracking

**BLUETOOTH_CONNECT**
```xml
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```
- **Purpose**: Establish GATT connections to peers
- **When Used**: When sending/receiving messages from discovered peers
- **Alternative**: None - required for communication
- **Privacy**: Only connects to EmergencyMesh devices

**BLUETOOTH_ADVERTISE**
```xml
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
```
- **Purpose**: Advertise device presence to other EmergencyMesh devices
- **When Used**: When relay mode is enabled
- **Alternative**: None - required for discoverability
- **Privacy**: Only broadcasts EmergencyMesh service UUID and nickname

#### Android 11 and Below (API 23-30)

**BLUETOOTH**
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
```
- **Purpose**: Basic Bluetooth operations
- **When Used**: All BLE operations
- **Alternative**: None

**BLUETOOTH_ADMIN**
```xml
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```
- **Purpose**: BLE scanning and advertising
- **When Used**: Discovery and announcements
- **Alternative**: None

**ACCESS_FINE_LOCATION**
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```
- **Purpose**: Required by Android for BLE scanning (< Android 12)
- **When Used**: During BLE scanning on older Android versions
- **Alternative**: None on Android < 12
- **Privacy**: We never access GPS or location services directly. This is a system requirement for BLE scanning.

**Note**: On Android 10-11, `ACCESS_FINE_LOCATION` must be granted for BLE scanning to work, even though EmergencyMesh doesn't use location services.

### 2. Background Execution

**FOREGROUND_SERVICE**
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```
- **Purpose**: Keep app running when backgrounded to relay messages
- **When Used**: When relay mode is enabled
- **Alternative**: None - required for background operation
- **Impact**: Shows persistent notification when active

**FOREGROUND_SERVICE_CONNECTED_DEVICE** (Android 14+)
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />
```
- **Purpose**: Specifically for BLE connectivity in foreground service
- **When Used**: Android 14+ when relay service is running
- **Alternative**: None on Android 14+

### 3. Wake Lock (Optional)

**WAKE_LOCK**
```xml
<uses-permission android:name="android.permission.WAKE_LOCK" />
```
- **Purpose**: Keep CPU awake during message transmission
- **When Used**: Briefly during BLE operations
- **Alternative**: Best-effort delivery without wake lock
- **Impact**: Minor battery usage

## Permission Request Flow

### First Launch

1. User opens app for first time
2. Onboarding screen explains why permissions are needed
3. User clicks "Continue" to grant permissions
4. System shows permission dialogs in sequence:
   - Bluetooth permissions
   - Location permission (if Android < 12)
   - Notification permission (if Android 13+)
5. User can grant or deny each permission

### Handling Denials

**Partial Denial**: Some features disabled
- No Bluetooth → Cannot discover peers or send messages
- No Location (Android < 12) → Cannot scan for devices
- No Notifications → No alerts for new messages (app still works)

**Complete Denial**: App explains that permissions are required for functionality
- Shows explanation screen with "Grant Permissions" button
- User can choose to grant or use limited functionality

### Runtime Permission Checks

Before each BLE operation:

```kotlin
fun scanForDevices() {
    when {
        hasBluetoothPermissions() -> {
            // Proceed with scanning
        }
        shouldShowRationale() -> {
            // Show explanation to user
            showPermissionRationale()
        }
        else -> {
            // Request permissions
            requestBluetoothPermissions()
        }
    }
}
```

## Permissions by Android Version

| Permission | API 23-30 | API 31-32 | API 33+ | Why |
|------------|-----------|-----------|---------|-----|
| BLUETOOTH | ✅ | ❌ | ❌ | Legacy BLE access |
| BLUETOOTH_ADMIN | ✅ | ❌ | ❌ | Legacy BLE scanning |
| BLUETOOTH_SCAN | ❌ | ✅ | ✅ | Modern BLE scanning |
| BLUETOOTH_CONNECT | ❌ | ✅ | ✅ | Modern BLE connections |
| BLUETOOTH_ADVERTISE | ❌ | ✅ | ✅ | Modern BLE advertising |
| ACCESS_FINE_LOCATION | ✅ | ❌ | ❌ | System requirement for BLE < API 31 |
| FOREGROUND_SERVICE | ✅ | ✅ | ✅ | Background relay |
| FOREGROUND_SERVICE_CONNECTED_DEVICE | ❌ | ❌ | ✅ | Specific to BLE service |
| POST_NOTIFICATIONS | ❌ | ❌ | ✅ | Show message alerts |
| WAKE_LOCK | ✅ | ✅ | ✅ | Reliable delivery |

## Manifest Declaration

### Android 12+ (Target SDK 31+)

```xml
<manifest>
    <!-- Bluetooth permissions for Android 12+ -->
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN"
        android:usesPermissionFlags="neverForLocation" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    
    <!-- Legacy Bluetooth for older Android versions -->
    <uses-permission android:name="android.permission.BLUETOOTH"
        android:maxSdkVersion="30" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"
        android:maxSdkVersion="30" />
    
    <!-- Location only required on Android < 12 for BLE scanning -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"
        android:maxSdkVersion="30" />
    
    <!-- Background and notifications -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    
    <!-- Declare BLE feature -->
    <uses-feature android:name="android.hardware.bluetooth_le" android:required="true" />
    
    <application>
        <!-- Service declaration -->
        <service
            android:name=".service.EmergencyMeshService"
            android:foregroundServiceType="connectedDevice"
            android:exported="false" />
    </application>
</manifest>
```

## Privacy Guarantees

### What We DON'T Do

- ❌ Access GPS or location coordinates
- ❌ Track device location history
- ❌ Share location data with third parties
- ❌ Use location for analytics or advertising
- ❌ Access contacts, calendar, or other personal data
- ❌ Read other apps' data
- ❌ Access the internet (no network permission)

### What We DO Do

- ✅ Scan for nearby Bluetooth devices (only EmergencyMesh)
- ✅ Connect to EmergencyMesh peers only
- ✅ Advertise device presence (nickname only, no location)
- ✅ Store messages locally (never sent to cloud)
- ✅ Run in background to relay messages (when enabled)
- ✅ Show notifications for new messages

## User Control

Users can control permissions at any time:

### App Settings
- Settings → Privacy → Manage Permissions
- Toggle scanning, advertising, relay individually
- Revoke permissions (reduces functionality)

### Android System Settings
- Settings → Apps → EmergencyMesh → Permissions
- Grant/revoke any permission
- View permission usage history

### Persistent Notification (When Active)
- Shows when relay service is running
- Tap to open app
- Shows peer count and status
- Can disable relay from notification

## Testing Permissions

### Manual Test Cases

1. **Fresh Install**
   - Install app, launch
   - Verify permission requests appear
   - Grant all, verify functionality works

2. **Deny Individual Permissions**
   - Deny Bluetooth → Verify error message and disabled features
   - Deny Location (Android < 12) → Verify scanning disabled
   - Deny Notifications → Verify no alerts but core functionality works

3. **Revoke During Use**
   - Grant all, start using app
   - Go to System Settings → Revoke Bluetooth
   - Return to app → Verify graceful degradation

4. **Background Behavior**
   - Enable relay mode
   - Background app
   - Verify notification shows
   - Verify BLE operations continue

### Automated Tests

```kotlin
@Test
fun verifyPermissionRequests() {
    // Launch app for first time
    val scenario = launch(OnboardingActivity::class.java)
    
    // Verify permission rationale shown
    onView(withText("EmergencyMesh needs Bluetooth")).check(matches(isDisplayed()))
    
    // Tap grant button
    onView(withId(R.id.grantPermissionsButton)).perform(click())
    
    // Verify system permission dialog (UI Automator)
    val allowButton = device.findObject(UiSelector().text("Allow"))
    assertTrue(allowButton.exists())
}

@Test
fun verifyBluetoothPermissionGranted() {
    // Verify we can scan if permission granted
    assumeTrue(hasBluetoothPermissions())
    
    val bleManager = BleManager(context)
    bleManager.startScanning()
    
    // Should not throw SecurityException
}
```

## FAQ

**Q: Why does EmergencyMesh need location permission?**
A: On Android 11 and below, Google requires location permission for BLE scanning. EmergencyMesh never accesses GPS or location services. On Android 12+, this permission is not required.

**Q: Can I use EmergencyMesh without granting all permissions?**
A: Bluetooth permissions are essential. Location (on Android < 12) is also required for discovery. Notifications are optional but recommended.

**Q: Will EmergencyMesh work with location services disabled?**
A: Yes. EmergencyMesh requests permission but doesn't use location services. Disabling GPS has no impact.

**Q: Can I revoke permissions after granting them?**
A: Yes, via Android Settings → Apps → EmergencyMesh → Permissions. Some features will be disabled.

**Q: Does the app use permissions when not in use?**
A: Only if relay mode is enabled. Otherwise, BLE operations only happen when app is in foreground.

**Q: How can I verify EmergencyMesh isn't accessing location?**
A: Check Android's Privacy Dashboard (Android 12+): Settings → Privacy → Permission usage. EmergencyMesh should not appear under Location.

## Compliance

EmergencyMesh permissions comply with:
- Android 12+ Bluetooth permissions best practices
- Google Play Store policies
- GDPR privacy requirements
- Android Permission Best Practices guide

## Updates

This document is updated when:
- New Android versions require permission changes
- New features require additional permissions
- Privacy policies change
- User feedback indicates confusion

Last updated: 2026-01-29 (v0.1.0)
