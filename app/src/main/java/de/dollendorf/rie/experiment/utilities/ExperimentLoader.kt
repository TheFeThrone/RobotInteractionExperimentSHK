package de.dollendorf.rie.experiment.utilities

import de.dollendorf.rie.utilities.JsonLoader
import java.io.FileNotFoundException

class ExperimentLoader(path: String) : JsonLoader(path) {

    fun loadExperiment() {
        try {
            super.loadData()
        }
        catch (e: FileNotFoundException) {
            println("Failed to load experiment file.")
        }
    }

}