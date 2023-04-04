package de.dollendorf.rie

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Documentation(private val experimentFile: String) {

    private var events = ""
    private val logDateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm:ss")
    private val fileDateFormat = SimpleDateFormat("yyyyMMdd-hhmmss")

    fun createFile() {
        val file = File("${Environment.getExternalStorageDirectory()}/RIE/Logs/${experimentFile.substringBeforeLast(".")}-${fileDateFormat.format(Date())}.log")
        file.createNewFile()
        file.writeText(events)
    }

    fun addEvent(event: String) {
        val currentTime = logDateFormat.format(Date())
        events = if (events == "") {
            "[$currentTime] \"$event\""
        } else {
            "$events\n[$currentTime] \"$event\""
        }
    }
}