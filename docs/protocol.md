# EmergencyMesh Protocol Specification

## Overview

The EmergencyMesh protocol defines the message format, routing rules, and communication patterns for peer-to-peer emergency messaging over Bluetooth Low Energy (BLE).

## Protocol Version

**Current Version**: 1.0
**Status**: Draft

## Message Format

### Message Structure

Messages are transmitted as JSON objects with the following structure:

```json
{
  "version": 1,
  "messageId": "sha256-hash-of-content",
  "senderId": "uuid-v4-device-id",
  "senderNickname": "User Display Name",
  "content": "The actual message text",
  "timestamp": 1706529600000,
  "ttl": 5,
  "groupId": "uuid-v4-group-id",
  "signature": "optional-future-field"
}
```

### Field Specifications

| Field | Type | Required | Max Size | Description |
|-------|------|----------|----------|-------------|
| `version` | Integer | Yes | 4 bytes | Protocol version number (currently 1) |
| `messageId` | String | Yes | 64 bytes | SHA-256 hash of (senderId + content + timestamp) in hex format |
| `senderId` | String | Yes | 36 bytes | UUID v4 of the sending device |
| `senderNickname` | String | Yes | 20 bytes | UTF-8 encoded display name of sender |
| `content` | String | Yes | 512 bytes | UTF-8 encoded message content |
| `timestamp` | Long | Yes | 8 bytes | Unix timestamp in milliseconds when message was created |
| `ttl` | Integer | Yes | 4 bytes | Time-To-Live counter, decrements at each hop (1-10) |
| `groupId` | String | No | 36 bytes | UUID v4 of target group, null for broadcast |
| `signature` | String | No | 128 bytes | Future: Ed25519 signature for authentication |

### Message ID Generation

The message ID is calculated as:
```
messageId = SHA256(senderId + "|" + content + "|" + timestamp)
```

This ensures:
- **Uniqueness**: Same content from different senders has different IDs
- **Deterministic**: Same message generates same ID for deduplication
- **Integrity**: Content tampering changes the ID

Example (Kotlin):
```kotlin
fun generateMessageId(senderId: String, content: String, timestamp: Long): String {
    val data = "$senderId|$content|$timestamp"
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(data.toByteArray())
    return hash.joinToString("") { "%02x".format(it) }
}
```

### Message Size Limits

- **Maximum Total Size**: 768 bytes (JSON encoded)
- **Maximum Content**: 512 bytes UTF-8
- **Rationale**: Fits within BLE MTU limits with overhead

Implementations **MUST**:
- Reject messages exceeding size limits
- Truncate nicknames to 20 bytes if needed
- Validate UTF-8 encoding

## Time-To-Live (TTL)

### Purpose

TTL prevents infinite message loops and limits network flooding.

### Rules

1. **Initial Value**: Sender sets TTL between 1-10 (default: 5)
2. **Decrement**: Each relay decrements TTL by 1 before forwarding
3. **Stop Condition**: Messages with TTL = 0 are **NOT** relayed
4. **Minimum**: TTL cannot be less than 0

### TTL Values

| TTL | Expected Reach | Use Case |
|-----|----------------|----------|
| 1 | Direct neighbors only | Local announcements |
| 3 | ~75 meter radius | Building-wide |
| 5 | ~200 meter radius | Campus-wide (default) |
| 10 | ~500+ meter radius | Emergency broadcast |

**Assumptions**: ~30-40 meter BLE range per hop

### Implementation

```kotlin
fun relayMessage(message: Message) {
    if (message.ttl <= 0) {
        // Do not relay
        return
    }
    
    val relayedMessage = message.copy(
        ttl = message.ttl - 1
    )
    
    sendToPeers(relayedMessage)
}
```

## Message Deduplication

### Problem

In mesh networks, a device may receive the same message multiple times via different paths.

### Solution

Each device maintains a **Seen Message IDs** cache.

### Algorithm

1. When a message is received:
   ```kotlin
   if (seenMessageIds.contains(message.messageId)) {
       // Already processed, drop silently
       return
   }
   ```

2. Add to seen cache:
   ```kotlin
   seenMessageIds.add(message.messageId, currentTimestamp)
   ```

3. Process message (display, relay, etc.)

### Cache Management

- **Retention**: 24 hours
- **Cleanup**: Periodic job removes entries older than 24 hours
- **Storage**: In-memory Set + Room database for persistence
- **Size**: Typically < 10,000 entries (assuming 100 msg/hour)

### Edge Cases

- **Clock Skew**: Devices may have different system times; use TTL, not timestamp, for freshness
- **Message Replay**: Old messages seen again after cache expiry will be processed (acceptable)
- **Cache Full**: Implement LRU eviction if memory constrained

## Routing Rules

### Basic Routing

1. **Receive Message**
   - Validate format and fields
   - Check deduplication (seen IDs)
   - Verify TTL > 0

2. **Local Delivery**
   - If no groupId OR device in group → display to user
   - Store in local database

3. **Relay Decision**
   - If relay mode enabled AND TTL > 0 → relay
   - Decrement TTL
   - Forward to connected peers (except sender)

### Group Filtering

Messages with a `groupId` are only delivered/relayed to devices in that group:

```kotlin
fun shouldRelayMessage(message: Message, peer: Peer): Boolean {
    if (message.groupId == null) {
        return true // Broadcast to all
    }
    
    return peer.groupId == message.groupId
}
```

### Anti-Loop Protection

1. **Sender Exclusion**: Never send a message back to the peer who sent it to you
2. **Message ID**: Deduplication prevents processing same message twice
3. **TTL**: Limits maximum hops

### Routing Example

```
Device A (TTL=3) → Device B → Device C → Device D → Device E (TTL=0, stop)
                      ↓
                   Device F (also receives from B)
```

1. A sends with TTL=3
2. B receives, stores, relays with TTL=2 to C and F
3. C receives from B, relays with TTL=1 to D
4. F receives from B, relays with TTL=1 to (other peers)
5. D receives from C, relays with TTL=0 to E
6. E receives from D, stores, but does NOT relay (TTL=0)

## BLE Transport

### Service UUID

EmergencyMesh uses a custom GATT service:
```
Service UUID: 0000feed-0000-1000-8000-00805f9b34fb
```

### Characteristics

**Message Characteristic** (Write, Notify)
- UUID: `0000beef-0000-1000-8000-00805f9b34fb`
- Purpose: Receive messages from peers
- Write: Peer writes message JSON
- Notify: Device notifies when new message available

**Device Info Characteristic** (Read)
- UUID: `0000dead-0000-1000-8000-00805f9b34fb`
- Purpose: Peer discovery and identification
- Read: Returns JSON with `{ "deviceId", "nickname", "groupId" }`

### Advertisement Data

BLE advertisement packet contains:
- **Service UUID**: Identifies EmergencyMesh devices
- **Local Name**: Device nickname (optional, space permitting)
- **Manufacturer Data**: Reserved for future use

### Connection Lifecycle

1. **Discovery**: Scanner finds device with EmergencyMesh service UUID
2. **Connect**: GATT client connects to GATT server
3. **Read Info**: Client reads device info characteristic
4. **Exchange Messages**: Both devices can write to each other's message characteristic
5. **Disconnect**: After idle timeout (30 seconds) or manual disconnect

### Transmission

1. Serialize message to JSON
2. Compress with GZIP if > 256 bytes (optional)
3. Write to peer's message characteristic
4. Handle write response (success/failure)
5. Retry once on failure, then move to next peer

### Error Handling

- **Connection Timeout**: 10 seconds
- **Write Timeout**: 5 seconds
- **Retry Strategy**: 1 retry with exponential backoff
- **Failed Peer**: Mark as disconnected, remove from active peers

## Rate Limiting

### Per-Peer Limits

- **Maximum**: 10 messages per minute per peer
- **Enforcement**: Track last 60 seconds of messages
- **Violation**: Disconnect peer, block for 5 minutes

### Device Limits

- **Maximum Outgoing**: 20 messages per minute
- **Maximum Relay**: 50 messages per minute
- **Purpose**: Prevent spam and battery drain

### Implementation

```kotlin
class RateLimiter {
    private val peerMessageTimestamps = mutableMapOf<String, MutableList<Long>>()
    
    fun allowMessage(peerId: String): Boolean {
        val now = System.currentTimeMillis()
        val timestamps = peerMessageTimestamps.getOrPut(peerId) { mutableListOf() }
        
        // Remove timestamps older than 1 minute
        timestamps.removeAll { now - it > 60_000 }
        
        return if (timestamps.size < 10) {
            timestamps.add(now)
            true
        } else {
            false // Rate limit exceeded
        }
    }
}
```

## Security Considerations

### Current (v1.0)

- **No Encryption**: Messages transmitted in plaintext
- **No Authentication**: Cannot verify sender identity
- **Integrity**: Message ID provides basic integrity check

### Future (v2.0)

- **E2E Encryption**: Signal Protocol or similar
- **Signatures**: Ed25519 for message authentication
- **Key Exchange**: Out-of-band via QR code

## Protocol Extensions

The protocol is designed for future extensions:

### Version Negotiation

Devices advertise supported protocol version. If mismatch:
- Use lowest common version
- Or reject connection if versions incompatible

### Custom Fields

Future versions may add optional fields:
- `priority`: Message priority (0-10)
- `replyTo`: Message ID being replied to
- `mediaType`: For future media support
- `encrypted`: Flag indicating encryption used

Implementations **MUST** ignore unknown fields to maintain forward compatibility.

## Compliance Checklist

Implementations **MUST**:
- ✅ Generate valid message IDs using SHA-256
- ✅ Enforce TTL rules (decrement, stop at 0)
- ✅ Implement deduplication with 24-hour cache
- ✅ Respect message size limits (512 bytes content)
- ✅ Filter messages by group ID
- ✅ Implement rate limiting (10 msg/min per peer)
- ✅ Use correct service UUID for BLE

Implementations **SHOULD**:
- ✅ Support TTL range 1-10
- ✅ Default TTL to 5
- ✅ Implement connection timeouts
- ✅ Handle malformed messages gracefully
- ✅ Log protocol errors for debugging

## References

- Bluetooth Core Specification 5.2
- [ARCHITECTURE.md](../ARCHITECTURE.md) - System architecture
- [SECURITY.md](../SECURITY.md) - Security considerations
- [test-plan.md](test-plan.md) - Protocol testing procedures
