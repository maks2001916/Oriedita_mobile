package com.example.oriedita_ui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oriedita_ui.viewmodel.SettingsViewModel
import com.example.oriedita_common.resources.AndroidResourceManager
import com.example.oriedita_common.resources.ResourceConstants
import com.example.oriedita_common.resources.DrawableResources
import com.example.oriedita_common.resources.ResourceHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val resourceManager = remember { AndroidResourceManager(context) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(resourceManager.getString(ResourceConstants.MENU_SETTINGS)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = ResourceHelper.getDrawableId(context, DrawableResources.MEMORI_YOKO_IDOU)),
                            contentDescription = resourceManager.getString(ResourceConstants.BACK)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Настройки сетки
            SettingsSection(title = resourceManager.getString(ResourceConstants.SETTINGS_GRID)) {
                SettingsSwitch(
                    title = resourceManager.getString(ResourceConstants.SETTINGS_GRID_SHOW),
                    checked = viewModel.isGridVisible.collectAsState().value,
                    onCheckedChange = { viewModel.setGridVisible(it) }
                )
                SettingsSwitch(
                    title = resourceManager.getString(ResourceConstants.SETTINGS_GRID_SNAP),
                    checked = viewModel.isGridSnapEnabled.collectAsState().value,
                    onCheckedChange = { viewModel.setGridSnapEnabled(it) }
                )
                SettingsSlider(
                    title = resourceManager.getString(ResourceConstants.SETTINGS_GRID_SIZE),
                    value = viewModel.gridSize.collectAsState().value,
                    onValueChange = { viewModel.setGridSize(it) },
                    valueRange = 1f..50f,
                    steps = 49
                )
            }

            // Настройки привязки по углу
            SettingsSection(title = resourceManager.getString(ResourceConstants.SETTINGS_ANGLE_SNAP)) {
                SettingsSwitch(
                    title = resourceManager.getString(ResourceConstants.SETTINGS_ANGLE_SNAP_ENABLED),
                    checked = viewModel.isAngleSnapEnabled.collectAsState().value,
                    onCheckedChange = { viewModel.setAngleSnapEnabled(it) }
                )
                SettingsSlider(
                    title = resourceManager.getString(ResourceConstants.SETTINGS_ANGLE_SNAP_VALUE),
                    value = viewModel.angleSnapValue.collectAsState().value,
                    onValueChange = { viewModel.setAngleSnapValue(it) },
                    valueRange = 1f..90f,
                    steps = 89
                )
            }

            // Настройки отображения
            SettingsSection(title = resourceManager.getString(ResourceConstants.MENU_LINE_TYPE)) {
                SettingsSlider(
                    title = resourceManager.getString(ResourceConstants.SETTINGS_LINE_WIDTH),
                    value = viewModel.lineWidth.collectAsState().value,
                    onValueChange = { viewModel.setLineWidth(it) },
                    valueRange = 1f..10f,
                    steps = 9
                )
                SettingsSlider(
                    title = resourceManager.getString(ResourceConstants.SETTINGS_POINT_SIZE),
                    value = viewModel.pointSize.collectAsState().value,
                    onValueChange = { viewModel.setPointSize(it) },
                    valueRange = 1f..10f,
                    steps = 9
                )
            }

            // Настройки масштабирования
            SettingsSection(title = resourceManager.getString(ResourceConstants.SETTINGS_ZOOM)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.zoomIn() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = ResourceHelper.getDrawableId(context, DrawableResources.TENKAIZU_KAKUDAI)),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(resourceManager.getString(ResourceConstants.SETTINGS_ZOOM_IN))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.zoomOut() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = ResourceHelper.getDrawableId(context, DrawableResources.TENKAIZU_SYUKUSYOU)),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(resourceManager.getString(ResourceConstants.SETTINGS_ZOOM_OUT))
                    }
                }

                Button(
                    onClick = { viewModel.zoomReset() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = ResourceHelper.getDrawableId(context, DrawableResources.ZEN_SYOKIKA)),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(resourceManager.getString(ResourceConstants.SETTINGS_ZOOM_RESET))
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = title)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
        Text(
            text = value.toInt().toString(),
            style = MaterialTheme.typography.bodySmall
        )
    }
} 