package de.stubbe.jaem_client.view.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import de.stubbe.jaem_client.data.SHARE_LINK_EXPIRATION_TIME
import de.stubbe.jaem_client.model.SharedProfileModel
import de.stubbe.jaem_client.utils.getUnixTime
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

    val sharedProfiles by shareProfileViewModel.sharedProfiles.collectAsState()
    val isVisible by shareProfileViewModel.isShareProfileBottomSheetVisible.collectAsState()

    val noInternetConnection by shareProfileViewModel.noInternetConnection.collectAsState()
    val errorCreatingSharedProfile by shareProfileViewModel.errorCreatingSharedProfile.collectAsState()
    val noInternetConnectionString = stringResource(R.string.no_internet_connection)
    val errorCreatingSharedProfileString = stringResource(R.string.error_sharing_profile)

    if (isVisible) {
        val pager = rememberPagerState { sharedProfiles?.size ?: 0 }

        DisposableEffect(Unit) {
            onDispose {
                shareProfileViewModel.resetErrorFlags()
            }
        }

        LaunchedEffect(noInternetConnection) {
            if (noInternetConnection) Toast.makeText(context, noInternetConnectionString, Toast.LENGTH_SHORT).show()
        }

        LaunchedEffect(errorCreatingSharedProfile) {
            if (errorCreatingSharedProfile) Toast.makeText(context, errorCreatingSharedProfileString, Toast.LENGTH_SHORT).show()
        }

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
                sharedProfiles,
                modifier = Modifier.fillMaxWidth().padding(Dimensions.Padding.Medium),
                size = Dimensions.Size.SuperHuge,
                strokeWidth = Dimensions.Border.ThickBorder
            ) {
                val shareLinkUrls = remember {
                    sharedProfiles!!.map { sharedProfile ->
                        "$DEEP_LINK_URL/share/${sharedProfile.sharedCode}"
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (shareLinkUrls.isEmpty()) {
                        NoItemsText(stringResource(R.string.nn_server_joined))
                        return@Column
                    }
                    HorizontalPager(
                        state = pager
                    ) { page ->
                        SharedProfilePage(shareLinkUrls[page], sharedProfiles!![page])
                    }

                    if (sharedProfiles!!.size > 1) {
                        Row {
                            sharedProfiles?.indices?.forEach { index ->
                                Box(
                                    modifier = Modifier
                                        .size(Dimensions.Size.Tiny)
                                        .padding(Dimensions.Padding.Tiny)
                                        .background(
                                            if (pager.currentPage == index) JAEMThemeProvider.current.textPrimary
                                            else JAEMThemeProvider.current.textPrimary.copy(
                                                alpha = 0.4f
                                            ),
                                            CircleShape
                                        )
                                )
                            }
                        }
                    }

                    Divider()

                    ShareProfileActions(
                        onShare = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    shareLinkUrls[pager.currentPage]
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)

                            context.startActivity(shareIntent, null)
                        },
                        onCopy = {
                            clipboardManager.setText(
                                AnnotatedString(
                                    shareLinkUrls[pager.currentPage]
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SharedProfilePage(
    shareLinkUrl: String,
    sharedProfile: SharedProfileModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.Padding.Medium),
            text = sharedProfile.sharedCode,
            style = JAEMTextStyle(MaterialTheme.typography.headlineMedium).copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        )

        Divider()

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.Padding.Medium),
            text = sharedProfile.serverUrl.name,
            style = JAEMTextStyle(MaterialTheme.typography.titleMedium).copy(
                textAlign = TextAlign.Center,
            )
        )

        ShareProfileQRCode(shareLinkUrl)

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.Padding.Medium),
            text = stringResource(
                R.string.link_available_for_n_minutes,
                calculateMinutesUntilExpiration(sharedProfile.timestamp)
            ),
            style = JAEMTextStyle(MaterialTheme.typography.titleMedium).copy(
                textAlign = TextAlign.Center,
            )
        )
    }
}

fun calculateMinutesUntilExpiration(createdAt: Long): Int {
    val shareLinkExpirationTimeInMinutes = SHARE_LINK_EXPIRATION_TIME / 60
    val currentTime = getUnixTime()
    val timeDifference = currentTime - createdAt
    val timeDifferenceInMinutes = timeDifference / 60
    return (shareLinkExpirationTimeInMinutes - timeDifferenceInMinutes).toInt()
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