# BluePilot Remote 2.0 Professional Audit Fixes

This build was audited and hardened for compile stability, security, permissions, Bluetooth HID behavior, and UI navigation.

## Fixed compile-risk issues
- Fixed `PermissionScreen.kt` duplicate `fontWeight` syntax error.
- Removed incompatible Material API usage from earlier builds.
- Added missing imports and static-checked Kotlin brace balance.
- Added explicit `lifecycle-viewmodel-ktx` dependency for `viewModelScope`.
- Removed unused heavy dependencies and inactive feature code that increased CI risk.

## Security and privacy hardening
- Removed unused camera and microphone runtime permissions.
- Permission screen now requests only Bluetooth/Nearby Devices and notification permission where needed.
- Disabled app backup and device-transfer data extraction.
- Disabled cleartext network traffic with network security config.
- Added `neverForLocation` flag to Bluetooth scan permission.
- Limited legacy Bluetooth/location permissions to Android 11 and older where possible.
- Kept services non-exported except launcher activity.

## Bluetooth HID improvements
- Added missing System Control HID descriptor.
- HID foreground service no longer destroys/unregisters HID unless the user explicitly taps Disconnect.
- Added permission checks before prepare/discoverable host mode.
- Better status messages for Windows pairing/removing old pairings.
- Kept pending connect flow for HID registration timing.

## Settings wired to real behavior
- Mouse sensitivity and pointer speed affect trackpad movement.
- Scroll speed and invert-scroll affect scroll output.
- Pen mode reduces pointer movement for precision.
- Touch vibration setting controls haptic feedback.
- Keyboard text bar and auto-clear behavior use settings.
- Gamepad mapping mode switches between HID gamepad, keyboard fallback, and mouse/keyboard behavior.
- Fullscreen/navigation settings apply in `MainActivity`.

## UI/UX fixes
- Trackpad now has a real scroll strip.
- Tap, double tap, long press, left/right/middle buttons work through HID actions.
- Connection screen now includes a Control Modes hub for Mouse, Keyboard, Media, Presenter, Numpad, Gamepad, Pairing Helper, and Help.
- Mouse bottom navigation buttons now navigate instead of doing nothing.
- Pairing helper replaces inactive scanner workflow.

## Known real-world limitation
Bluetooth HID Device mode depends on phone hardware/ROM support and Windows pairing behavior. Some phones cannot register HID Device profile even if the app compiles correctly.


## 2.0 feature upgrade additions
- Added secure screen setting using FLAG_SECURE.
- Added movement smoothing setting for smoother trackpad motion.
- Added tap-to-click setting for mouse customization.
- Added productivity keyboard shortcuts: copy, paste, cut, select all, save, undo, redo, delete.
- Added presenter shortcut wrappers for slideshow start and blank screen.
