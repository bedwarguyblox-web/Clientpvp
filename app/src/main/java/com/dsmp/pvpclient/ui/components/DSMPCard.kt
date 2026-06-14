package com.dsmp.pvpclient.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dsmp.pvpclient.ui.theme.BorderDark
import com.dsmp.pvpclient.ui.theme.SurfaceCard

@Composable
fun DSMPCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    glowColor: Color? = null,
    borderColor: Color = BorderDark,
    borderWidth: Dp = 1.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val border = BorderStroke(
        width = borderWidth,
        color = glowColor ?: borderColor
    )

    if (onClick != null) {
        Card(
            onClick    = onClick,
            modifier   = modifier.fillMaxWidth(),
            shape      = MaterialTheme.shapes.large,
            colors     = CardDefaults.cardColors(containerColor = SurfaceCard),
            border     = border,
            elevation  = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(contentPadding)) { content() }
        }
    } else {
        Card(
            modifier  = modifier.fillMaxWidth(),
            shape     = MaterialTheme.shapes.large,
            colors    = CardDefaults.cardColors(containerColor = SurfaceCard),
            border    = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(contentPadding)) { content() }
        }
    }
}
