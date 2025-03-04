package de.stubbe.jaem_client.view.components.filepicker

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.model.JAEMFileType
import de.stubbe.jaem_client.utils.OnResume
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JAEMFilePicker(
    maxSelection: Int,
    types: List<JAEMFileType>,
    onDismiss: () -> Unit,
    selected: (JAEMFileType?, List<Uri>) -> Unit,
) {
    val context = LocalContext.current

    var dismissWhenOnResume by remember { mutableStateOf(false) }
    var isPermissionGrant by remember { mutableStateOf(false) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var dialogAlpha by remember { mutableFloatStateOf(0f) }

    val isPhotoPickerAvailable by remember { mutableStateOf(isPhotoPickerAvailable(context)) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showPickerDialog by remember(types) { mutableStateOf(types.size > 1) }

    var selectedType: JAEMFileType? by remember(showPickerDialog, types) { mutableStateOf(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { grantResults ->
            if (grantResults.all { it.value }) {
                isPermissionGrant = true
                Log.d("permissionLauncher", "PickerDialog: $isPermissionGrant")
            }
        }
    )

    val pickMultipleMedia = if (maxSelection > 1) {
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickMultipleVisualMedia(maxSelection)) { uris ->
            if (uris.isNotEmpty()) {
                selected(selectedType, uris)
            }
        }
    } else {
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selected(selectedType, listOf(uri))
            }
        }
    }

    val pickFile = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            selected(selectedType, listOf(uri))
        }
    }

    OnResume {
        if (dismissWhenOnResume) onDismiss()
    }

    LaunchedEffect(types, showPickerDialog) {
        if (selectedType == null && !showPickerDialog) {
            selectedType = types.firstOrNull()
        }
    }

    LaunchedEffect(selectedType) {
        dialogAlpha = when (selectedType) {
            JAEMFileType.CAMERA -> 0f
            JAEMFileType.IMAGE_AND_VIDEO -> if (isPhotoPickerAvailable) 0f else 1f
            JAEMFileType.IMAGE -> if (isPhotoPickerAvailable) 0f else 1f
            JAEMFileType.STORAGE -> 0f
            null -> if (showPickerDialog && selectedType == null) 1f else 0f
        }
    }

    ModalBottomSheet(
        modifier = Modifier.alpha(dialogAlpha),
        onDismissRequest = { onDismiss() },
        containerColor = JAEMThemeProvider.current.background,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = JAEMThemeProvider.current.textSecondary
            )
        },
        sheetState = sheetState,
    ) {
        if (showPickerDialog && selectedType == null) {
            PickFileTypeContent(
                types = types,
                onSelected = { selectedType = it }
            )
        } else {
            when (selectedType) {
                JAEMFileType.CAMERA -> {
//                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
//                        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.Camera))
//                        dismissWhenOnResume = true
//                    }
                }

                JAEMFileType.IMAGE_AND_VIDEO -> {
                    if (isPhotoPickerAvailable) {
                        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            Log.d("TAG", "PickerDialog: ImageAndVideo")
                            pickMultipleMedia.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                                )
                            )
                            dismissWhenOnResume = true
                        }
                    } else {
                        permissionLauncher.launch(
                            selectedType!!.getPermissions().toTypedArray()
                        )
                    }
                }

                JAEMFileType.IMAGE -> {
                    if (isPhotoPickerAvailable) {
                        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            pickMultipleMedia.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                            dismissWhenOnResume = true
                        }
                    } else {
                        permissionLauncher.launch(
                            selectedType!!.getPermissions().toTypedArray()
                        )
                    }
                }

                JAEMFileType.STORAGE -> {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        pickFile.launch(arrayOf("*/*"))
                        dismissWhenOnResume = true
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun PickFileTypeContent(
    types: List<JAEMFileType>,
    onSelected: (JAEMFileType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        types.forEach { type ->
            PickFileTypeItem(
                icon = when(type) {
                    JAEMFileType.CAMERA -> Icons.Default.CameraAlt
                    JAEMFileType.IMAGE_AND_VIDEO -> Icons.Default.Photo
                    JAEMFileType.IMAGE -> Icons.Default.Image
                    JAEMFileType.STORAGE -> Icons.Default.Folder
                },
                title = when(type) {
                    JAEMFileType.CAMERA -> stringResource(R.string.camera)
                    JAEMFileType.IMAGE_AND_VIDEO -> stringResource(R.string.image_and_video)
                    JAEMFileType.IMAGE -> stringResource(R.string.image)
                    JAEMFileType.STORAGE -> stringResource(R.string.storage)
                },
                onClick = { onSelected(type) }
            )
        }
    }
}

@Composable
private fun RowScope.PickFileTypeItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = null,
                indication = ripple(
                    bounded = true
                )
            ) {
                onClick()
            }
            .padding(
                horizontal = Dimensions.Padding.Medium,
                vertical = Dimensions.Padding.Small
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically)
    ) {
        Icon(
            modifier = Modifier
                .background(
                    JAEMThemeProvider.current.primary,
                    shape = CircleShape
                )
                .padding(Dimensions.Padding.Medium),
            imageVector = icon,
            contentDescription = null,
            tint = JAEMThemeProvider.current.textPrimary
        )

        Text(
            text = title,
            style = JAEMTextStyle(MaterialTheme.typography.bodyMedium,)
        )
    }
}