package com.edureminder.easynotes.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun copyUriToInternalStorage(context: Context, uri: Uri): File? {
    return try {
        // Create images directory if it doesn't exist
        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs() // important!
        }

        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        val fileName = "img_${System.currentTimeMillis()}.jpg"
        val outputFile = File(imagesDir, fileName)

        inputStream.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        outputFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
