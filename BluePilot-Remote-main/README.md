# BluePilot Remote 2.0

BluePilot Remote turns an Android phone into a privacy-first Bluetooth HID remote for a PC or other Bluetooth host. It focuses on real working controls: PC connection, mouse trackpad, keyboard, numpad, multimedia, presenter, gamepad, devices, settings, pairing help, and troubleshooting.

## Production features

- **PC connection hub** with guided Windows pairing steps.
- **Mouse trackpad** with drag movement, tap click, double click, long-press right click, left/middle/right buttons, and scroll strip.
- **PC keyboard** with text send, function keys, arrows, modifiers, Enter, Backspace, Tab, Escape, and Space.
- **Numpad** for numeric entry and calculator-style workflows.
- **Multimedia controls** for play/pause, stop, track navigation, volume and brightness controls where supported.
- **Presenter controls** for slide navigation and presentation keyboard fallbacks.
- **Gamepad** with HID gamepad mode, keyboard fallback mode, and mouse/keyboard mode.
- **Devices screen** for paired and nearby Bluetooth devices.
- **Settings** for active runtime behavior only.
- **2.0 smoothness controls**: movement smoothing, tap-to-click toggle, pointer speed and precision mode.
- **2.0 security option**: secure screen toggle blocks screenshots and app switcher preview.
- **2.0 keyboard shortcuts**: copy, paste, cut, select all, save, undo, redo and delete.
- **Pairing helper** for stuck Windows pairing cases.
- **Help screen** with real troubleshooting steps.

## Privacy and security

- No account required.
- No cloud server required.
- No camera permission.
- No microphone permission.
- Cleartext network traffic disabled.
- App backup disabled.
- Foreground HID service is not exported.
- Bluetooth controls are sent directly over Android Bluetooth HID APIs.

## Requirements

- Android 9.0 / API 28 or newer.
- Bluetooth support.
- A phone/ROM that supports Android Bluetooth HID Device mode.
- A target PC/device that accepts Bluetooth HID input.

Important: Android HID Device support depends on phone hardware and ROM. Some phones can pair but Android may refuse HID registration. The app shows HID unsupported or connection messages when this happens.

## Best Windows PC setup

1. Remove old BluePilot/phone pairing from Windows Bluetooth settings.
2. Open BluePilot and tap **Prepare PC connection**.
3. Accept Android discoverability.
4. On Windows, open **Bluetooth > Add device > Bluetooth**.
5. Select the phone and accept pairing on both sides.
6. Wait until BluePilot shows **Connected**.
7. Test Keyboard first with Enter or Space, then test Mouse.

## Settings that are active in 2.0

### General
- Theme: Light, Dark, System.
- Fullscreen mode.
- Keep screen on.
- Show Android navigation.
- Touch vibrations.
- Secure screen screenshot protection.

### Mouse
- Sensitivity.
- Pointer speed.
- Scroll speed.
- Movement smoothing.
- Invert scroll.
- Tap to click.
- Pen mode for precise movement.

### Keyboard
- Show or hide text input bar.

### Gamepad
- Mode: HID gamepad, keyboard fallback, mouse/keyboard.
- Joystick sensitivity.
- Dead zone.
- Haptic feedback.

## Build

GitHub Actions builds:

- Debug APK.
- Installable release APK signed with the CI debug key.

Manual build:

```bash
chmod +x ./gradlew
./gradlew :app:compileDebugKotlin --stacktrace --no-daemon
./gradlew :app:assembleDebug --stacktrace --no-daemon
./gradlew :app:assembleRelease --stacktrace --no-daemon
```

## Project stack

- Kotlin.
- Jetpack Compose.
- Material 3.
- Hilt.
- Coroutines and Flow.
- DataStore Preferences.
- Android Bluetooth HID Device APIs.

## Version history

### 2.0.0
- Professional glassmorphism UI pass.
- Added movement smoothing, tap-to-click toggle, secure screen mode and productivity shortcuts.
- Security hardening.
- Removed inactive camera/microphone/fake feature permissions.
- Trackpad scroll strip and settings-based movement.
- Real settings wiring for mouse, keyboard visibility, gamepad and haptics.
- Improved Windows pairing guidance.
- Devices and Help screens rebuilt.
- GitHub Actions APK workflow retained.

## Known limitations

- Bluetooth HID Device mode is not supported by all Android phones/ROMs.
- Some Windows pairings require removing old phone entries before pairing again.
- QR scanner and voice commands are not included in this production build.
