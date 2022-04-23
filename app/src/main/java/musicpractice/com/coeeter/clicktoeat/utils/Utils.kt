package musicpractice.com.coeeter.clicktoeat.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import java.io.File

fun Uri.getFile(contentResolver: ContentResolver): File {
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = contentResolver.query(this, filePathColumn, null, null, null)
    assert(cursor != null)
    cursor!!.moveToFirst()
    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
    val file = File(cursor.getString(columnIndex))
    cursor.close()
    return file

}

fun Uri.getFileType(contentResolver: ContentResolver): String {
    return contentResolver.getType(this)!!
}