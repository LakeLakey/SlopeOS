package com.slopeos.launcher.ui.components

import android.os.Build
import android.view.RenderEffect
import android.view.Shader
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerPercent: Int = 20,
    frostedAlpha: Float = 0.35f,
    blurRadius: Dp = 18.dp,
    glow: Float = 0.12f,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(percent = cornerPercent)
    val animatedAlpha by animateFloatAsState(
        targetValue = frostedAlpha,
        animationSpec = spring(stiffness = Spring.StiffnessLow), label = "alpha"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Approximate frosted glass: local blur + translucent fill + glow
                    Modifier
                        .blur(blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                } else Modifier
            )
            .background(Color.White.copy(alpha = animatedAlpha))
            .drawBehind {
                // Subtle inner glow
                drawRect(Color.White.copy(alpha = glow))
            }
            .shadow(elevation = 8.dp, shape = shape, clip = false)
            .padding(12.dp)
    ) {
        content()
    }
}
