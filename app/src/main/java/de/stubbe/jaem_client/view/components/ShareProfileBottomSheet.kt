package de.stubbe.jaem_client.view.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import de.stubbe.jaem_client.R
import de.stubbe.jaem_client.data.DEEP_LINK_URL
import de.stubbe.jaem_client.data.JAEMTextStyle
import de.stubbe.jaem_client.utils.rememberQrBitmapPainter
import de.stubbe.jaem_client.view.variables.Dimensions
import de.stubbe.jaem_client.view.variables.JAEMThemeProvider
import de.stubbe.jaem_client.viewmodel.ShareProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareProfileBottomSheet(
    onClose: () -> Unit,
    shareProfileViewModel: ShareProfileViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val sharedProfile by shareProfileViewModel.sharedProfile.collectAsState()
    val profileToShare by shareProfileViewModel.profileToShare.collectAsState()
    val isVisible by shareProfileViewModel.isShareProfileBottomSheetVisible.collectAsState()

    val shareLinkUrl = remember(sharedProfile) {
        if (sharedProfile == null) {
            null
        } else {
            "$DEEP_LINK_URL/share/${sharedProfile?.sharedCode}"
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onClose()
            },
            containerColor = JAEMThemeProvider.current.background,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = JAEMThemeProvider.current.textSecondary
                )
            },
        ) {
            LoadingIfNull(
                profileToShare,
                sharedProfile,
                modifier = Modifier.fillMaxWidth().padding(Dimensions.Padding.Medium),
                size = Dimensions.Size.SuperHuge,
                strokeWidth = Dimensions.Border.ThickBorder
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimensions.Padding.Medium),
                        text = sharedProfile?.sharedCode ?: "",
                        style = JAEMTextStyle(MaterialTheme.typography.headlineMedium).copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Divider()

                    if (shareLinkUrl != null) {
                        ShareProfileQRCode(shareLinkUrl)

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimensions.Padding.Medium),
                            text = stringResource(
                                R.string.link_available_for_n_minutes,
                                calculateMinutesUntilExpiration(
                                    sharedProfile!!.timestamp,
                                    10
                                )
                            ),
                            style = JAEMTextStyle(MaterialTheme.typography.titleMedium).copy(
                                textAlign = TextAlign.Center,
                            )
                        )
                    }

                    Divider()

                    ShareProfileActions(
                        onShare = {
                            if (sharedProfile != null) {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        shareLinkUrl
                                    )
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)

                                context.startActivity(shareIntent, null)
                            }
                        },
                        onCopy = {
                            if (sharedProfile != null) {
                                clipboardManager.setText(
                                    AnnotatedString(
                                        shareLinkUrl ?: ""
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

fun calculateMinutesUntilExpiration(createdAt: Long, maxMinutesUntilExpiration: Int): Int {
    val currentTime = System.currentTimeMillis()
    val timeDifference = currentTime - createdAt
    val minutesDifference = timeDifference / 1000 / 60

    return maxMinutesUntilExpiration - minutesDifference.toInt()
}

@Composable
fun ShareProfileQRCode(shareLink: String) {
    Image(
        modifier = Modifier.size(Dimensions.Size.SuperHuge),
        painter = rememberQrBitmapPainter(
            content = shareLink,
            size = Dimensions.Size.SuperHuge,
            codeColor = JAEMThemeProvider.current.textPrimary,
            backgroundColor = JAEMThemeProvider.current.background
        ),
        contentDescription = stringResource(R.string.key_qr_code),
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
private fun ShareProfileActions(
    onShare: () -> Unit,
    onCopy: () -> Unit
) {
    Row {
        ShareProfileActionButton(
            text = stringResource(R.string.share),
            onClick = onShare
        )
        ShareProfileActionButton(
            text = stringResource(R.string.copy),
            onClick = onCopy
        )
    }
}

@Composable
private fun RowScope.ShareProfileActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .padding(
                horizontal = Dimensions.Padding.Medium,
                vertical = Dimensions.Padding.Small
            ),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = JAEMThemeProvider.current.primary
        ),
        shape = Dimensions.Shape.Rounded.Small,
        border = BorderStroke(
            width = Dimensions.Border.ThinBorder,
            color = JAEMThemeProvider.current.border
        )
    ) {
        Text(
            text = text,
            style = JAEMTextStyle(MaterialTheme.typography.bodyMedium)
        )
    }
}

/*@Composable
private fun KeyMatrix(key: String) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.Padding.Medium)
            .background(
                color = JAEMThemeProvider.current.primary,
                shape = Dimensions.Shape.Rounded.Small
            )
            .border(
                color = JAEMThemeProvider.current.border,
                width = Dimensions.Border.ThinBorder,
                shape = Dimensions.Shape.Rounded.Small
            )
            .padding(
                vertical = Dimensions.Padding.Medium
            ),
        columns = GridCells.Fixed(8),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium),
    ) {
        items(key.toList()) { char ->
            Text(
                text = char.toString(),
                style = JaemTextStyle(MaterialTheme.typography.titleLarge).copy(
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}*/