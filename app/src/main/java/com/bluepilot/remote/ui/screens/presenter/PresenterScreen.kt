package com.bluepilot.remote.ui.screens.presenter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bluepilot.remote.ui.theme.*

/**
 * Presenter screen for presentation controls
 * Supports responsive portrait and landscape layouts
 */
@Composable
fun PresenterScreen(
    onNavigateBack: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

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
                onMenuClick = { },
                onNavigateBack = onNavigateBack
            )

            if (isLandscape) {
                LandscapeContent()
            } else {
                PortraitContent()
            }
        }

        // Bottom Navigation Bar
        BottomNavigationBar()
    }
}

@Composable
private fun TopAppBar(
    onMenuClick: () -> Unit,
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
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Presenter",
                color = Primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Connection Status Pill
        Surface(
            shape = CircleShape,
            color = SurfaceContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Connected",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun PortraitContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Previous/Next buttons
        SlideNavigationCard(
            modifier = Modifier.fillMaxWidth()
        )

        // Presentation controls
        PresentationControlsCard(
            modifier = Modifier.fillMaxWidth()
        )

        // Touchpad
        TouchpadCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun LandscapeContent() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left half: Previous slide
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            LargeSlideButton(
                text = "Previous",
                icon = Icons.Default.ArrowBack,
                onClick = { },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Right half: Next slide
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            LargeSlideButton(
                text = "Next",
                icon = Icons.Default.ArrowForward,
                onClick = { },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // Compact top row with controls (overlay)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CompactControlButton(
                text = "Start",
                onClick = { }
            )
            CompactControlButton(
                text = "Esc",
                onClick = { }
            )
            CompactControlButton(
                text = "Blank",
                onClick = { }
            )
            CompactControlButton(
                icon = Icons.Default.TouchApp,
                onClick = { }
            )
        }
    }
}

@Composable
private fun SlideNavigationCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLow
        ),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LargeSlideButton(
                text = "Previous",
                icon = Icons.Default.ArrowBack,
                onClick = { },
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            LargeSlideButton(
                text = "Next",
                icon = Icons.Default.ArrowForward,
                onClick = { },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PresentationControlsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLow
        ),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PresentationButton(
                text = "Start",
                onClick = { }
            )
            
            PresentationButton(
                text = "Esc",
                onClick = { }
            )
            
            PresentationButton(
                text = "Blank",
                onClick = { }
            )
            
            PresentationButton(
                icon = Icons.Default.TouchApp,
                onClick = { }
            )
        }
    }
}

@Composable
private fun TouchpadCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryContainer.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = "Touchpad",
                    tint = Primary.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Touchpad",
                    color = Primary.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun LargeSlideButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = OnPrimary
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PresentationButton(
    text: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceContainerHighest,
            contentColor = OnSurface
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text ?: "",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun CompactControlButton(
    text: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = SurfaceContainer.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = OnSurface,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = text ?: "",
                    color = OnSurface,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar() {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = SurfaceContainer,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = false,
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
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = "Terminal"
                )
            },
            label = { Text("Terminal") }
        )

        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.SettingsBluetooth,
                    contentDescription = "Presenter"
                )
            },
            label = { Text("Presenter") }
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
