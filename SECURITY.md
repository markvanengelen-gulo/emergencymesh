# Security Policy

## Overview

EmergencyMesh is designed for emergency communication scenarios where traditional infrastructure is unavailable. Security is balanced with the need for rapid deployment and ease of use in crisis situations.

## Threat Model

### Assumptions

**In Scope:**
- Attackers within BLE range can observe traffic
- Malicious actors may attempt to join the mesh network
- Devices may be compromised or running modified software
- Message injection and replay attacks are possible
- Metadata leakage through BLE advertisements

**Out of Scope (Current Version):**
- Physical device compromise or theft
- Side-channel attacks (timing, power analysis)
- Attacks on the Android OS or BLE stack itself
- GPS/location spoofing

### Trust Model

- **No Central Authority**: The network is fully decentralized with no trusted coordinator
- **User Responsibility**: Users must verify peer identities through out-of-band means
- **Open Mesh**: Any device with EmergencyMesh can join the network
- **Relay Trust**: Relaying devices can observe message metadata (sender, timestamp, TTL)

## Security Features

### Current Implementation (v0.1.0)

#### 1. Message Deduplication
- **Protection**: Prevents message replay and infinite loops
- **Mechanism**: SHA-256 hash of message content used as unique ID
- **Storage**: Seen message IDs stored locally with timestamp
- **Cleanup**: Old message IDs pruned after 24 hours

#### 2. Time-To-Live (TTL)
- **Protection**: Limits message propagation and resource exhaustion
- **Mechanism**: Integer counter decremented at each hop
- **Default**: TTL = 5 (messages traverse maximum 5 hops)
- **Range**: 1-10 configurable by sender

#### 3. Message Size Limits
- **Protection**: Prevents memory exhaustion and DoS attacks
- **Limit**: 512 bytes per message payload
- **Enforcement**: Validated before acceptance and relay

#### 4. Rate Limiting
- **Protection**: Prevents message flooding and spam
- **Limit**: Maximum 10 messages per device per minute
- **Enforcement**: Tracked per peer, violations result in temporary block

#### 5. Group Isolation
- **Protection**: Messages only delivered within intended groups
- **Mechanism**: Group ID included in message header
- **Validation**: Relays only forward to peers in matching group

### Planned Security Enhancements (Future Versions)

#### 1. End-to-End Encryption (v0.2.0)
- **Algorithm**: Signal Protocol or similar
- **Key Exchange**: Out-of-band verification with QR codes
- **Forward Secrecy**: Ratcheting keys for each message
- **Group Encryption**: Sender keys for efficient group messaging

#### 2. Message Authentication (v0.2.0)
- **Signatures**: Ed25519 digital signatures for message origin verification
- **Identity Keys**: Long-term identity keys for peer authentication
- **Trust on First Use (TOFU)**: Accept and remember peer keys

#### 3. Secure Pairing (v0.3.0)
- **Mechanism**: Bluetooth LE Secure Connections
- **Verification**: Out-of-band numeric comparison or QR code scan
- **Protection**: Prevents MITM attacks during initial pairing

## Known Risks and Limitations

### 1. Metadata Leakage

**Risk**: BLE advertisements and message headers expose metadata
- Device IDs and nicknames are visible to nearby BLE scanners
- Message timing and frequency can be observed
- Network topology can be inferred from relay patterns

**Mitigation** (current):
- Minimal data in advertisements (service UUID, nickname only)
- No location or device info included

**Mitigation** (planned):
- Rotating device identifiers (v0.3.0)
- Traffic padding to obscure patterns (v0.4.0)

### 2. Message Content Exposure

**Risk**: Messages currently transmitted in plaintext
- Any peer or observer can read message content
- Relaying nodes can inspect and modify messages

**Mitigation** (current):
- ⚠️ None - users should assume messages are public

**Mitigation** (planned):
- End-to-end encryption (v0.2.0)
- Message integrity checks (v0.2.0)

### 3. Sybil Attacks

**Risk**: Attacker creates many fake identities to:
- Dominate the network
- Perform traffic analysis
- Execute denial of service

**Mitigation** (current):
- Connection limits per device (max 5 simultaneous)
- Rate limiting per peer

**Mitigation** (planned):
- Proof-of-work for joining (v0.3.0)
- Reputation system based on successful relays (v0.4.0)

### 4. Message Injection

**Risk**: Attackers can craft fake messages appearing from any sender

**Mitigation** (current):
- ⚠️ None - no authentication implemented

**Mitigation** (planned):
- Digital signatures (v0.2.0)
- Public key infrastructure (v0.2.0)

### 5. Denial of Service

**Risk**: Attackers flood network with messages

**Mitigation** (current):
- Message size limits (512 bytes)
- Rate limiting (10 msgs/min per peer)
- TTL limits propagation

**Mitigation** (planned):
- Enhanced rate limiting with backoff (v0.2.0)
- Message priority system (v0.3.0)

### 6. Device Tracking

**Risk**: Fixed device IDs allow tracking user movement

**Mitigation** (current):
- ⚠️ Device IDs are persistent within session

**Mitigation** (planned):
- Rotating identifiers (v0.3.0)
- Privacy modes (v0.3.0)

## Security Best Practices for Users

### During Use

1. **Assume Messages Are Public**: Until E2E encryption is implemented, all messages should be considered visible to anyone in the mesh
2. **Verify Identities**: Use out-of-band methods (voice, visual) to confirm peer identities
3. **Limit Sensitive Information**: Don't share passwords, locations, or PII
4. **Monitor Peer List**: Be aware of who is connected to your device
5. **Disable When Not Needed**: Turn off relay and scanning when not in emergency

### Device Security

1. **Keep Device Locked**: Use strong PIN/biometric lock
2. **Update Regularly**: Install EmergencyMesh updates promptly
3. **Secure Device**: Keep Android OS updated with security patches
4. **Review Permissions**: Regularly audit app permissions
5. **Uninstall If Compromised**: If device behavior is suspicious, uninstall and reset

## Reporting Security Issues

If you discover a security vulnerability in EmergencyMesh:

1. **Do Not** publicly disclose the issue
2. **Email** the details to: security@emergencymesh.io (when available)
3. **Include**:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

We aim to respond to security reports within 48 hours and provide fixes within 30 days.

## Security Roadmap

| Feature | Target Version | Priority |
|---------|---------------|----------|
| End-to-End Encryption | v0.2.0 | Critical |
| Message Signatures | v0.2.0 | Critical |
| Secure Pairing | v0.3.0 | High |
| Rotating Identifiers | v0.3.0 | High |
| Enhanced Rate Limiting | v0.2.0 | Medium |
| Traffic Padding | v0.4.0 | Medium |
| Reputation System | v0.4.0 | Low |

## Compliance and Standards

EmergencyMesh aims to follow security best practices from:
- OWASP Mobile Security Project
- Android Security Best Practices
- Bluetooth SIG Security Guidelines
- Signal Protocol Specifications (for E2E encryption)

## Disclaimer

EmergencyMesh is experimental software provided "as is" without warranty. Users are responsible for their own security and should not rely on EmergencyMesh as the sole communication method in life-threatening emergencies.

## Last Updated

This security policy was last updated on January 29, 2026 for version 0.1.0.
