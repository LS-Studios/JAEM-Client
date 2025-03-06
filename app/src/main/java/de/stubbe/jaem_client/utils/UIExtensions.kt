package de.stubbe.jaem_client.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.CancellationSignal
import android.util.Size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import de.stubbe.jaem_client.model.NavRoute
import de.stubbe.jaem_client.view.variables.Dimensions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import kotlin.reflect.full.createInstance


/**
 * Prüft, ob die Liste am Ende gescrollt ist
 *
 * @return true, wenn die Liste am Ende ist
 */
fun LazyListState.isScrolledToEnd(): Boolean {
    return (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= layoutInfo.totalItemsCount - 2
}

inline fun <reified T> NavBackStackEntry.isNavRouteOfType(): Boolean {
    val route = this.destination.route ?: return false

    val navRoute = NavRoute::class.sealedSubclasses.firstOrNull {
        route.contains(it.qualifiedName.toString())
    }?.let {
        it.objectInstance ?: it.createInstance()
    }

    return navRoute is T
}

fun Modifier.mirror(): Modifier {
    return this.scale(scaleX = -1f, scaleY = 1f)
}

@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGroupRoute = destination.parent?.route ?: return hiltViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGroupRoute)
    }
    return hiltViewModel<T>(parentEntry)
}

/**
 * Lädt eine Bitmap aus einer Uri
 *
 * @param context der Context
 * @return die Bitmap
 */
fun Uri.toBitmap(context: Context): Bitmap? {
    val bitmap = try {
        context.contentResolver.openInputStream(this)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    return bitmap
}

/**
 * Lädt ein Vorschaubild aus einer Datei
 *
 * @param size die Größe
 * @return das Vorschaubild
 */
suspend fun File.loadPreviewFromFile(context: Context, size: Size = Size(Dimensions.Quality.Medium, Dimensions.Quality.Medium)): Bitmap? {
    val contentResolver: ContentResolver = context.contentResolver

    val mimeType = withContext(Dispatchers.IO) {
        Files.probeContentType(this@loadPreviewFromFile.toPath())
    }

    return if (mimeType?.startsWith("video") == true) {
        this.let {
            ThumbnailUtils.createVideoThumbnail(it, size, CancellationSignal())
        }
    } else {
        contentResolver.openInputStream(Uri.fromFile(this))?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }
}