package c4.testing.macro

import android.os.Environment
import java.io.Serializable

class MacroState(var time: Long,
                 val motorPowerFL: Double,
                 val motorPowerFR: Double,
                 val motorPowerBL: Double,
                 val motorPowerBR: Double): Serializable {

    companion object {
        val filename = "MacroRecording.txt"
        val path = "/${Environment.getExternalStorageDirectory().path}/FIRST/"
    }
}