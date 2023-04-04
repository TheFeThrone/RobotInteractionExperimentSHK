package de.dollendorf.rie

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Documentation(private val experimentFile: String) {

    private var events = ""
    private val logDateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm:ss")
    private val fileDateFormat = SimpleDateFormat("yyyyMMdd-hhmmss")
    private var currentFile = ""

    fun createFile() {
        val file = File("${Environment.getExternalStorageDirectory()}/RIE/Logs/${experimentFile.substringBeforeLast(".")}-${fileDateFormat.format(Date())}.log")
        file.createNewFile()
        currentFile = file.name
        file.writeText(events)
        events = ""
    }

    fun addEvent(event: String) {
        val currentTime = logDateFormat.format(Date())
        events = if (events == "") {
            currentFile = ""
            "[$currentTime] \"$event\""
        } else {
            "$events\n[$currentTime] \"$event\""
        }
    }

    fun getCurrentFileName(): String {
        return currentFile
    }
}