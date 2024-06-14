package de.dollendorf.rie.robot.activities

import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.ImageView
import androidx.core.view.isVisible
import de.dollendorf.rie.MainActivity
import kotlinx.coroutines.Runnable
import java.io.File


class Display(private val imageView: ImageView, private val mainActivity: MainActivity) {
    fun show(pictureFile: String) {
        val imgageFile = File("${Environment.getExternalStorageDirectory()}/RIE/Pictures/$pictureFile")

        if (imgageFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgageFile.absolutePath)

            mainActivity.runOnUiThread(Runnable{
                imageView.setImageBitmap(myBitmap)
                imageView.isVisible = true
            })
        }
    }

    fun blank() {
        mainActivity.runOnUiThread(Runnable{
            imageView.isVisible = false
        })
    }
}