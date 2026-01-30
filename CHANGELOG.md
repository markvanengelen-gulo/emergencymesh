# Changelog

All notable changes to EmergencyMesh will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project structure and documentation
- Android application scaffold with Kotlin and Jetpack Compose
- Basic UI screens (Onboarding, Home, Settings)
- Room database setup for peers, messages, and seen message IDs
- BLE permissions configuration

## [0.1.0] - 2026-01-29

### Added
- Project repository initialization
- Comprehensive documentation:
  - README.md with project overview and build instructions
  - ARCHITECTURE.md detailing system design and data flow
  - SECURITY.md with threat model and security considerations
  - PRIVACY.md explaining data collection and retention
  - CONTRIBUTING.md with contribution guidelines
  - CHANGELOG.md (this file)
  - LICENSE file (MIT)
- Technical documentation in /docs:
  - protocol.md: Message format and protocol specification
  - permissions.md: Android permissions requirements
  - test-plan.md: Manual testing checklist
- Android project scaffold:
  - Gradle build configuration
  - Kotlin and Jetpack Compose setup
  - Minimum SDK 23 (Android 6.0) for BLE support
  - Material 3 design system
- Core UI screens:
  - Onboarding screen for nickname and group setup
  - Home screen with relay toggle and peer count
  - Settings screen for user preferences
- Room database schema:
  - Peers table with device ID, nickname, last seen
  - Messages table with content, metadata, TTL
  - SeenMessageIds table for deduplication
- Permission handling for:
  - Bluetooth Low Energy (BLE)
  - Location (required for BLE scanning on Android < 12)
  - Nearby Devices (Android 12+)

### Status
- ✅ Phase 0 (Documentation and Structure): Complete
- 🚧 Phase 1 (Android Scaffold): Complete - app builds successfully
- ⏳ Phase 2 (BLE Communication): Not started
- ⏳ Phase 3 (Message Routing): Not started
- ⏳ Phase 4 (Enhanced Features): Not started

[Unreleased]: https://github.com/markvanengelen-gulo/emergencymesh/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/markvanengelen-gulo/emergencymesh/releases/tag/v0.1.0
