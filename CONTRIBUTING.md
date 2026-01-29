# Contributing to EmergencyMesh

Thank you for your interest in contributing to EmergencyMesh! This document provides guidelines for contributing to the project.

## Code of Conduct

Be respectful, inclusive, and professional. This is an emergency communication tool that may be used in critical situations, so quality and reliability are paramount.

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported in [Issues](https://github.com/markvanengelen-gulo/emergencymesh/issues)
2. If not, create a new issue with:
   - Clear title describing the problem
   - Steps to reproduce
   - Expected vs. actual behavior
   - Android version and device model
   - EmergencyMesh version
   - Relevant logs or screenshots

### Suggesting Features

1. Check existing issues and roadmap in README.md
2. Create an issue with:
   - Clear description of the feature
   - Use case and motivation
   - Proposed implementation approach (optional)
   - Potential security/privacy implications

### Submitting Changes

1. **Fork** the repository
2. **Create a branch** from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes** following the guidelines below
4. **Test thoroughly** (see Testing section)
5. **Commit** with clear messages (see Commit Guidelines)
6. **Push** to your fork
7. **Open a Pull Request** with:
   - Description of changes
   - Link to related issues
   - Testing performed
   - Screenshots (for UI changes)

## Development Setup

### Prerequisites

- Android Studio (Arctic Fox or newer)
- JDK 11 or higher
- Git
- Android device or emulator with BLE support

### Setup Steps

1. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/emergencymesh.git
   cd emergencymesh
   ```

2. Open `android` directory in Android Studio

3. Sync Gradle and resolve dependencies

4. Create a device/emulator configuration with:
   - Minimum API level 23
   - BLE support enabled

## Coding Guidelines

### Kotlin Style

Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// Good
class MessageManager(
    private val database: AppDatabase,
    private val peerManager: PeerManager
) {
    fun sendMessage(content: String, groupId: String?) {
        // Implementation
    }
}

// Use meaningful names
val messageId = generateMessageId(content)
val isAlreadySeen = seenMessageIds.contains(messageId)

// Prefer immutability
val message = Message(
    messageId = messageId,
    content = content,
    timestamp = System.currentTimeMillis()
)
```

### Jetpack Compose Style

```kotlin
// Composable names are PascalCase
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = { HomeTopBar() }
    ) { paddingValues ->
        HomeContent(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState
        )
    }
}

// Extract reusable components
@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState
) {
    Column(modifier = modifier) {
        // Content
    }
}
```

### Architecture Patterns

- **MVVM**: Use ViewModel for business logic, Compose for UI
- **Repository Pattern**: Abstract data sources (Room, Preferences)
- **Dependency Injection**: Use Hilt or manual DI
- **Coroutines**: For asynchronous operations
- **StateFlow/LiveData**: For reactive state management

```kotlin
class HomeViewModel(
    private val messageRepository: MessageRepository,
    private val peerManager: PeerManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun sendMessage(content: String) {
        viewModelScope.launch {
            messageRepository.sendMessage(content)
        }
    }
}
```

### Room Database

```kotlin
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val messageId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val groupId: String?
)

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(limit: Int): Flow<List<Message>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)
}
```

### BLE Code

- Always check permissions before BLE operations
- Handle callbacks on appropriate threads
- Clean up connections properly
- Implement timeouts for operations

```kotlin
class BleManager(private val context: Context) {
    
    fun startScanning(callback: ScanCallback) {
        if (!hasBluetoothPermissions()) {
            // Request permissions
            return
        }
        
        bluetoothAdapter?.bluetoothLeScanner?.startScan(
            scanFilters,
            scanSettings,
            callback
        )
    }
    
    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Handle legacy permissions
            true
        }
    }
}
```

## Testing

### Unit Tests

- Test business logic in isolation
- Use JUnit 4 and MockK
- Aim for 70%+ code coverage

```kotlin
class MessageManagerTest {
    
    private lateinit var messageManager: MessageManager
    private val mockDatabase = mockk<AppDatabase>()
    
    @Before
    fun setup() {
        messageManager = MessageManager(mockDatabase)
    }
    
    @Test
    fun `sendMessage creates message with correct fields`() {
        val content = "Test message"
        val message = messageManager.createMessage(content)
        
        assertEquals(content, message.content)
        assertTrue(message.timestamp > 0)
        assertNotNull(message.messageId)
    }
}
```

### Instrumented Tests

- Test Android-specific functionality
- Test Room database operations
- Test UI with Compose testing

```kotlin
@RunWith(AndroidJUnit4::class)
class MessageDaoTest {
    
    private lateinit var database: AppDatabase
    private lateinit var messageDao: MessageDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        messageDao = database.messageDao()
    }
    
    @Test
    fun insertAndRetrieveMessage() = runBlocking {
        val message = Message(
            messageId = "test-id",
            content = "Test",
            timestamp = System.currentTimeMillis()
        )
        
        messageDao.insertMessage(message)
        val retrieved = messageDao.getRecentMessages(10).first()
        
        assertTrue(retrieved.contains(message))
    }
}
```

### Manual Testing

Before submitting a PR, test:
1. Fresh install and onboarding
2. Permissions flow
3. Message sending and receiving
4. Relay functionality
5. Settings changes
6. App backgrounding/foregrounding
7. Device rotation
8. Low battery scenarios

See [docs/test-plan.md](docs/test-plan.md) for complete checklist.

## Commit Guidelines

Use clear, descriptive commit messages:

```
Format: <type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation only
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Build/tooling changes

**Examples:**
```
feat(ble): implement device discovery scanner

Add BLE scanner with service UUID filtering and background scanning support.
Includes timeout handling and proper permission checks.

Closes #42

---

fix(ui): correct peer count display on home screen

The peer count was showing disconnected peers. Now filters to only
show currently connected peers.

Fixes #58

---

docs(readme): update build instructions for Android 12+

Add note about new BLE permissions required on Android 12 and above.
```

## Pull Request Guidelines

### Before Submitting

- [ ] Code follows style guidelines
- [ ] All tests pass (`./gradlew test connectedCheck`)
- [ ] New tests added for new features
- [ ] Documentation updated if needed
- [ ] No merge conflicts with `main`
- [ ] Commits are squashed/cleaned up
- [ ] PR description is clear and complete

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues
Closes #123

## Testing Performed
- [ ] Unit tests added/updated
- [ ] Instrumented tests added/updated
- [ ] Manual testing completed
- Tested on: Android 11, Pixel 4a

## Screenshots (if UI changes)
[Add screenshots]

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings introduced
- [ ] Tests pass locally
```

## Code Review Process

1. **Automated Checks**: CI runs tests and linting
2. **Peer Review**: At least one maintainer reviews code
3. **Discussion**: Address feedback and questions
4. **Approval**: Once approved, maintainer will merge
5. **Cleanup**: Delete branch after merge

### Review Criteria

Reviewers check for:
- Correctness and functionality
- Code quality and style
- Test coverage
- Security implications
- Performance impact
- Documentation completeness

## Security Considerations

When contributing code that handles:
- **User data**: Follow privacy guidelines in PRIVACY.md
- **Network communication**: Consider encryption and validation
- **Permissions**: Request only necessary permissions
- **BLE operations**: Handle malicious/malformed data
- **Database**: Sanitize inputs, handle constraints

Report security issues privately - do not open public issues.

## Documentation

Update documentation when:
- Adding new features
- Changing architecture
- Modifying protocols
- Updating dependencies
- Changing build process

Documentation locations:
- User-facing: README.md, docs/
- Technical: ARCHITECTURE.md, code comments
- Security: SECURITY.md
- Privacy: PRIVACY.md

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Questions?

- Open an issue with the `question` label
- Check existing issues and documentation
- Review [ARCHITECTURE.md](ARCHITECTURE.md) for technical details

## Thank You!

Every contribution helps make EmergencyMesh better for people in emergency situations. Your time and expertise are greatly appreciated!
