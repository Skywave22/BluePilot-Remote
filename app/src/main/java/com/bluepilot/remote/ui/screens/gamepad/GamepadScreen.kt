@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bluepilot.remote.ui.screens.gamepad

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.VideogameAsset
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Gamepad screen with landscape-first fullscreen gaming controller
 * Supports portrait with warning message
 */
@Composable
fun GamepadScreen(
    onNavigateBack: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (isLandscape) {
            LandscapeGamepad()
        } else {
            PortraitGamepad(onNavigateBack = onNavigateBack)
        }
    }
}

@Composable
private fun PortraitGamepad(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.VideogameAsset,
            contentDescription = null,
            tint = Primary.copy(alpha = 0.6f),
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Rotate your phone for the best gamepad experience",
            color = OnSurface,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = OnPrimary
            )
        ) {
            Text("Use portrait gamepad anyway")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Back", color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun LandscapeGamepad() {
    var isLayoutLocked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Connection indicator (top center)
        ConnectionIndicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        // Layout lock button (top right)
        IconButton(
            onClick = { isLayoutLocked = !isLayoutLocked },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock Layout",
                tint = if (isLayoutLocked) Primary else OnSurfaceVariant.copy(alpha = 0.5f)
            )
        }

        // Main gamepad layout
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: L shoulder, Select/Start, Left stick, D-pad
            LeftZone()

            // Center zone: (minimal)
            Spacer(modifier = Modifier.width(48.dp))

            // Right side: R shoulder, Action buttons, Right stick
            RightZone()
        }
    }
}

@Composable
private fun ConnectionIndicator(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                color = OnSurface,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun LeftZone() {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // L shoulder button
        ShoulderButton(
            text = "L1",
            modifier = Modifier.fillMaxWidth(0.6f)
        )

        // Select and Start
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SmallButton(text = "Select")
            SmallButton(text = "Start")
        }

        // Left analog stick
        AnalogStick(
            modifier = Modifier.size(120.dp)
        )

        // D-pad
        DPad(
            modifier = Modifier.size(140.dp)
        )
    }
}

@Composable
private fun RightZone() {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // R shoulder button
        ShoulderButton(
            text = "R1",
            modifier = Modifier.fillMaxWidth(0.6f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Action buttons (A/B/X/Y in diamond layout)
        ActionButtons(
            modifier = Modifier.size(160.dp)
        )

        // Right analog stick
        AnalogStick(
            modifier = Modifier.size(120.dp)
        )
    }
}

@Composable
private fun ShoulderButton(
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Primary.copy(alpha = 0.8f)
        ),
        border = BorderStroke(2.dp, Primary.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SmallButton(
    text: String
) {
    Button(
        onClick = { },
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = OnSurfaceVariant.copy(alpha = 0.7f)
        ),
        border = BorderStroke(1.dp, OnSurfaceVariant.copy(alpha = 0.2f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun AnalogStick(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Transparent)
            .border(2.dp, Primary.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.2f))
        )
    }
}

@Composable
private fun DPad(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Center
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.1f))
        )

        // Up
        DPadButton(
            icon = "↑",
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Down
        DPadButton(
            icon = "↓",
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Left
        DPadButton(
            icon = "←",
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Right
        DPadButton(
            icon = "→",
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun DPadButton(
    icon: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier = modifier.size(40.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = OnSurfaceVariant.copy(alpha = 0.7f)
        ),
        border = BorderStroke(1.dp, OnSurfaceVariant.copy(alpha = 0.2f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun ActionButtons(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Y (top)
        ActionButton(
            text = "Y",
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // A (bottom)
        ActionButton(
            text = "A",
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // X (left)
        ActionButton(
            text = "X",
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // B (right)
        ActionButton(
            text = "B",
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier = modifier.size(48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary.copy(alpha = 0.2f),
            contentColor = Primary
        ),
        border = BorderStroke(2.dp, Primary.copy(alpha = 0.4f)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
