package com.slopeos.launcher.ui.screens

import android.app.Activity
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slopeos.launcher.data.AppEntry
import com.slopeos.launcher.data.AppRepository
import com.slopeos.launcher.data.SettingsDataStore
import com.slopeos.launcher.ui.components.GlassSurface
import com.slopeos.launcher.util.toImageBitmap
import com.slopeos.launcher.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {
    val ctx = LocalContext.current
    val vm: HomeViewModel = viewModel(factory = viewModelFactory {
        initializer { HomeViewModel(ctx.applicationContext) }
    })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { vm.load() }

    val allApps by vm.allApps.collectAsState()
    val pinned by vm.pinned.collectAsState(initial = emptySet())
    val unpinned by vm.unpinned.collectAsState(initial = emptyList())

    var showDrawer by remember { mutableStateOf(false) }
    var showNotif by remember { mutableStateOf(false) }
    var showQuick by remember { mutableStateOf(false) }
    var longPressedName by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val haptics = LocalHapticFeedback.current

    // Settings store
    val store = remember { SettingsDataStore(ctx) }
    val blurDp by store.blurFlow.collectAsState(initial = 18f)
    val opacity by store.opacityFlow.collectAsState(initial = 0.35f)

    // Tunables (bound to DataStore)
    val cornerPercent = 20
    val blur = blurDp.dp
    val frostedAlpha = opacity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(top = 36.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
            .combinedClickable(onClick = {}, onLongClick = { showSettings = true })
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                onLeft = { showNotif = true; haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
                onRight = { showQuick = true; haptics.performHapticFeedback(HapticFeedbackType.LongPress) }
            )
            Spacer(Modifier.height(12.dp))

            // 5 x 7 grid
            val homeApps = remember(allApps, pinned) {
                allApps.filter { pinned.contains(it.packageName) }
            }

            LazyVerticalGrid(
                modifier = Modifier.weight(1f),
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(6.dp)
            ) {
                items(homeApps, key = { it.packageName }) { app ->
                    AppIcon(
                        entry = app,
                        cornerPercent = cornerPercent,
                        frostedAlpha = frostedAlpha,
                        blur = blur,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            vm.openApp(app)
                        },
                        onLongPress = {
                            longPressedName = app.label
                        },
                        onRemoveFromHome = { vm.unpin(app.packageName) },
                        onInfo = { vm.showAppInfo(app) }
                    )
                }
            }

            Dock(
                onCenter = { showDrawer = !showDrawer; haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
                left = homeApps.take(2),
                right = homeApps.takeLast(2),
                open = { vm.openApp(it) },
                cornerPercent = cornerPercent,
                frostedAlpha = frostedAlpha,
                blur = blur
            )
        }

        // App drawer bubble
        AnimatedVisibility(
            visible = showDrawer,
            enter = scaleIn(spring(stiffness = Spring.StiffnessMediumLow)),
            exit = scaleOut(spring(stiffness = Spring.StiffnessMedium))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassSurface(cornerPercent = 20, frostedAlpha = 0.22f, blurRadius = 24.dp, glow = 0.08f) {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(560.dp),
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        items(unpinned, key = { it.packageName }) { app ->
                            AppIcon(
                                entry = app,
                                cornerPercent = cornerPercent,
                                frostedAlpha = 0.28f,
                                blur = 16.dp,
                                onClick = { vm.openApp(app) },
                                onLongPress = { longPressedName = app.label },
                                onRemoveFromHome = {},
                                onInfo = { vm.showAppInfo(app) },
                                allowPin = true,
                                onPin = { vm.pin(app.packageName) }
                            )
                        }
                    }
                }
            }
        }

        // Notifications drawer
        AnimatedVisibility(visible = showNotif, enter = fadeIn(), exit = fadeOut()) {
            FloatingPanel(title = "Notifications", onDismiss = { showNotif = false }) {
                Text("Grant notification access in system settings to show notifications.", color = Color.White.copy(alpha = 0.8f))
                Spacer(Modifier.height(8.dp))
                Text("Settings > Notifications > Notification access", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }

        // Quick settings
        AnimatedVisibility(visible = showQuick, enter = fadeIn(), exit = fadeOut()) {
            FloatingPanel(title = "Home Center", onDismiss = { showQuick = false }) {
                Text("Brightness", color = Color.White)
                Slider(value = 0.8f, onValueChange = { /* local-only demo */ })
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickToggle("Wi‑Fi") { openSystemPanel(ctx as Activity, "android.settings.panel.action.INTERNET_CONNECTIVITY") }
                    QuickToggle("Bluetooth") { openSystemPanel(ctx as Activity, "android.settings.panel.action.BLUETOOTH") }
                    QuickToggle("Volume") { openSystemPanel(ctx as Activity, "android.settings.panel.action.VOLUME") }
                }
            }
        }

        // Long-press name bubble
        AnimatedVisibility(visible = longPressedName != null, enter = fadeIn(), exit = fadeOut()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GlassSurface(cornerPercent = 20, frostedAlpha = 0.2f, blurRadius = 12.dp, glow = 0.06f) {
                    Text(longPressedName ?: "", modifier = Modifier.padding(12.dp), color = Color.White)
                }
            }
        }

        // Slope Settings Panel
        AnimatedVisibility(visible = showSettings, enter = fadeIn(), exit = fadeOut()) {
            FloatingPanel(title = "Slope Settings", onDismiss = { showSettings = false }) {
                Text("Icon size", color = Color.White)
                // Placeholder slider for future icon size binding
                Slider(value = 1.0f, onValueChange = { /* TODO bind */ }, valueRange = 0.8f..1.4f)
                Spacer(Modifier.height(6.dp))
                Text("Blur intensity", color = Color.White)
                Slider(value = blurDp, onValueChange = { scope.launch { store.setBlur(it) } }, valueRange = 8f..36f)
                Spacer(Modifier.height(6.dp))
                Text("Glass opacity", color = Color.White)
                Slider(value = opacity, onValueChange = { scope.launch { store.setOpacity(it) } }, valueRange = 0.15f..0.5f)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickToggle("Vibration") { /* toggle via DataStore if needed */ }
                    QuickToggle("Change wallpaper") {
                        try { (ctx as Activity).startActivity(android.content.Intent(android.app.WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)) } catch (_: Exception) {
                            try { (ctx as Activity).startActivity(android.content.Intent(android.app.WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)) } catch (_: Exception) {}
                        }
                    }
                    QuickToggle("Reset") { scope.launch { store.reset() } }
                }
            }
        }
    }
}

@Composable
private fun TopBar(onLeft: () -> Unit, onRight: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val date = remember { java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEE, MMM d")) }
        Text(text = date, color = Color.White.copy(alpha = 0.9f), modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            GlassButton(label = "Left", onClick = onLeft)
            GlassButton(label = "Right", onClick = onRight)
        }
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            Text("100%  b  b ", color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable private fun GlassButton(label: String, onClick: () -> Unit) {
    GlassSurface(cornerPercent = 20, frostedAlpha = 0.18f, blurRadius = 12.dp, glow = 0.08f,
        modifier = Modifier.clip(RoundedCornerShape(percent = 20)).clickable { onClick() }) {
        Text(label, color = Color.White, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
    }
}

@Composable
private fun Dock(
    onCenter: () -> Unit,
    left: List<AppEntry>,
    right: List<AppEntry>,
    open: (AppEntry) -> Unit,
    cornerPercent: Int,
    frostedAlpha: Float,
    blur: Dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            left.forEach { entry ->
                AppIcon(entry, cornerPercent, frostedAlpha, blur, onClick = { open(entry) })
            }
        }
        Box(contentAlignment = Alignment.Center) {
            GlassSurface(cornerPercent = 50, frostedAlpha = 0.22f, blurRadius = 18.dp, glow = 0.08f,
                modifier = Modifier.size(64.dp).clip(CircleShape).clickable { onCenter() }) {
                Text("⋯", color = Color.White, fontSize = 24.sp, modifier = Modifier.align(Alignment.Center))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            right.forEach { entry ->
                AppIcon(entry, cornerPercent, frostedAlpha, blur, onClick = { open(entry) })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppIcon(
    entry: AppEntry,
    cornerPercent: Int,
    frostedAlpha: Float,
    blur: Dp,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {},
    onRemoveFromHome: () -> Unit = {},
    onInfo: () -> Unit = {},
    allowPin: Boolean = false,
    onPin: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val pm = remember { AppRepository(ctx) }
    val icon = remember(entry) { pm.getAppIcon(entry) }
    var menu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { menu = true; onLongPress() }
            )
    ) {
        GlassSurface(cornerPercent = cornerPercent, frostedAlpha = frostedAlpha, blurRadius = blur, glow = 0.06f,
            modifier = Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                icon?.let {
                    androidx.compose.foundation.Image(bitmap = it.toImageBitmap(), contentDescription = entry.label, modifier = Modifier.size(36.dp))
                }
            }
        }

        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
            if (allowPin) {
                DropdownMenuItem(text = { Text("Add to Home") }, onClick = { onPin(); menu = false })
            } else {
                DropdownMenuItem(text = { Text("Remove from Home") }, onClick = { onRemoveFromHome(); menu = false })
            }
            DropdownMenuItem(text = { Text("App Info") }, onClick = { onInfo(); menu = false })
        }
    }
}

@Composable private fun FloatingPanel(title: String, onDismiss: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.matchParentSize().clickable { onDismiss() })
        Box(modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 72.dp)
        ) {
            GlassSurface(cornerPercent = 20, frostedAlpha = 0.20f, blurRadius = 24.dp, glow = 0.08f) {
                Column(Modifier.widthIn(max = 520.dp).padding(16.dp)) {
                    Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    content()
                }
            }
        }
    }
}

@Composable private fun QuickToggle(label: String, onClick: () -> Unit) {
    GlassSurface(cornerPercent = 20, frostedAlpha = 0.18f, blurRadius = 10.dp, glow = 0.06f,
        modifier = Modifier.clip(RoundedCornerShape(percent = 20)).clickable { onClick() }) {
        Text(label, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

private fun openSystemPanel(activity: Activity, action: String) {
    try {
        val intent = android.content.Intent(action)
        activity.startActivity(intent)
    } catch (_: Exception) {}
}
