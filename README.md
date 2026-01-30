# EmergencyMesh

EmergencyMesh is a peer-to-peer emergency communication application for Android that enables off-grid messaging through Bluetooth Low Energy (BLE) mesh networking. When traditional communication infrastructure fails, EmergencyMesh creates a resilient network by relaying messages between nearby devices.

## Overview

EmergencyMesh allows users to:
- Send and receive emergency messages without internet or cellular connectivity
- Automatically relay messages to extend network range
- Organize into groups for targeted communication
- Maintain privacy through encryption and minimal data retention

The application uses BLE to discover nearby devices and establish peer-to-peer connections, creating a self-organizing mesh network where each device can act as a message relay.

## How It Works

1. **Discovery**: Devices continuously scan for nearby peers using BLE advertising
2. **Connection**: When peers are discovered, devices establish BLE connections
3. **Messaging**: Users can send messages that are encrypted and broadcast to peers
4. **Relaying**: Devices automatically forward messages to other peers, extending range
5. **Deduplication**: Message IDs prevent infinite loops and duplicate deliveries

## Project Status

**Current Version**: 0.1.0 (Initial Development)

This project is in early development. See [CHANGELOG.md](CHANGELOG.md) for version history.

## Build Instructions

### Prerequisites

- Android Studio (Arctic Fox or newer)
- JDK 11 or higher
- Android SDK with minimum API level 23 (Android 6.0)

### Building the Android App

1. Clone the repository:
   ```bash
   git clone https://github.com/markvanengelen-gulo/emergencymesh.git
   cd emergencymesh
   ```

2. Open the `android` directory in Android Studio

3. Sync Gradle and build:
   ```bash
   cd android
   ./gradlew build
   ```

4. Run on a device or emulator:
   ```bash
   ./gradlew installDebug
   ```

### Running Tests

```bash
cd android
./gradlew test           # Run unit tests
./gradlew connectedCheck # Run instrumented tests
```

## Quick Start

1. Install EmergencyMesh on multiple Android devices
2. Grant required permissions (Bluetooth, Location)
3. Complete onboarding by setting a nickname
4. Enable relay mode to help extend the network
5. Join or create a group to start messaging

## Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed information about:
- Application modules and components
- BLE discovery and connection protocol
- Message routing and data flow
- Database schema

## Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) - Technical architecture and design
- [SECURITY.md](SECURITY.md) - Security model and threat analysis
- [PRIVACY.md](PRIVACY.md) - Privacy considerations and data handling
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution guidelines
- [docs/protocol.md](docs/protocol.md) - Message protocol specification
- [docs/permissions.md](docs/permissions.md) - Android permissions explained
- [docs/test-plan.md](docs/test-plan.md) - Testing guidelines

## Roadmap

### Phase 0: Foundation ✓
- Repository structure and documentation
- Technical specifications

### Phase 1: Basic Android App (In Progress)
- Runnable Android scaffold with Kotlin + Jetpack Compose
- Basic UI screens (onboarding, home, settings)
- Room database setup
- BLE permissions configuration

### Phase 2: BLE Communication (Planned)
- BLE device discovery and advertising
- Peer connection management
- Basic message transmission

### Phase 3: Message Routing (Planned)
- Message relay implementation
- TTL and deduplication
- Group-based routing

### Phase 4: Enhanced Features (Planned)
- End-to-end encryption
- Offline message queue
- Battery optimization
- UI enhancements

## Contributing

We welcome contributions! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for:
- Code style guidelines
- Development workflow
- Pull request process

## Security

EmergencyMesh is designed with security in mind. See [SECURITY.md](SECURITY.md) for:
- Threat model
- Encryption approach
- Known limitations

## Privacy

Privacy is a core principle. See [PRIVACY.md](PRIVACY.md) for:
- Data retention policies
- What data is stored and where
- Privacy considerations

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Disclaimer

EmergencyMesh is experimental software. While designed for emergency communication, it should not be relied upon as the sole means of communication in life-threatening situations. Always have backup communication plans.

## Contact

For questions or issues, please open an issue on GitHub.