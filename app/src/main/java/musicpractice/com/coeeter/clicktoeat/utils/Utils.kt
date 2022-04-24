package musicpractice.com.coeeter.clicktoeat.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
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

fun Activity.hideKeyboard() {
    try {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
    } catch (error: Exception) {
        Log.d("poly", error.message.toString())
        error.printStackTrace()
    }
}

fun View.isVisible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

fun View.createSnackBar(message: String) {
    val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    snackBar.setAction("okay") {
        snackBar.dismiss()
    }.show()
}