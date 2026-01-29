# EmergencyMesh Test Plan

This document provides a comprehensive manual testing checklist for EmergencyMesh. Use this before releases and when validating significant changes.

## Test Environment Setup

### Requirements
- **Devices**: Minimum 3 Android devices with BLE support
- **Android Versions**: Test on API 23, 28, 31, and 33+ (if available)
- **Device Types**: Mix of phones and tablets
- **Conditions**: Indoor and outdoor environments

### Pre-Test Setup
- [ ] Build debug APK: `./gradlew assembleDebug`
- [ ] Install on all test devices
- [ ] Enable Developer Options on all devices
- [ ] Disable battery optimization for EmergencyMesh
- [ ] Ensure devices have sufficient battery (>50%)
- [ ] Clear app data before major test runs

## Phase 1: Installation and Permissions

### Fresh Installation
- [ ] Install APK on Device A
- [ ] Launch app
- [ ] Verify onboarding screen appears
- [ ] Verify app requests Bluetooth permissions
- [ ] Grant all permissions
- [ ] Verify app reaches home screen
- [ ] Check no crashes in logcat

### Permission Scenarios

#### All Permissions Granted
- [ ] All features accessible
- [ ] Scanning works
- [ ] Relay toggle available

#### Bluetooth Permission Denied (Android 12+)
- [ ] App shows permission rationale
- [ ] Scanning disabled
- [ ] Error message shown: "Bluetooth permission required"
- [ ] "Grant Permissions" button available
- [ ] Clicking button re-requests permissions

#### Location Permission Denied (Android < 12)
- [ ] App shows permission rationale for older Android
- [ ] Scanning disabled
- [ ] Error message clear and helpful

#### Permissions Revoked During Use
- [ ] Launch app with permissions
- [ ] Enable scanning
- [ ] Go to Settings → Revoke Bluetooth permission
- [ ] Return to app
- [ ] Verify graceful degradation (no crash)
- [ ] Verify error message displayed

## Phase 2: Onboarding Flow

### First Launch
- [ ] Onboarding screen displays correctly
- [ ] Explanation text is clear and helpful
- [ ] Permission rationale is shown
- [ ] "Get Started" button works

### Nickname Setup
- [ ] Can enter nickname (1-20 characters)
- [ ] Validation: Empty nickname rejected
- [ ] Validation: Nickname > 20 chars truncated
- [ ] Nickname saves successfully
- [ ] Special characters handled (emoji, unicode)

### Group Setup
- [ ] "Create Group" option shown
- [ ] "Join Group" option shown
- [ ] "Skip" option available
- [ ] Can create group with name
- [ ] Group ID generated and saved
- [ ] Can manually enter group ID to join
- [ ] Invalid group ID rejected

### Navigation
- [ ] Can complete onboarding
- [ ] Redirected to home screen
- [ ] Onboarding doesn't show on subsequent launches
- [ ] Can reset from Settings to see onboarding again

## Phase 3: Home Screen

### UI Elements
- [ ] Relay toggle switch present
- [ ] Peer count display shows "0 peers"
- [ ] Bluetooth status indicator present
- [ ] Message feed area visible
- [ ] Settings button accessible
- [ ] All UI elements properly aligned

### Bluetooth Status Indicator
- [ ] Shows "Bluetooth Off" when BT disabled
- [ ] Shows "Bluetooth On" when BT enabled
- [ ] Shows "Scanning..." when actively scanning
- [ ] Updates in real-time
- [ ] Color coding correct (red/yellow/green)

### Relay Toggle
- [ ] Toggle switch works
- [ ] Enabling relay starts foreground service
- [ ] Persistent notification appears when relay enabled
- [ ] Disabling relay stops service
- [ ] Notification dismissed when relay disabled
- [ ] State persists across app restarts

### Peer Count
- [ ] Shows "0 peers" when none connected
- [ ] Updates when peer discovered
- [ ] Shows "1 peer" or "X peers" correctly
- [ ] Decrements when peer disconnects
- [ ] Tapping shows peer list (future)

### Message Feed
- [ ] Shows "No messages" when empty
- [ ] Displays local messages
- [ ] Displays received messages
- [ ] Shows sender nickname
- [ ] Shows timestamp
- [ ] Auto-scrolls to latest message

## Phase 4: BLE Discovery (Multi-Device)

### Basic Discovery (2 Devices)
- [ ] Device A: Enable relay mode
- [ ] Device B: Enable relay mode
- [ ] Wait 10 seconds
- [ ] Verify both devices see peer count = 1
- [ ] Check logcat for discovery events
- [ ] Verify no crashes or errors

### Three-Device Mesh
- [ ] Device A: Enable relay
- [ ] Device B: Enable relay
- [ ] Device C: Enable relay
- [ ] Verify each device shows 2 peers
- [ ] Move Device C out of range of A (but in range of B)
- [ ] Verify Device A peer count drops to 1
- [ ] Move Device C back in range
- [ ] Verify Device A peer count returns to 2

### Connection Stability
- [ ] Keep 2 devices connected for 5 minutes
- [ ] Verify connection maintained
- [ ] Check for memory leaks (Android Studio Profiler)
- [ ] Battery usage reasonable (<10%/hour)

### Disconnection Scenarios
- [ ] Device A connected to Device B
- [ ] Turn off Bluetooth on Device B
- [ ] Verify Device A detects disconnection
- [ ] Verify peer count updates
- [ ] Turn Bluetooth back on
- [ ] Verify automatic reconnection (within 30 seconds)

### Background Discovery
- [ ] Device A: Enable relay, home button to background
- [ ] Device B: Enable relay
- [ ] Verify Device A discovers Device B while backgrounded
- [ ] Verify notification updates with peer count

## Phase 5: Messaging

### Send Message (Direct)
- [ ] Device A and B connected
- [ ] Device A: Type message "Hello from A"
- [ ] Send message
- [ ] Verify message appears in Device A feed
- [ ] Wait 2-3 seconds
- [ ] Verify message appears on Device B
- [ ] Check sender nickname correct

### Message Metadata
- [ ] Timestamp shown and correct
- [ ] Sender nickname displayed
- [ ] Message ID generated (check logs)
- [ ] Group ID included if in group

### Message Relay (3 Devices)
- [ ] Device A ← → Device B ← → Device C (A and C out of range)
- [ ] Device A: Send message "Relay test"
- [ ] Wait 5 seconds
- [ ] Verify Device B receives and relays
- [ ] Verify Device C receives relayed message
- [ ] Verify message only appears once on each device

### TTL Behavior
- [ ] Device A: Send with TTL=1
- [ ] Verify Device B receives (1 hop)
- [ ] Verify Device C does NOT receive (would be 2 hops)
- [ ] Device A: Send with TTL=5
- [ ] Verify Device C receives via relay

### Deduplication
- [ ] Setup: A ← → B ← → C ← → A (circular topology)
- [ ] Device A: Send message
- [ ] Verify message appears only once on A, B, C
- [ ] Check logs for deduplication events
- [ ] No infinite loops

### Message Size Limits
- [ ] Send message with 512 characters (max size)
- [ ] Verify sends successfully
- [ ] Try 513+ characters
- [ ] Verify UI prevents or truncates
- [ ] Verify error message if rejected

### Rate Limiting
- [ ] Send 10 messages rapidly
- [ ] Verify all sent
- [ ] Try 11th message immediately
- [ ] Verify rate limit prevents or delays
- [ ] Wait 60 seconds
- [ ] Verify can send again

## Phase 6: Group Functionality

### Group Creation
- [ ] Device A: Create group "Emergency"
- [ ] Verify group ID generated
- [ ] Verify group name saved
- [ ] Share group ID via QR/text

### Group Joining
- [ ] Device B: Join group with ID from A
- [ ] Verify group name synced (if possible)
- [ ] Verify both devices in same group

### Group Filtering
- [ ] Device A, B in "Group1"
- [ ] Device C not in any group
- [ ] Device A: Send message to Group1
- [ ] Verify Device B receives
- [ ] Verify Device C does NOT receive
- [ ] Device C: Send broadcast message
- [ ] Verify A and B both receive

### Multiple Groups
- [ ] Device A in "Group1"
- [ ] Device B in "Group2"
- [ ] Device A: Send to Group1
- [ ] Verify Device B doesn't receive
- [ ] Both devices: Leave and join common group
- [ ] Verify messages now shared

## Phase 7: Settings Screen

### UI
- [ ] Access settings from home screen
- [ ] All settings visible
- [ ] Back navigation works

### Nickname Change
- [ ] Change nickname
- [ ] Save
- [ ] Verify new nickname used in messages
- [ ] Verify nickname persists after restart

### Data Retention
- [ ] Change message retention (1, 7, 30 days)
- [ ] Save
- [ ] Verify old messages deleted per policy
- [ ] Create test message and fast-forward time (emulator)

### Privacy Settings
- [ ] "Delete All Messages" button
- [ ] Confirm dialog appears
- [ ] Delete messages
- [ ] Verify message feed cleared
- [ ] Verify database empty

### Scanning Frequency
- [ ] Change scan interval (10s, 30s, 60s)
- [ ] Verify scan rate changes (check logcat)
- [ ] Verify battery impact

### About Section
- [ ] App version displayed correctly
- [ ] Links to documentation work
- [ ] Licenses/credits shown

## Phase 8: Background and Lifecycle

### App Backgrounding
- [ ] Enable relay
- [ ] Press home button
- [ ] Verify foreground service continues
- [ ] Send message from another device
- [ ] Verify message received while backgrounded
- [ ] Return to app
- [ ] Verify message displayed

### App Killing
- [ ] Enable relay
- [ ] Force stop app (Settings → Apps)
- [ ] Verify service stopped
- [ ] Restart app
- [ ] Verify relay state restored

### Device Reboot
- [ ] Enable relay
- [ ] Reboot device
- [ ] Launch app
- [ ] Verify settings restored
- [ ] Verify relay state remembered

### Low Battery
- [ ] Enable relay
- [ ] Drain battery to <15%
- [ ] Verify app still functions
- [ ] Check if Android battery saver affects relay

### Airplane Mode
- [ ] Enable relay
- [ ] Turn on airplane mode
- [ ] Verify Bluetooth still works (if user enables it)
- [ ] Turn off airplane mode
- [ ] Verify normal operation resumes

## Phase 9: Error Handling

### Bluetooth Errors
- [ ] Turn off Bluetooth system-wide
- [ ] Verify app shows error message
- [ ] Verify prompts user to enable BT
- [ ] Turn on Bluetooth
- [ ] Verify app recovers

### No Peers Available
- [ ] Device alone
- [ ] Try to send message
- [ ] Verify message queued or error shown
- [ ] Connect to peer
- [ ] Verify queued message sends

### Database Errors
- [ ] Fill database to near capacity
- [ ] Verify old messages pruned
- [ ] Verify no crashes

### Malformed Messages
- [ ] Use developer tool to send invalid JSON
- [ ] Verify app doesn't crash
- [ ] Verify error logged
- [ ] Verify message ignored

## Phase 10: Performance

### Memory
- [ ] Connect to 5 peers
- [ ] Send 100 messages
- [ ] Check memory usage (Android Studio Profiler)
- [ ] Verify no leaks
- [ ] Verify < 100MB used

### Battery
- [ ] Fully charge device
- [ ] Enable relay mode
- [ ] Let run for 1 hour
- [ ] Check battery usage (Settings → Battery)
- [ ] Verify < 5% drain per hour

### Message Latency
- [ ] Device A and B connected
- [ ] Device A: Send message
- [ ] Measure time until received on B
- [ ] Target: < 2 seconds for direct connection
- [ ] Device A → B → C: < 5 seconds for relay

### Scan Performance
- [ ] Enable scanning
- [ ] Measure CPU usage
- [ ] Verify < 10% CPU on average
- [ ] Check for battery drain

## Phase 11: Stress Testing

### Many Peers
- [ ] Connect to 10+ devices (if available)
- [ ] Verify app stable
- [ ] Verify UI responsive
- [ ] Check connection limits enforced

### Message Flood
- [ ] Setup 3 devices
- [ ] All devices send messages rapidly
- [ ] Verify rate limiting works
- [ ] Verify no crashes
- [ ] Verify UI remains responsive

### Long Duration
- [ ] Enable relay
- [ ] Leave running overnight (8+ hours)
- [ ] Check for crashes
- [ ] Check battery usage
- [ ] Check memory leaks

### Rapid Connections
- [ ] Connect and disconnect 10 peers rapidly
- [ ] Verify no crashes
- [ ] Verify connections cleaned up
- [ ] Check for resource leaks

## Phase 12: Device Compatibility

### Android Versions
- [ ] Test on Android 6 (API 23)
- [ ] Test on Android 9 (API 28)
- [ ] Test on Android 12 (API 31)
- [ ] Test on Android 13+ (API 33+)
- [ ] Verify features work on all versions

### Device Types
- [ ] Phone (small screen)
- [ ] Phablet
- [ ] Tablet
- [ ] Foldable (if available)
- [ ] Verify UI scales appropriately

### Manufacturers
- [ ] Google Pixel
- [ ] Samsung Galaxy
- [ ] Motorola/OnePlus/Xiaomi
- [ ] Verify BLE implementations compatible

## Phase 13: Accessibility

### Screen Readers
- [ ] Enable TalkBack
- [ ] Navigate through app
- [ ] Verify all buttons labeled
- [ ] Verify content descriptions present

### Font Sizes
- [ ] Change system font to largest
- [ ] Verify UI doesn't break
- [ ] Verify text remains readable

### High Contrast
- [ ] Enable high contrast mode
- [ ] Verify all text readable
- [ ] Verify buttons distinguishable

## Pass/Fail Criteria

### Critical Issues (Must Fix Before Release)
- App crashes on launch
- Cannot send/receive messages
- BLE permissions not working
- Data loss or corruption
- Security vulnerabilities

### Major Issues (Should Fix Before Release)
- UI broken on common devices
- Poor battery life (>10%/hour)
- Messages delayed >10 seconds
- Background relay not working
- Memory leaks

### Minor Issues (Can Fix in Next Release)
- UI alignment issues
- Non-critical error messages
- Performance optimizations
- Feature requests

## Test Results Template

```
Test Date: YYYY-MM-DD
Tester: [Name]
Build: [Version/Commit]
Device: [Model/Android Version]

Phase 1: Installation - PASS/FAIL
Phase 2: Onboarding - PASS/FAIL
Phase 3: Home Screen - PASS/FAIL
...

Critical Issues Found: [Count]
Major Issues Found: [Count]
Minor Issues Found: [Count]

Notes:
- [Any observations]
- [Issues discovered]
- [Suggestions]

Overall Result: PASS/FAIL
```

## Automated Testing

In addition to manual tests:
- [ ] Run unit tests: `./gradlew test`
- [ ] Run instrumented tests: `./gradlew connectedCheck`
- [ ] Run UI tests: `./gradlew connectedAndroidTest`
- [ ] Verify >70% code coverage

## Sign-Off

Before release, the following must sign off:
- [ ] Developer (implemented and tested)
- [ ] QA/Tester (manual tests passed)
- [ ] Security review (no vulnerabilities)
- [ ] Documentation updated

---

**Last Updated**: 2026-01-29 (v0.1.0)
**Next Review**: Before each release
