package com.bluepilot.remote.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bluepilot.remote.model.ConnectionState
import com.bluepilot.remote.ui.theme.BackgroundDeep
import com.bluepilot.remote.ui.theme.BackgroundTop
import com.bluepilot.remote.ui.theme.CyanGlow
import com.bluepilot.remote.ui.theme.GlassBorderTop
import com.bluepilot.remote.ui.theme.OnPrimary
import com.bluepilot.remote.ui.theme.OnSurface
import com.bluepilot.remote.ui.theme.OnSurfaceVariant
import com.bluepilot.remote.ui.theme.OutlineVariant
import com.bluepilot.remote.ui.theme.Primary
import com.bluepilot.remote.ui.theme.PrimaryDark
import com.bluepilot.remote.ui.theme.PrimaryLight
import com.bluepilot.remote.ui.theme.SurfaceContainer
import com.bluepilot.remote.ui.theme.SurfaceContainerHigh
import com.bluepilot.remote.ui.theme.SurfaceContainerHighest
import com.bluepilot.remote.ui.theme.SurfaceContainerLow
import com.bluepilot.remote.ui.theme.Success
import com.bluepilot.remote.ui.theme.Warning
import com.bluepilot.remote.ui.theme.Error

@Composable
fun BluePilotBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundTop, BackgroundDeep)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .size(520.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(CyanGlow.copy(alpha = 0.12f), Color.Transparent),
                        radius = 520f
                    ),
                    shape = CircleShape
                )
        )
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(BorderStroke(1.dp, GlassBorderTop), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow.copy(alpha = 0.78f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            SurfaceContainer.copy(alpha = 0.72f),
                            SurfaceContainerLow.copy(alpha = 0.88f)
                        )
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
fun PrimaryGlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryDark,
            contentColor = OnPrimary,
            disabledContainerColor = SurfaceContainerHighest,
            disabledContentColor = OnSurfaceVariant
        ),
        border = BorderStroke(1.dp, PrimaryLight.copy(alpha = if (enabled) 0.45f else 0.10f))
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedButtonDefaults.outlinedButtonColors(
            containerColor = SurfaceContainer.copy(alpha = 0.35f),
            contentColor = PrimaryDark,
            disabledContentColor = OnSurfaceVariant
        ),
        border = BorderStroke(1.dp, PrimaryDark.copy(alpha = if (enabled) 0.45f else 0.12f))
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StatusPill(
    state: ConnectionState,
    label: String,
    modifier: Modifier = Modifier
) {
    val color = when (state) {
        ConnectionState.CONNECTED -> Success
        ConnectionState.CONNECTING, ConnectionState.PAIRING -> Warning
        ConnectionState.ERROR, ConnectionState.HID_UNSUPPORTED, ConnectionState.BLUETOOTH_DISABLED -> Error
        else -> OutlineVariant
    }
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = SurfaceContainerHigh.copy(alpha = 0.75f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label.uppercase(),
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = Primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(content = actions)
    }
}

@Composable
fun RemotePadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    active: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active) PrimaryDark else SurfaceContainerHighest.copy(alpha = 0.72f),
            contentColor = if (active) OnPrimary else OnSurface
        ),
        border = BorderStroke(1.dp, if (active) PrimaryLight.copy(alpha = 0.6f) else OutlineVariant.copy(alpha = 0.35f)),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
