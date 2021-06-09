package updated.mysterium.vpn.common.downloads

import android.content.ContentResolver
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

object DownloadsUtil {

    private const val MIME_TYPE = "application/json"
    private const val FILE_TITLE = "MysteriumKeystore"
    private const val FILE_NAME = "keystore"
    private const val FILE_NAME_JSON = "keystore.json"

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
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                outputStream?.write(fileContent)
                afterSaveAction.invoke(it.toString())
            }
        }
    }

    fun saveDirectlyToDownloads(fileContent: ByteArray) {
        val dir = File(Environment.getExternalStorageDirectory().absolutePath + "/download")
        dir.mkdirs()
        val file = File(dir, FILE_NAME_JSON)
        val fileOutputStream = FileOutputStream(file)
        val printWriter = PrintWriter(fileOutputStream)
        printWriter.write(String(fileContent))
        printWriter.flush()
        printWriter.close()
        fileOutputStream.close()
    }
}
