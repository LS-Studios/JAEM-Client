package de.stubbe.jaem_client.view.components.filepicker

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider

@Composable
fun JAEMPickFileAndCrop(
    onDismiss: () -> Unit,
    selected: (Uri) -> Unit,
) {
    var dismissWhenOnResume by remember { mutableStateOf(false) }

    val imageCropLauncher = rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
            if (result.isSuccessful && result.uriContent != null) {
                selected(result.uriContent!!)
            } else {
                println("ImageCropping error: ${result.error}")
            }
        }

    val borderColor = JAEMThemeProvider.current.border.toArgb()
    val textColor = JAEMThemeProvider.current.textPrimary.toArgb()
    val bgColor = JAEMThemeProvider.current.background.toArgb()

    val pickMultipleMedia =  rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageCropLauncher.launch(CropImageContractOptions(
                uri,
                CropImageOptions(
                    cropShape = CropImageView.CropShape.OVAL,
                    guidelines = CropImageView.Guidelines.ON,
                    fixAspectRatio = true,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    borderLineColor = borderColor,
                    borderCornerColor = textColor,
                    guidelinesColor = textColor,
                    activityMenuIconColor = textColor,
                    activityMenuTextColor = textColor,
                    activityBackgroundColor = bgColor,
                    progressBarColor = textColor,
                )
            ))
        }
    }

    LaunchedEffect(Unit) {
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}