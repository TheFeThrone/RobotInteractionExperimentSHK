package de.dollendorf.rie

import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.holder.AutonomousAbilitiesType
import com.aldebaran.qi.sdk.`object`.holder.Holder
import com.aldebaran.qi.sdk.builder.HolderBuilder

class AutonomousAbilitiesToggle  {
    /*
    To hold the abilities, we first build a Holder with a HolderBuilder. We pass the autonomous abilities we want to hold to the builder, by using the AutonomousAbilitiesType enum.
    Next, we can call the async and hold methods on the holder to hold the abilities asynchronously.
    */
    private fun holdAbilities(qiContext: QiContext){
        // Build and store the holder for the abilities.
        val holder: Holder = HolderBuilder.with(qiContext)
            .withAutonomousAbilities(
                AutonomousAbilitiesType.BACKGROUND_MOVEMENT,
                AutonomousAbilitiesType.BASIC_AWARENESS,
                AutonomousAbilitiesType.AUTONOMOUS_BLINKING
            )
            .build()

        // Hold the abilities asynchronously.
        holder.async().hold()
    }

    /*
    To release autonomous abilities asynchronously, call the async and release methods on the corresponding Holder instance.
    */
    private fun releaseAbilities(holder: Holder) {
        // Release the holder asynchronously.
        holder.async().release()
    }

    fun toggleAutonomousAbilities(autonomous: Boolean, qiContext: QiContext, holder: Holder) {

        if (!autonomous) {
            holder?.let { releaseAbilities(it) }
        } else {
            holdAbilities(qiContext)
        }
    }
}