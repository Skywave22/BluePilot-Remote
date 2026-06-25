# BluePilot Remote

A native Android app that transforms your phone into a versatile Bluetooth HID remote control. Control your computer, TV, tablet, and games over Bluetooth without requiring any server software on the target device.

## Features

- **Mouse & Keyboard**: Full touchpad support with click, scroll, and keyboard input
- **Multimedia Controls**: Play/pause, volume, navigation, and D-pad controls
- **Presenter Mode**: Slide navigation, black screen, and presentation controls
- **PC Keyboard**: Full QWERTY keyboard with function keys and modifiers
- **Numpad**: Numeric keypad with calculator shortcuts
- **Gamepad**: Virtual gamepad with analog sticks and action buttons (landscape-first)
- **Bluetooth HID**: Direct connection using Android Bluetooth HID Device APIs
- **Responsive UI**: Material 3 design with portrait and landscape support
- **Customizable**: Extensive settings for sensitivity, theme, and behavior

## Requirements

- Android 9.0 (API Level 28) or higher
- Bluetooth support
- Camera (for QR/barcode scanning - coming soon)
- Microphone (for voice commands - coming soon)

## Setup

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK with API Level 34

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd APP Project
```

2. Open the project in Android Studio

3. Sync Gradle files:
   - Android Studio will automatically prompt to sync Gradle
   - Click "Sync Now" when prompted

4. Build the project:
   - Go to Build > Make Project
   - Or press Ctrl+F9 (Windows) or Cmd+F9 (Mac)

5. Run on device:
   - Connect an Android device via USB
   - Enable USB debugging on the device
   - Click the Run button in Android Studio

## Architecture

### Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with ViewModels
- **Dependency Injection**: Hilt
- **Async**: Coroutines and Flow
- **Data Persistence**: DataStore for settings, Room for custom layouts (planned)
- **Bluetooth**: Android Bluetooth HID Device APIs
- **Camera**: CameraX (for QR scanning - planned)
- **Barcode**: ML Kit (for QR scanning - planned)

### Project Structure

```
app/src/main/java/com/bluepilot/remote/
├── bluetooth/
│   └── BluetoothHidManager.kt      # Bluetooth HID connection management
├── data/
│   └── SettingsRepository.kt        # DataStore settings management
├── model/
│   ├── ConnectionState.kt          # Connection state models
│   ├── HidAction.kt                # HID action models
│   ├── GamepadState.kt             # Gamepad state models
│   ├── AppSettings.kt              # App settings models
│   └── CustomLayout.kt             # Custom layout models
├── permission/
│   └── PermissionManager.kt        # Permission handling
├── service/
│   └── HidService.kt               # Foreground service for HID connection
├── ui/
│   ├── navigation/
│   │   └── BluePilotNavigation.kt  # Navigation setup
│   ├── screens/
│   │   ├── permission/
│   │   │   └── PermissionScreen.kt # Permission request screen
│   │   ├── connection/
│   │   │   └── NewConnectionScreen.kt # Connection setup screen
│   │   ├── mouse/
│   │   │   └── MouseKeyboardScreen.kt # Mouse/keyboard control
│   │   ├── multimedia/
│   │   │   └── MultimediaScreen.kt # Media controls
│   │   ├── presenter/
│   │   │   └── PresenterScreen.kt  # Presentation controls
│   │   ├── keyboard/
│   │   │   └── PCKeyboardScreen.kt # Full keyboard
│   │   ├── numpad/
│   │   │   └── NumpadScreen.kt     # Numeric keypad
│   │   ├── gamepad/
│   │   │   └── GamepadScreen.kt    # Virtual gamepad
│   │   ├── settings/
│   │   │   └── SettingsScreen.kt   # Settings screen
│   │   ├── scanner/
│   │   │   └── ScannerScreen.kt    # QR scanner (placeholder)
│   │   ├── devices/
│   │   │   └── DevicesScreen.kt    # Device management
│   │   └── help/
│   │       └── HelpScreen.kt       # Help and about
│   └── theme/
│       ├── Color.kt                # Color definitions
│       ├── Type.kt                 # Typography
│       └── Theme.kt                # Theme setup
├── viewmodel/
│   └── MainViewModel.kt            # Main app ViewModel
├── BluePilotApplication.kt         # Application class
└── MainActivity.kt                # Main activity
```

## Usage

### First-Time Setup

1. **Grant Permissions**
   - Launch the app
   - Review and grant required permissions:
     - Bluetooth (required)
     - Nearby devices (required)
     - Camera (optional, for QR scanning)
     - Microphone (optional, for voice commands)

2. **Connect to Device**
   - Go to "New Connection" screen
   - Choose one of three connection methods:
     - **Search**: Scan for nearby Bluetooth devices
     - **Discoverability**: Make your device discoverable to others
     - **MAC Address**: Manually enter device MAC address

3. **Select Control Mode**
   - Navigate to desired control screen:
     - Mouse/Keyboard
     - Multimedia
     - Presenter
     - PC Keyboard
     - Numpad
     - Gamepad

### Control Modes

#### Mouse/Keyboard
- Use the touchpad area for mouse movement
- Tap for left click, double-tap for double-click
- Use left/right buttons for clicks
- Scroll bar on the right side for vertical scrolling
- Keyboard button to toggle virtual keyboard

#### Multimedia
- Play/pause, skip, rewind controls
- Volume up/down/mute
- D-pad for navigation
- Touchpad for cursor control

#### Presenter
- Previous/Next slide buttons
- Start presentation, Escape, Black screen
- Touchpad for cursor control

#### PC Keyboard
- Full QWERTY keyboard layout
- Function keys (F1-F12)
- Modifier keys (Ctrl, Alt, Win)
- Arrow keys and navigation

#### Numpad
- Numeric keypad (0-9)
- Arithmetic operators (+, -, *, /)
- Enter, Backspace, Num Lock
- Quick shortcuts for common apps

#### Gamepad
- Landscape orientation recommended
- Left analog stick
- D-pad
- Action buttons (A, B, X, Y)
- Shoulder buttons (L1, R1)
- Select/Start buttons

## Settings

Access settings from any screen via the bottom navigation bar.

### General
- Theme (Light/Dark/System)
- Fullscreen mode
- Keep screen on
- Touch vibrations
- Show Android navigation
- Show media buttons
- Show shortcuts

### Mouse
- Air mouse (gyroscope)
- Sensitivity
- Pointer speed
- Scroll speed
- Invert scroll
- Mouse jiggler
- Pen mode

### Keyboard
- Language
- Show text input bar
- Auto-hide keyboard

### Gamepad
- Gamepad mode (HID/Keyboard fallback)
- Joystick sensitivity
- Dead zone
- Button opacity
- Turbo speed
- Lock layout while playing
- Haptic feedback

## Testing

### Manual Testing Guide

#### 1. Permission Testing
- Launch app on Android 9+ device
- Verify permission request dialog appears
- Grant all required permissions
- Verify app proceeds to connection screen

#### 2. Bluetooth Connection Testing
- Ensure Bluetooth is enabled on both devices
- Test "Search" connection method:
  - Verify device discovery works
  - Verify pairing process
  - Verify connection status indicator
- Test "MAC Address" connection method:
  - Enter valid MAC address
  - Verify manual connection works

#### 3. HID Functionality Testing
- Connect to a computer with Bluetooth HID support
- Test Mouse/Keyboard screen:
  - Move cursor on touchpad
  - Verify left/right click
  - Test scroll functionality
  - Type using virtual keyboard
- Test Multimedia screen:
  - Verify media controls work (play/pause, volume)
  - Test D-pad navigation
- Test Presenter screen:
  - Verify slide navigation
  - Test black screen toggle

#### 4. Responsive UI Testing
- Test all screens in portrait mode
- Rotate device to landscape
- Verify layouts adapt correctly
- Test gamepad specifically in landscape mode

#### 5. Settings Testing
- Change theme (Light/Dark/System)
- Verify theme applies correctly
- Toggle fullscreen mode
- Adjust mouse sensitivity
- Verify settings persist after app restart

### Known Limitations

- QR/Barcode scanner requires CameraX integration (placeholder implemented)
- Voice commands require microphone integration (placeholder implemented)
- Custom layout editor requires Room database (planned)
- Some devices may not support Bluetooth HID Device API

## Troubleshooting

### Device not found
- Ensure Bluetooth is enabled on both devices
- Verify target device is in pairing mode
- Check Bluetooth permissions are granted
- Restart Bluetooth on both devices

### Connection drops frequently
- Check Bluetooth signal strength
- Reduce interference from other devices
- Ensure devices are within range (~10 meters)
- Restart the app

### Controls not responding
- Verify HID support on target device
- Check if target device recognizes the HID profile
- Restart the Bluetooth HID service
- Test with a different target device

### App crashes on startup
- Ensure Android 9+ is installed
- Check if all permissions are granted
- Clear app data and restart
- Check Android Studio Logcat for error details

## Development

### Building from Source

```bash
# Clone repository
git clone <repository-url>
cd APP Project

# Open in Android Studio
# Sync Gradle
# Build and run
```

### Running Tests

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions focused and small

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues, questions, or feature requests:
- GitHub Issues: [repository-url]/issues
- Email: support@bluepilot.remote

## Acknowledgments

- Android Bluetooth HID Device API documentation
- Jetpack Compose and Material 3 design guidelines
- Open-source community for Bluetooth HID implementations

## Version History

### Version 2.4.0 (Current)
- Initial release with core features
- Mouse/Keyboard, Multimedia, Presenter screens
- PC Keyboard and Numpad
- Gamepad with landscape support
- Settings with multiple tabs
- Responsive portrait/landscape UI
- Material 3 design

### Planned Features
- QR/Barcode scanner with CameraX
- Voice commands with microphone
- Custom layout editor
- Room database for layouts
- Macro recording and playback
- Profile management

## Contact

- Project Website: [website-url]
- Documentation: [docs-url]
- Support: support@bluepilot.remote
