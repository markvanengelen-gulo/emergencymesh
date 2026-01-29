# EmergencyMesh Architecture

This document describes the technical architecture of EmergencyMesh, including modules, protocols, and data flow.

## System Overview

EmergencyMesh is a decentralized peer-to-peer messaging application that operates over Bluetooth Low Energy (BLE). The system consists of an Android application with the following key components:

```
┌─────────────────────────────────────────┐
│         Android Application             │
├─────────────────────────────────────────┤
│  UI Layer (Jetpack Compose)             │
│  ├─ Onboarding Screen                   │
│  ├─ Home Screen                         │
│  └─ Settings Screen                     │
├─────────────────────────────────────────┤
│  Application Layer                      │
│  ├─ Message Manager                     │
│  ├─ Peer Manager                        │
│  ├─ Relay Service                       │
│  └─ Group Manager                       │
├─────────────────────────────────────────┤
│  BLE Layer                               │
│  ├─ Scanner (Discovery)                 │
│  ├─ Advertiser (Broadcast)              │
│  ├─ GATT Server (Message Receiver)      │
│  └─ GATT Client (Message Sender)        │
├─────────────────────────────────────────┤
│  Data Layer (Room Database)             │
│  ├─ Peers Table                         │
│  ├─ Messages Table                      │
│  └─ SeenMessageIds Table                │
└─────────────────────────────────────────┘
```

## Modules

### 1. UI Module

Built with Jetpack Compose for modern, reactive UI development.

**Screens:**
- **OnboardingScreen**: First-run experience where users set their nickname and optionally create/join groups
- **HomeScreen**: Main interface showing relay status, connected peer count, Bluetooth status, and message feed
- **SettingsScreen**: Configuration for data retention, scanning frequency, relay behavior

**Key Components:**
- Reactive state management using Compose State
- ViewModel pattern for business logic separation
- Navigation component for screen transitions

### 2. Application Layer

**MessageManager:**
- Handles message creation, validation, and delivery
- Implements message TTL (Time To Live) logic
- Manages message deduplication using seen IDs
- Coordinates with encryption module for secure messaging

**PeerManager:**
- Tracks discovered and connected peers
- Maintains peer metadata (nickname, last seen, connection status)
- Handles peer timeouts and cleanup
- Updates UI with peer count and status

**RelayService:**
- Android foreground service for background operation
- Receives messages from connected peers
- Determines routing decisions based on TTL and group membership
- Forwards messages to appropriate peers

**GroupManager:**
- Manages group creation and membership
- Filters messages based on group IDs
- Handles group discovery announcements

### 3. BLE Communication Layer

The BLE layer implements a custom protocol for peer discovery and message exchange.

**Scanner:**
- Continuously scans for BLE devices advertising EmergencyMesh service UUID
- Filters by service UUID to avoid non-EmergencyMesh devices
- Reports discovered devices to PeerManager

**Advertiser:**
- Broadcasts BLE advertisement with EmergencyMesh service UUID
- Includes device nickname and group ID in advertisement data
- Makes device discoverable to other EmergencyMesh nodes

**GATT Server:**
- Implements custom GATT service with characteristics for:
  - Message receiving (write characteristic)
  - Peer information (read characteristic)
  - Device status (read characteristic)

**GATT Client:**
- Connects to peer GATT servers
- Writes messages to peer message characteristic
- Reads peer information for discovery

### 4. Data Layer (Room Database)

**Peers Table:**
```kotlin
@Entity(tableName = "peers")
data class Peer(
    @PrimaryKey val deviceId: String,
    val nickname: String,
    val lastSeen: Long,
    val connectionStatus: ConnectionStatus,
    val groupId: String?
)
```

**Messages Table:**
```kotlin
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val messageId: String,
    val senderId: String,
    val senderNickname: String,
    val content: String,
    val timestamp: Long,
    val groupId: String?,
    val ttl: Int,
    val isLocal: Boolean
)
```

**SeenMessageIds Table:**
```kotlin
@Entity(tableName = "seen_message_ids")
data class SeenMessageId(
    @PrimaryKey val messageId: String,
    val firstSeen: Long
)
```

## BLE Discovery Protocol

### Service UUID

EmergencyMesh uses a custom UUID for identification:
```
Service UUID: 0000feed-0000-1000-8000-00805f9b34fb
```

### Advertisement Format

BLE advertisements contain:
- Service UUID (identifies EmergencyMesh devices)
- Device nickname (up to 20 bytes, UTF-8 encoded)
- Group ID (optional, 16 bytes UUID)

### Connection Flow

1. Device A scans and discovers Device B's advertisement
2. Device A connects to Device B's GATT server
3. Device A subscribes to Device B's message characteristic
4. Device A writes pending messages to Device B
5. Device B receives and processes messages
6. Device B may relay messages to other peers
7. Connection maintained or terminated based on activity

## Data Flow

### Message Creation and Sending

```
User → UI → MessageManager → Message Creation
                            ↓
                    Add to local database
                            ↓
                    PeerManager (get connected peers)
                            ↓
                    BLE GATT Client → Write to peer(s)
```

### Message Reception and Relay

```
BLE GATT Server → Receive message → MessageManager
                                          ↓
                                    Validate message
                                          ↓
                                    Check deduplication
                                          ↓
                                    Add to seen IDs
                                          ↓
                                    Store in database (if for us)
                                          ↓
                                    Update UI
                                          ↓
                                    RelayService → Forward to peers (if TTL > 0)
```

### Peer Discovery

```
BLE Scanner → Device discovered → Parse advertisement
                                        ↓
                                  Extract nickname/group
                                        ↓
                                  PeerManager → Add/update peer
                                        ↓
                                  Update UI with peer count
                                        ↓
                                  Initiate GATT connection
```

## Message Routing Logic

1. **Local Messages**: Created by user, delivered to all connected peers
2. **Received Messages**: 
   - Check if already seen (deduplication)
   - If new and TTL > 0, relay to connected peers (except sender)
   - Decrement TTL before relaying
3. **Group Filtering**: Messages with group ID only relayed to peers in same group
4. **TTL Enforcement**: Messages with TTL = 0 not relayed further

## State Management

### Application State
- Maintained in ViewModels using StateFlow/LiveData
- Persisted to database for critical data (messages, peers)
- Shared preferences for user settings

### Connection State
- Tracked per peer in PeerManager
- States: Discovered, Connecting, Connected, Disconnected
- Automatic reconnection logic for temporary disconnections

### Relay State
- User-controlled toggle (on/off)
- Persisted across app restarts
- Affects whether messages are forwarded

## Threading Model

- **Main Thread**: UI updates and Compose recomposition
- **IO Thread**: Database operations (Room)
- **BLE Thread**: Bluetooth operations (Android BLE callbacks)
- **Background Thread**: Message processing and relay logic

Coroutines used for asynchronous operations with proper dispatchers:
- `Dispatchers.Main` for UI updates
- `Dispatchers.IO` for database and file operations
- `Dispatchers.Default` for computational work

## Security Considerations

- Message encryption (future enhancement)
- Peer authentication (future enhancement)
- Rate limiting to prevent spam
- Maximum message size enforcement
- TTL limits to prevent infinite propagation

See [SECURITY.md](SECURITY.md) for detailed security analysis.

## Performance Considerations

### Battery Optimization
- Configurable scan intervals
- Connection pooling to limit simultaneous connections
- Foreground service with battery-efficient settings
- Doze mode compatibility

### Memory Management
- Automatic cleanup of old seen message IDs
- Peer pruning based on last seen timestamp
- Message database size limits

### Network Efficiency
- Deduplication prevents redundant transmissions
- TTL prevents excessive hops
- Group filtering reduces unnecessary relays

## Future Enhancements

- End-to-end encryption for message content
- Digital signatures for message authentication
- Mesh topology awareness and optimization
- Multi-hop route discovery
- Priority-based message queuing
- Location-based services (optional)
