# Privacy Policy

## Overview

EmergencyMesh is designed with privacy as a core principle. This document explains what data is collected, how it's stored, and who can access it.

## Core Privacy Principles

1. **Data Minimization**: Collect only essential data for functionality
2. **Local Storage**: All data stored on device, not in cloud
3. **User Control**: Users control data retention and deletion
4. **No Central Server**: No centralized database or analytics
5. **Open Source**: Code is transparent and auditable

## Data Collection

### Data We Collect

EmergencyMesh collects and stores the following data **locally on your device only**:

#### User-Provided Data
- **Nickname**: User-chosen display name (required)
- **Group IDs**: Groups you create or join (optional)
- **Messages**: Text content you send and receive
- **Settings**: Your preferences (relay enabled, scan frequency, etc.)

#### Automatically Generated Data
- **Device ID**: Random UUID generated on first launch
- **Message IDs**: SHA-256 hash of message content for deduplication
- **Peer Information**: 
  - Device IDs of discovered peers
  - Nicknames of connected peers
  - Last seen timestamps
  - Connection status
- **Message Metadata**:
  - Sender device ID and nickname
  - Timestamp
  - TTL (Time To Live)
  - Group ID (if applicable)

### Data We Do NOT Collect

- ❌ Real names or personal identifiers
- ❌ Phone numbers or email addresses
- ❌ Location or GPS coordinates
- ❌ Device serial numbers or IMEI
- ❌ Contacts or address books
- ❌ Photos or files (text messages only)
- ❌ Usage analytics or telemetry
- ❌ Advertising identifiers
- ❌ Social media connections

## Data Storage

### On-Device Storage

All data is stored locally on your Android device using:

1. **Room Database** (SQLite)
   - Messages (up to 1000 recent messages)
   - Peers (up to 100 recent peers)
   - Seen message IDs (rolling 24-hour window)

2. **SharedPreferences**
   - User nickname
   - Settings and preferences
   - Device ID

3. **Memory (Runtime Only)**
   - Active BLE connections
   - Current peer states
   - Processing queues

**Storage Location**: `/data/data/com.emergencymesh.app/`
- Only accessible by EmergencyMesh app
- Protected by Android app sandboxing
- Deleted when app is uninstalled

### Off-Device Storage

**EmergencyMesh does NOT store any data off-device:**

- ❌ No cloud backup or sync
- ❌ No remote servers or databases
- ❌ No third-party analytics services
- ❌ No advertising networks
- ❌ No crash reporting services

**Exception**: If you manually enable Android device backup, your settings may be backed up to your Google account. This is controlled by Android system settings, not EmergencyMesh.

## Data Retention

### Automatic Retention Policies

| Data Type | Retention Period | Reason |
|-----------|-----------------|---------|
| Messages | 7 days (default, configurable) | Balance utility and privacy |
| Peers | Until 24 hours after last seen | Maintain recent network topology |
| Seen Message IDs | 24 hours | Prevent duplicate message processing |
| Settings | Until app uninstall | User preferences |

### User Control

Users can control data retention:

1. **Message Retention**: Configure in Settings (1-30 days, or unlimited)
2. **Clear Messages**: Manual "Delete All Messages" button
3. **Clear Peers**: Manual "Clear Peer History" button
4. **Reset App**: "Reset to Defaults" deletes all local data

### Complete Data Deletion

To completely remove all data:
1. Go to Settings → Privacy → "Delete All Data"
2. Or uninstall the app (Android will delete all app data)

## Data Sharing

### With Other EmergencyMesh Users

When you use EmergencyMesh, the following is shared with nearby peers:

**Broadcast to All Nearby Devices** (via BLE advertising):
- EmergencyMesh service UUID (identifies you're running the app)
- Your nickname
- Your group ID (if in a group)

**Sent to Connected Peers** (when sending/relaying messages):
- Message content
- Your device ID and nickname
- Timestamp
- Message TTL
- Group ID (if applicable)

**Relay Nodes Can See**:
- Message metadata (sender, timestamp, size, TTL)
- ⚠️ In v0.1.0, relay nodes can also read message content (no encryption yet)

### With Third Parties

EmergencyMesh does **NOT** share data with third parties:
- ❌ No analytics companies
- ❌ No advertising networks
- ❌ No data brokers
- ❌ No government agencies (unless legally compelled)
- ❌ No other apps on your device

### Legal Disclosure

EmergencyMesh has no servers and collects no user data centrally, so there is nothing to disclose in response to legal requests. However:

- Data on your device may be accessible through Android backups or device forensics
- Messages you send are visible to recipients and relay nodes
- BLE traffic may be observable by nearby scanners

## Privacy Considerations

### Metadata Exposure

Even without reading message content, observers can deduce:

- **Who**: Device IDs and nicknames are visible
- **When**: Timestamps show when messages were sent
- **Network Topology**: Connection patterns reveal mesh structure
- **Activity Patterns**: Frequency and timing of messages

**Mitigation**: Future versions will implement:
- Rotating device identifiers (v0.3.0)
- Traffic padding and timing obfuscation (v0.4.0)

### BLE Privacy

Bluetooth Low Energy broadcasts are:
- Visible to all nearby BLE scanners
- Not encrypted at the BLE layer
- Can be used for proximity tracking

**Mitigation**:
- Device ID is randomized, not tied to hardware
- BLE MAC address randomization (Android 8.0+)
- Option to disable advertising when not actively using app (Settings)

### Message Privacy

Current version (v0.1.0):
- ⚠️ Messages are **NOT encrypted**
- ⚠️ Any peer in the mesh can read your messages
- ⚠️ Relay nodes can inspect message content

**Recommendation**: Assume all messages are public. Don't share:
- Personal identifiable information (PII)
- Passwords or credentials
- Sensitive locations
- Private conversations

**Future Enhancement**: End-to-end encryption (v0.2.0) will ensure only intended recipients can read messages.

## User Rights

You have the right to:

1. **Access**: View all data EmergencyMesh stores (via Settings → Privacy → View Stored Data)
2. **Modify**: Change your nickname and preferences anytime
3. **Delete**: Remove messages, peers, or all data at any time
4. **Export**: (v0.3.0) Export your message history in JSON format
5. **Portability**: Data stays on your device; take it with you

## Children's Privacy

EmergencyMesh is not directed at children under 13. We do not knowingly collect data from children. If a parent/guardian believes their child has used EmergencyMesh, please contact us to delete any data.

## Changes to Privacy Policy

This privacy policy may be updated to reflect new features or regulations. Changes will be:
- Documented in [CHANGELOG.md](CHANGELOG.md)
- Shown in-app on first launch after update
- Published on GitHub with version history

## Data Breach Response

If a vulnerability is discovered that exposes user data:
1. Fix will be released within 7 days
2. Users will be notified via in-app notification
3. Disclosure will be posted on GitHub
4. Affected data types and timeframe will be specified

## Privacy by Design

EmergencyMesh architecture prioritizes privacy:

- **Decentralized**: No central server to breach or subpoena
- **Local First**: All data processing on-device
- **Minimal Collection**: Only collect what's necessary
- **User Control**: Settings for retention and deletion
- **Open Source**: Transparent, auditable code
- **No Tracking**: No analytics, no ads, no third parties

## Contact

For privacy concerns or questions:
- Open an issue on GitHub: [github.com/markvanengelen-gulo/emergencymesh](https://github.com/markvanengelen-gulo/emergencymesh)
- Email: privacy@emergencymesh.io (when available)

## Compliance

EmergencyMesh aims to comply with:
- GDPR (General Data Protection Regulation)
- CCPA (California Consumer Privacy Act)
- Android Privacy Best Practices
- Google Play Store Privacy Requirements

## Last Updated

This privacy policy was last updated on January 29, 2026 for version 0.1.0.
