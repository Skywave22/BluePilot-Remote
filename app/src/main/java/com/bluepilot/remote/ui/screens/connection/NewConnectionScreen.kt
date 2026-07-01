@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.connection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluepilot.remote.ui.theme.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.alpha

/**
 * New Connection screen with three connection options:
 * A. Connect to another device (search)
 * B. Connect from another device (discoverability)
 * C. Connect to hidden device (MAC address)
 */
@Composable
fun NewConnectionScreen(
    onNavigateToScreen: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDevices: () -> Unit
) {
    var isSearching by remember { mutableStateOf(false) }
    var isDiscovering by remember { mutableStateOf(false) }
    var macAddress by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top App Bar
            TopAppBar(
                onNavigateBack = { },
                onNavigateToSettings = onNavigateToSettings
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Searching State (Hidden initially)
            AnimatedVisibility(
                visible = isSearching,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                SearchingState(
                    onCancel = { isSearching = false }
                )
            }

            // Connection Cards
            ConnectionCards(
                isSearching = isSearching,
                onSearchClick = { isSearching = true },
                isDiscovering = isDiscovering,
                onDiscoverClick = { isDiscovering = !isDiscovering },
                macAddress = macAddress,
                onMacAddressChange = { macAddress = formatMacAddress(it) },
                onConnectClick = { /* Connect to MAC address */ },
                onNavigateToScreen = onNavigateToScreen
            )

            Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
        }

        // Bottom Navigation Bar
        BottomNavigationBar(
            onNavigateToScreen = onNavigateToScreen
        )
    }
}

@Composable
private fun TopAppBar(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit
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
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "New connection",
                color = Primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Connection Status Pill
        Surface(
            shape = CircleShape,
            color = SurfaceContainerHighest
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Error)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Disconnected",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun SearchingState(
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLow
        ),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scanning animation
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(96.dp),
                    color = Primary,
                    strokeWidth = 4.dp
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Searching",
                    tint = Primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Searching for devices",
                color = Primary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Make sure your other device is in pairing mode",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceContainerHighest,
                    contentColor = OnSurface
                ),
                shape = CircleShape
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun ConnectionCards(
    isSearching: Boolean,
    onSearchClick: () -> Unit,
    isDiscovering: Boolean,
    onDiscoverClick: () -> Unit,
    macAddress: String,
    onMacAddressChange: (String) -> Unit,
    onConnectClick: () -> Unit,
    onNavigateToScreen: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card A: Connect to another device
        ConnectionCard(
            icon = Icons.Default.Search,
            iconBackgroundColor = SecondaryContainer,
            iconTintColor = OnSecondaryContainer,
            title = "Connect to another device",
            description = "Scan for nearby Bluetooth or Network enabled BluePilot devices.",
            buttonText = "Search device",
            buttonIcon = Icons.Default.Radar,
            onButtonClick = onSearchClick,
            enabled = !isSearching,
            modifier = Modifier.alpha(if (isSearching) 0.5f else 1f)
        )

        // Card B: Connect from another device
        DiscoverabilityCard(
            isDiscovering = isDiscovering,
            onDiscoverClick = onDiscoverClick
        )

        // Card C: Connect to hidden device
        MacAddressCard(
            macAddress = macAddress,
            onMacAddressChange = onMacAddressChange,
            onConnectClick = onConnectClick
        )
    }
}

@Composable
private fun ConnectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBackgroundColor: Color,
    iconTintColor: Color,
    title: String,
    description: String,
    buttonText: String,
    buttonIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onButtonClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLow
        ),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        color = OnSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onButtonClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = OnPrimary
                )
            ) {
                Icon(
                    imageVector = buttonIcon,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun DiscoverabilityCard(
    isDiscovering: Boolean,
    onDiscoverClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLow
        ),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = OnPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Connect from another device",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Broadcast your presence to allow other controllers to find this host.",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDiscoverClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDiscovering) Primary.copy(alpha = 0.2f) else Color.Transparent,
                    contentColor = Primary
                ),
                border = if (!isDiscovering) BorderStroke(1.dp, Primary) else null
            ) {
                Icon(
                    imageVector = if (isDiscovering) Icons.Default.BluetoothDisabled else Icons.Outlined.Visibility,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isDiscovering) "Turn off discoverability" else "Turn on discoverability",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Discovery status
            AnimatedVisibility(visible = isDiscovering) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Broadcasting as \"BluePilot-Pro-2024\"",
                        color = Primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MacAddressCard(
    macAddress: String,
    onMacAddressChange: (String) -> Unit,
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLow
        ),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PrivacyTip,
                        contentDescription = null,
                        tint = OnTertiaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Connect to hidden device",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Manually enter the identifier for non-broadcasting nodes.",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // MAC Address Input
            OutlinedTextField(
                value = macAddress,
                onValueChange = { value ->
                    if (value.length <= 17) onMacAddressChange(value.uppercase())
                },
                label = { Text("MAC Address") },
                placeholder = { Text("XX:XX:XX:XX:XX:XX") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = OutlineVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConnectClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceContainerHighest,
                    contentColor = OnSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Connect",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onNavigateToScreen: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SurfaceContainer,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { onNavigateToScreen("mouse_keyboard") },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = "Controls"
                )
            },
            label = { Text("Controls") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { onNavigateToScreen("pc_keyboard") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Terminal"
                )
            },
            label = { Text("Terminal") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { onNavigateToScreen("gamepad") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "Macros"
                )
            },
            label = { Text("Macros") }
        )

        NavigationBarItem(
            selected = true,
            onClick = { onNavigateToScreen("settings") },
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

// Helper function to format MAC address
private fun formatMacAddress(input: String): String {
    val cleaned = input.uppercase().filter { it.isLetterOrDigit() }
    val formatted = StringBuilder()
    for (i in cleaned.indices) {
        if (i > 0 && i % 2 == 0) {
            formatted.append(":")
        }
        formatted.append(cleaned[i])
        if (formatted.length >= 17) break // XX:XX:XX:XX:XX:XX
    }
    return formatted.toString()
}

