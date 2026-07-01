@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bluepilot.remote.ui.theme.*
import androidx.compose.ui.draw.clip

/**
 * Help/About screen with app information and support links
 */
@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                onNavigateBack = onNavigateBack
            )

            // Help Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Help,
                                contentDescription = "Help Icon",
                                tint = OnPrimaryContainer,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "BluePilot Remote",
                            color = OnSurface,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Version 2.4.0",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "A versatile Bluetooth HID remote control for Android devices. Control your computer, TV, tablet, and games over Bluetooth.",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Quick Start Section
                HelpSection(title = "Quick Start") {
                    HelpItem(
                        title = "1. Grant Permissions",
                        description = "Enable Bluetooth, camera, and microphone permissions when prompted"
                    )
                    HelpItem(
                        title = "2. Connect to Device",
                        description = "Use the New Connection screen to search for devices or enter MAC address"
                    )
                    HelpItem(
                        title = "3. Choose Control Mode",
                        description = "Select Mouse/Keyboard, Multimedia, Presenter, or Gamepad"
                    )
                    HelpItem(
                        title = "4. Start Controlling",
                        description = "Use the touchpad, buttons, or controls to interact with your device"
                    )
                }

                // Features Section
                HelpSection(title = "Features") {
                    HelpItem(
                        title = "Mouse & Keyboard",
                        description = "Full touchpad support with click, scroll, and keyboard input"
                    )
                    HelpItem(
                        title = "Multimedia Controls",
                        description = "Play/pause, volume, navigation, and D-pad controls"
                    )
                    HelpItem(
                        title = "Presenter Mode",
                        description = "Slide navigation, black screen, and presentation controls"
                    )
                    HelpItem(
                        title = "PC Keyboard",
                        description = "Full QWERTY keyboard with function keys and modifiers"
                    )
                    HelpItem(
                        title = "Numpad",
                        description = "Numeric keypad with calculator shortcuts"
                    )
                    HelpItem(
                        title = "Gamepad",
                        description = "Virtual gamepad with analog sticks and action buttons"
                    )
                }

                // Troubleshooting Section
                HelpSection(title = "Troubleshooting") {
                    HelpItem(
                        title = "Device not found",
                        description = "Ensure the target device has Bluetooth enabled and is in pairing mode"
                    )
                    HelpItem(
                        title = "Connection drops",
                        description = "Check Bluetooth signal strength and reduce interference"
                    )
                    HelpItem(
                        title = "Controls not responding",
                        description = "Verify HID support on target device and restart the app"
                    )
                }

                // Support Section
                HelpSection(title = "Support") {
                    HelpItem(
                        title = "Report a Bug",
                        description = "Submit bug reports through GitHub Issues"
                    )
                    HelpItem(
                        title = "Feature Request",
                        description = "Suggest new features on our GitHub Discussions"
                    )
                    HelpItem(
                        title = "Contact Support",
                        description = "Email us at support@bluepilot.remote"
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Help & About",
                color = Primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HelpSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = Primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SurfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun HelpItem(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = OnSurface,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = description,
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
