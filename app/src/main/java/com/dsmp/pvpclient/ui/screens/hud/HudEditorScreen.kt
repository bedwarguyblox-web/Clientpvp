package com.dsmp.pvpclient.ui.screens.hud

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsmp.pvpclient.domain.model.HudElement
import com.dsmp.pvpclient.domain.model.HudLayout
import com.dsmp.pvpclient.ui.components.DSMPTopBar
import com.dsmp.pvpclient.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun HudEditorScreen(
    onBack: () -> Unit,
    viewModel: HudEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLayoutPicker by remember { mutableStateOf(false) }

    // Auto-dismiss save success
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            kotlinx.coroutines.delay(1500)
            viewModel.clearSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            DSMPTopBar(
                title = "HUD Editor",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { showLayoutPicker = !showLayoutPicker }) {
                        Icon(Icons.Rounded.Layers, "Layouts", tint = OnSurfaceSecondary)
                    }
                    IconButton(onClick = { viewModel.resetToDefault() }) {
                        Icon(Icons.Rounded.Refresh, "Reset", tint = OnSurfaceSecondary)
                    }
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(Icons.Rounded.Save, "Save", tint = Emerald)
                    }
                }
            )
        },
        containerColor = CosmicBlack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Simulated game canvas ──────────────────────────────────────
            GameCanvas(
                elements          = uiState.elements,
                selectedElementId = uiState.selectedElementId,
                onElementMoved    = viewModel::onElementMoved,
                onElementTapped   = viewModel::onElementSelected,
                modifier          = Modifier.fillMaxSize()
            )

            // ── Layout picker strip ────────────────────────────────────────
            AnimatedVisibility(
                visible = showLayoutPicker,
                enter   = slideInVertically { -it },
                exit    = slideOutVertically { -it },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                LayoutPickerStrip(
                    layouts       = uiState.layouts,
                    activeLayout  = uiState.activeLayout,
                    onSelectLayout = { viewModel.loadLayout(it) }
                )
            }

            // ── Element inspector (bottom sheet style) ─────────────────────
            val sel = uiState.elements.firstOrNull { it.id == uiState.selectedElementId }
            AnimatedVisibility(
                visible  = sel != null,
                enter    = slideInVertically { it },
                exit     = slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                sel?.let { element ->
                    ElementInspector(
                        element             = element,
                        onScaleChange       = { viewModel.onElementScaleChanged(element.id, it) },
                        onOpacityChange     = { viewModel.onElementOpacityChanged(element.id, it) },
                        onVisibilityToggle  = { viewModel.onElementVisibilityToggled(element.id) },
                        onDismiss           = { viewModel.onElementSelected(null) }
                    )
                }
            }

            // ── Save success toast ─────────────────────────────────────────
            AnimatedVisibility(
                visible  = uiState.saveSuccess,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Surface(
                    color  = Emerald,
                    shape  = RoundedCornerShape(24.dp),
                    tonalElevation = 4.dp
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Check, null, tint = CosmicBlack, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Layout saved", color = CosmicBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ── Saving indicator ──────────────────────────────────────────
            if (uiState.isSaving) {
                Box(
                    Modifier.fillMaxSize().background(ScrimDark),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Emerald)
                }
            }
        }
    }

    // ── Save dialog ────────────────────────────────────────────────────────
    if (showSaveDialog) {
        SaveLayoutDialog(
            activeLayoutName = uiState.activeLayout?.name ?: "My Layout",
            onSaveExisting   = { viewModel.saveLayout(); showSaveDialog = false },
            onSaveNew        = { name -> viewModel.saveAsNew(name); showSaveDialog = false },
            onDismiss        = { showSaveDialog = false }
        )
    }
}

// ── Game canvas ────────────────────────────────────────────────────────────────
@Composable
private fun GameCanvas(
    elements: List<HudElement>,
    selectedElementId: String?,
    onElementMoved: (String, Float, Float) -> Unit,
    onElementTapped: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF1C2B1C)) // simulated Minecraft grass/sky tint
            .pointerInput(Unit) {
                detectTapGestures { onElementTapped(null) }
            }
    ) {
        // Subtle grid overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 40.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color       = Color.White.copy(alpha = 0.04f),
                    start       = Offset(x, 0f),
                    end         = Offset(x, size.height),
                    strokeWidth = 1f
                )
                x += step
            }
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color       = Color.White.copy(alpha = 0.04f),
                    start       = Offset(0f, y),
                    end         = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += step
            }
        }

        // Render each HUD element
        elements.forEach { element ->
            if (!element.isVisible && element.id != selectedElementId) return@forEach
            DraggableHudElement(
                element           = element,
                isSelected        = element.id == selectedElementId,
                onDragEnd         = { dx, dy -> onElementMoved(element.id, dx, dy) },
                onTap             = { onElementTapped(element.id) }
            )
        }

        // Instruction label
        Text(
            text     = "Drag elements to position them",
            style    = MaterialTheme.typography.labelSmall,
            color    = Color.White.copy(alpha = 0.3f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 180.dp)
        )
    }
}

@Composable
private fun DraggableHudElement(
    element: HudElement,
    isSelected: Boolean,
    onDragEnd: (Float, Float) -> Unit,
    onTap: () -> Unit
) {
    var currentX by remember(element.id) { mutableStateOf(element.x) }
    var currentY by remember(element.id) { mutableStateOf(element.y) }

    // Sync when external state changes (e.g. reset)
    LaunchedEffect(element.x, element.y) {
        currentX = element.x
        currentY = element.y
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
            .alpha(element.opacity)
            .scale(element.scale)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isSelected) Emerald.copy(alpha = 0.25f)
                else CosmicBlack.copy(alpha = 0.55f)
            )
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) Emerald else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .pointerInput(element.id) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentX += dragAmount.x
                        currentY += dragAmount.y
                    },
                    onDragEnd = { onDragEnd(currentX, currentY) }
                )
            }
            .pointerInput(element.id) {
                detectTapGestures { onTap() }
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text       = buildHudLabel(element),
            color      = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun buildHudLabel(el: HudElement): String =
    "${el.type.displayName}: ${el.type.defaultValue}"

// ── Layout picker strip ────────────────────────────────────────────────────────
@Composable
private fun LayoutPickerStrip(
    layouts: List<HudLayout>,
    activeLayout: HudLayout?,
    onSelectLayout: (HudLayout) -> Unit
) {
    Surface(
        color          = SurfaceDark.copy(alpha = 0.95f),
        tonalElevation = 4.dp,
        modifier       = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            contentPadding     = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(layouts) { layout ->
                val isActive = layout.id == activeLayout?.id
                FilterChip(
                    selected = isActive,
                    onClick  = { onSelectLayout(layout) },
                    label    = { Text(layout.name) },
                    leadingIcon = if (isActive) {{
                        Icon(Icons.Rounded.Check, null, Modifier.size(14.dp))
                    }} else null,
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor    = Emerald.copy(alpha = 0.2f),
                        selectedLabelColor        = Emerald,
                        selectedLeadingIconColor  = Emerald
                    )
                )
            }
        }
    }
}

// ── Element inspector ──────────────────────────────────────────────────────────
@Composable
private fun ElementInspector(
    element: HudElement,
    onScaleChange: (Float) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onVisibilityToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        color          = SurfaceCard,
        tonalElevation = 8.dp,
        shape          = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier       = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = element.type.displayName,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = OnSurfacePrimary,
                    modifier   = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, "Dismiss", tint = OnSurfaceSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Visibility toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Visible", color = OnSurfaceSecondary)
                Switch(
                    checked        = element.isVisible,
                    onCheckedChange = { onVisibilityToggle() },
                    colors         = SwitchDefaults.colors(checkedThumbColor = Emerald, checkedTrackColor = EmeraldDeep)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Scale slider
            Text("Scale  ${"%.1f".format(element.scale)}×", color = OnSurfaceSecondary,
                style = MaterialTheme.typography.bodySmall)
            Slider(
                value         = element.scale,
                onValueChange = onScaleChange,
                valueRange    = 0.5f..3.0f,
                colors        = SliderDefaults.colors(thumbColor = Emerald, activeTrackColor = Emerald)
            )

            // Opacity slider
            Text("Opacity  ${(element.opacity * 100).toInt()}%", color = OnSurfaceSecondary,
                style = MaterialTheme.typography.bodySmall)
            Slider(
                value         = element.opacity,
                onValueChange = onOpacityChange,
                valueRange    = 0.1f..1.0f,
                colors        = SliderDefaults.colors(thumbColor = Emerald, activeTrackColor = Emerald)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "Position: ${"%.0f".format(element.x)}, ${"%.0f".format(element.y)} dp",
                color = OnSurfaceTertiary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// ── Save dialog ────────────────────────────────────────────────────────────────
@Composable
private fun SaveLayoutDialog(
    activeLayoutName: String,
    onSaveExisting: () -> Unit,
    onSaveNew: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newName by remember { mutableStateOf("") }
    var showNewField by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceCard,
        title = {
            Text("Save Layout", fontWeight = FontWeight.Bold, color = OnSurfacePrimary)
        },
        text = {
            Column {
                if (!showNewField) {
                    Text(
                        "Overwrite \"$activeLayoutName\" or save as a new layout?",
                        color = OnSurfaceSecondary
                    )
                } else {
                    OutlinedTextField(
                        value         = newName,
                        onValueChange = { newName = it },
                        label         = { Text("Layout name") },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Emerald,
                            focusedLabelColor    = Emerald,
                            cursorColor          = Emerald
                        )
                    )
                }
            }
        },
        confirmButton = {
            if (!showNewField) {
                Row {
                    TextButton(onClick = onSaveExisting) {
                        Text("Overwrite", color = Emerald)
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { showNewField = true }) {
                        Text("Save new", color = AccentBlue)
                    }
                }
            } else {
                TextButton(
                    onClick  = { if (newName.isNotBlank()) onSaveNew(newName.trim()) },
                    enabled  = newName.isNotBlank()
                ) {
                    Text("Create", color = Emerald)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceSecondary)
            }
        }
    )
}

// Local Canvas import alias to avoid conflict with Compose Box
@Composable
private fun Canvas(modifier: Modifier, onDraw: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit) {
    androidx.compose.foundation.Canvas(modifier = modifier, onDraw = onDraw)
}
