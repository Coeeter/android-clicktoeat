package musicpractice.com.coeeter.clicktoeat.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import musicpractice.com.coeeter.clicktoeat.R
import okhttp3.MediaType
import okhttp3.RequestBody
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

fun Activity.showKeyboard() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this.currentFocus, InputMethodManager.SHOW_FORCED)
}

fun <T> Activity.nextActivity(activity: Class<T>) {
    startActivity(
        Intent(this, activity)
    )
    overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
}

fun Activity.nextActivity(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.stay_still)
}

fun Activity.saveItemToSharedPref(sharedPrefName: String, key: String, value: Any) {
    val editor = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).edit()
    when (value) {
        is String -> editor.putString(key, value as String)
        is Int -> editor.putInt(key, value as Int)
        is Float -> editor.putFloat(key, value as Float)
        is Long -> editor.putLong(key, value as Long)
        is Boolean -> editor.putBoolean(key, value as Boolean)
        else -> editor.putString(key, Gson().toJson(value))
    }
    editor.apply()
}

fun Activity.removeItemFromSharedPref(sharedPrefName: String, vararg key: String) {
    val sharedPref = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
    key.forEach {
        if (sharedPref.contains(it)) sharedPref.edit().remove(it).apply()
    }
}

fun Activity.getStringFromSharedPref(sharedPrefName: String, key: String) =
    getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).getString(key, "")!!

fun View.isVisible(isVisible: Boolean, invisible: Int = View.GONE) {
    this.visibility = if (isVisible) View.VISIBLE else invisible
}

fun View.createSnackBar(message: String) {
    val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    snackBar.setAction("okay") {
        snackBar.dismiss()
    }.show()
}

fun String.createFormData(): RequestBody {
    return RequestBody.create(MediaType.parse("multipart/form-data"), this)
}

fun<T> Iterable<T>.every(lambda: (item: T) -> Boolean): Boolean {
    for (item in this)
        if (!lambda(item)) return false
    return true
}
