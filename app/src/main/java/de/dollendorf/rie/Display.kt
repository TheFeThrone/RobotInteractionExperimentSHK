package de.dollendorf.rie

import android.graphics.BitmapFactory
import java.io.File


class Display(private val rootPath: String) {
    fun show(filename: String) {
        val imgFile = File("$rootPath/Pictures/$filename")

        /*if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            val myImage =
            myImage.setImageBitmap(myBitmap)
        }*/
    }
}