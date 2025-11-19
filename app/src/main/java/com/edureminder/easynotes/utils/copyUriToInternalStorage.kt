package com.edureminder.easynotes.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun copyUriToInternalStorage(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val directory = File(context.filesDir, "images")
        if (!directory.exists()) directory.mkdirs()

        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val outFile = File(directory, fileName)

        inputStream.use { input ->
            FileOutputStream(outFile).use { output ->
                input.copyTo(output) // <-- NO compression, original bytes preserved
            }
        }

        outFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
