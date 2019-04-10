package c4.testing.macro

import android.os.Environment
import java.io.Serializable

/**
 * A class to store a single instant in time for all the motor powers or positions on the robot.
 * When you add a new feature to this class, you must also edit MacroRecorder#createCurrentState
 * and MacroPlayer#actState, as well as any new initializations or looping code.
 */
data class MacroState(var time: Long): Serializable {

    var motorPowerFL = 0.0
    var motorPowerFR = 0.0
    var motorPowerBL = 0.0
    var motorPowerBR = 0.0

    var extenderPower = 0.0
    var hingePosition = 0
    var spinnerPower = 0.0
    var limiterPosition = 0.0

    var liftPower = 0.0

    //TODO: Arms are unstable.  Fix this, maybe?
//    var armsPower = 0.0
//    var leftLatchPosition = 0.0
//    var rightLatchPosition = 0.0

    companion object {
        val filename = "MacroRecording.txt"
        val path = "/${Environment.getExternalStorageDirectory().path}/FIRST/"
    }
}