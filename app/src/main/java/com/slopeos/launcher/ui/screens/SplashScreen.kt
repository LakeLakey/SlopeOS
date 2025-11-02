package com.slopeos.launcher.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slopeos.launcher.ui.components.GlassSurface

@Composable
fun SplashScreen(onOpenLauncher: () -> Unit) {
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050508))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Slope OS",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            GlassSurface(
                cornerPercent = 20,
                frostedAlpha = 0.25f,
                blurRadius = 24.dp,
                glow = 0.08f,
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 20))
                    .clickable {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenLauncher()
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 280.dp, height = 84.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Open Launcher",
                        color = Color(0xFF0E1116),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
