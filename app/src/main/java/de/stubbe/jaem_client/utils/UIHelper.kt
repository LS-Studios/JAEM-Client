package de.stubbe.jaem_client.utils

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Prüft, ob die Tastatur sichtbar ist
 *
 * @return true, wenn die Tastatur sichtbar ist
 */
@Composable
fun keyboardVisibility(): State<Boolean> {
    val keyboardVisibilityState = rememberSaveable { mutableStateOf(false) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardVisibilityState.value = keypadHeight > screenHeight * 0.15
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }
    return keyboardVisibilityState
}

/**
 * Fügt zusätzliche Parameter für die Erstellung eines ViewModels hinzu
 *
 * @param viewModelStoreOwner Der ViewModelStoreOwner, der die ViewModels erstellt
 * @param builder Die Funktion, die die zusätzlichen Parameter hinzufügt
 * @return Die zusätzlichen Parameter
 */
@Composable
fun addViewModelExtras(
    viewModelStoreOwner: ViewModelStoreOwner = LocalViewModelStoreOwner.current ?: error("No ViewModelStoreOwner found"),
    builder: MutableCreationExtras.() -> Unit
): CreationExtras {
    val defaultExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    }

    return MutableCreationExtras(defaultExtras).apply(builder)
}

/**
 * Erstellt einen QR-Code als BitmapPainter
 *
 * @param content Der Inhalt des QR-Codes
 * @param size Die Größe des QR-Codes
 * @param padding Der Abstand des QR-Codes
 * @param codeColor Die Farbe des Codes
 * @param backgroundColor Die Hintergrundfarbe
 * @return Der QR-Code als BitmapPainter
 */
@Composable
fun rememberQrBitmapPainter(
    content: String,
    size: Dp = 150.dp,
    padding: Dp = 0.dp,
    codeColor: Color = Color.Black,
    backgroundColor: Color = Color.White
): BitmapPainter {

    val density = LocalDensity.current
    val sizePx = with(density) { size.roundToPx() }
    val paddingPx = with(density) { padding.roundToPx() }

    var bitmap by remember(content) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(bitmap) {
        if (bitmap != null) return@LaunchedEffect

        launch(Dispatchers.IO) {
            val qrCodeWriter = QRCodeWriter()

            val encodeHints = mutableMapOf<EncodeHintType, Any?>()
                .apply {
                    this[EncodeHintType.MARGIN] = paddingPx
                }

            println(content)

            val bitmapMatrix = try {
                qrCodeWriter.encode(
                    content, BarcodeFormat.QR_CODE,
                    sizePx, sizePx, encodeHints
                )
            } catch (ex: WriterException) {
                null
            }

            val matrixWidth = bitmapMatrix?.width ?: sizePx
            val matrixHeight = bitmapMatrix?.height ?: sizePx

            val newBitmap = Bitmap.createBitmap(
                bitmapMatrix?.width ?: sizePx,
                bitmapMatrix?.height ?: sizePx,
                Bitmap.Config.ARGB_8888,
            )

            for (x in 0 until matrixWidth) {
                for (y in 0 until matrixHeight) {
                    val shouldColorPixel = bitmapMatrix?.get(x, y) ?: false
                    val pixelColor = if (shouldColorPixel) codeColor.toArgb() else backgroundColor.toArgb()

                    newBitmap.setPixel(x, y, pixelColor)
                }
            }

            bitmap = newBitmap
        }

    }

    return remember(bitmap) {
        val currentBitmap = bitmap ?: Bitmap.createBitmap(
            sizePx, sizePx,
            Bitmap.Config.ARGB_8888,
        ).apply { eraseColor(Color.Transparent.toArgb()) }

        BitmapPainter(currentBitmap.asImageBitmap())
    }
}

/**
 * Führt eine Aktion aus, wenn ein Lifecycle-Event auftritt
 */
@Composable
internal fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun OnResume(callBack: () -> Unit) {
    OnLifecycleEvent { owner, event ->
        if (event == Lifecycle.Event.ON_RESUME) callBack()
    }
}
@Composable
fun OnPause(callBack: () -> Unit) {
    OnLifecycleEvent { owner, event ->
        if (event == Lifecycle.Event.ON_PAUSE) callBack()
    }
}