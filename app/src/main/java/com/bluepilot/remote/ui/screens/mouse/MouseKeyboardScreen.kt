@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.mouse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bluepilot.remote.ui.theme.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable

/**
 * Mouse/Keyboard screen with touchpad interface
 * Supports responsive portrait and landscape layouts
 */
@Composable
fun MouseKeyboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToConnection: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    var isDrawerOpen by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }

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
                onMenuClick = { isDrawerOpen = true },
                onKeyboardClick = { /* Open keyboard */ },
                isConnected = isConnected
            )

            if (isLandscape) {
                LandscapeContent(
                    isConnected = isConnected,
                    onNavigateToConnection = onNavigateToConnection
                )
            } else {
                PortraitContent(
                    isConnected = isConnected,
                    onNavigateToConnection = onNavigateToConnection
                )
            }
        }

        // Navigation Drawer Overlay
        if (isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { isDrawerOpen = false }
            )
            NavigationDrawer(
                modifier = Modifier.align(Alignment.CenterStart),
                onClose = { isDrawerOpen = false }
            )
        }

        // Bottom Navigation Bar
        BottomNavigationBar(
            isLandscape = isLandscape
        )
    }
}

@Composable
private fun TopAppBar(
    onMenuClick: () -> Unit,
    onKeyboardClick: () -> Unit,
    isConnected: Boolean
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
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mouse / Keyboard",
                color = Primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onKeyboardClick) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = "Keyboard",
                    tint = OnSurfaceVariant
                )
            }

            // Connection Status Pill
            Surface(
                shape = CircleShape,
                color = if (isConnected) SurfaceContainer else ErrorContainer.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) Color(0xFF10B981) else Error)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) "Connected" else "Disconnected",
                        color = if (isConnected) OnSurface else Error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PortraitContent(
    isConnected: Boolean,
    onNavigateToConnection: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warning Card if not connected
        if (!isConnected) {
            ConnectionWarningCard(onNavigateToConnection = onNavigateToConnection)
        }

        // Touchpad Area
        TouchpadArea(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Mouse Buttons
        MouseButtons(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LandscapeContent(
    isConnected: Boolean,
    onNavigateToConnection: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left side: Touchpad (expanded)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isConnected) {
                ConnectionWarningCard(onNavigateToConnection = onNavigateToConnection)
            }
            
            TouchpadArea(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        // Right side: Mouse buttons (compact)
        Column(
            modifier = Modifier
                .width(120.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactMouseButtons()
        }
    }
}

@Composable
private fun ConnectionWarningCard(
    onNavigateToConnection: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ErrorContainer.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, Error.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ErrorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BluetoothDisabled,
                        contentDescription = null,
                        tint = OnErrorContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Connection Required",
                        color = Error,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Click below to configure your remote device and start controlling.",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = onNavigateToConnection,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error,
                    contentColor = OnError
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Setup remote device")
            }
        }
    }
}

@Composable
private fun TouchpadArea(
    modifier: Modifier = Modifier
) {
    var lastPosition by remember { mutableStateOf(Offset.Zero) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryContainer.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            lastPosition = offset
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            // Send mouse movement based on drag amount.
                            lastPosition += dragAmount
                        }
                    )
                }
        ) {
            // Center mouse icon
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.05f))
                        .border(1.dp, Primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mouse,
                        contentDescription = "Touchpad",
                        tint = Primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Scroll bar (right side)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(vertical = 32.dp, horizontal = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Outlined.ExpandLess,
                    contentDescription = "Scroll Up",
                    tint = Primary.copy(alpha = 0.3f)
                )
                
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Primary.copy(alpha = 0.2f))
                )
                
                Icon(
                    imageVector = Icons.Outlined.ExpandMore,
                    contentDescription = "Scroll Down",
                    tint = Primary.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun MouseButtons(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MouseButton(
            text = "Left",
            modifier = Modifier.weight(1f),
            onClick = { /* Left click */ }
        )
        
        MouseButton(
            text = "Right",
            modifier = Modifier.weight(1f),
            onClick = { /* Right click */ }
        )
    }
}

@Composable
private fun CompactMouseButtons() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MouseButton(
            text = "L",
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* Left click */ }
        )
        
        MouseButton(
            text = "R",
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* Right click */ }
        )
    }
}

@Composable
private fun MouseButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceContainerHighest,
            contentColor = OnSurface
        ),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = OnSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun NavigationDrawer(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BP",
                        color = OnPrimaryContainer,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "BluePilot Pro",
                        color = Primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Power User • v2.4.0",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Divider(color = OutlineVariant.copy(alpha = 0.3f))

            // Navigation items
            DrawerItem(
                icon = Icons.Default.SettingsRemote,
                text = "Dashboard",
                isSelected = false,
                onClick = { }
            )

            DrawerItem(
                icon = Icons.Default.Mouse,
                text = "Touchpad",
                isSelected = true,
                onClick = onClose
            )

            DrawerItem(
                icon = Icons.Default.Keyboard,
                text = "Keyboard",
                isSelected = false,
                onClick = { }
            )

            DrawerItem(
                icon = Icons.Default.SettingsBluetooth,
                text = "Device Settings",
                isSelected = false,
                onClick = { }
            )
        }
    }
}

@Composable
private fun DrawerItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
        color = if (isSelected) SecondaryContainer else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isSelected) OnSecondaryContainer else OnSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                color = if (isSelected) OnSecondaryContainer else OnSurfaceVariant,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    isLandscape: Boolean
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SurfaceContainer,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsRemote,
                    contentDescription = "Controls"
                )
            },
            label = { Text("Controls") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = "Keyboard"
                )
            },
            label = { Text("Keyboard") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = "Gamepad"
                )
            },
            label = { Text("Gamepad") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings") }
        )
    }
}
