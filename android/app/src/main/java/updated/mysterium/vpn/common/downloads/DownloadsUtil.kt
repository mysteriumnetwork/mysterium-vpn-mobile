package updated.mysterium.vpn.common.downloads

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream

object DownloadsUtil {

    private const val MIME_TYPE = "application/json"
    private const val FILE_TITLE = "MysteriumKeystore"
    private const val FILE_NAME = "keystore"

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveWithContentResolver(
        fileContent: ByteArray,
        contentResolver: ContentResolver,
        afterSaveAction: (uri: String) -> Unit
    ) {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Downloads.TITLE, FILE_TITLE)
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, FILE_NAME)
        contentValues.put(MediaStore.Downloads.MIME_TYPE, MIME_TYPE)
        contentValues.put(MediaStore.Downloads.SIZE, fileContent.size)
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + "Mysterium")
        contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                outputStream?.write(fileContent)
                afterSaveAction.invoke(it.toString())
            }
        }
    }

    fun saveWithDownloadManager(fileContent: ByteArray, downloadManager: DownloadManager) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            FILE_NAME
        )
        if (!file.exists()) {
            file.createNewFile()
        }
        file.parentFile?.mkdirs()
        FileOutputStream(file).write(fileContent)
        downloadManager.addCompletedDownload(
            FILE_TITLE,
            FILE_NAME,
            true,
            MIME_TYPE,
            file.absolutePath,
            file.length(),
            true
        )
    }
}
