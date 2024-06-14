package de.dollendorf.rie.utilities

import android.os.Environment
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset

open class JsonLoader(path: String) {

    private val file = File("${Environment.getExternalStorageDirectory()}/RIE/$path")
    private lateinit var fileContent: String
    private lateinit var items: JsonElement

    protected fun loadData() {
        try {
            fileContent = file.readText(Charset.defaultCharset())
            items = Json.parseToJsonElement(fileContent)
        }
        catch (e: Exception) {
            throw FileNotFoundException()
        }
    }

    fun getElement(elementPath: String): String? {
        return try {
            val keys = elementPath.split("/")
            var item: JsonElement = items
            for (key in keys) {
                item = item.jsonObject[key]!!
            }
            item.toString().replace("\"", "")
        } catch (e: Exception) {
            null
        }
    }

    fun writeData(data: String) {
        items = Json.parseToJsonElement(data)
        if (!file.exists()) {
            File(file.path.substringBeforeLast("/")).mkdirs()
            file.createNewFile()
        }
        file.writeText(data)
        fileContent = file.readText(Charset.defaultCharset())
    }

    fun getFullData(): String {
        return fileContent
    }
}