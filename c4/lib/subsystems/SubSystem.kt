package c4.lib.subsystems

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import java.lang.Exception

/**
 * Bare bones subsystem class.
 * You should *not* inject any more variables in this class.  They can be accessed via opm or from a
 * static context.
 */
abstract class SubSystem(val opm: OpMode) {

    /**
     * You should initialize this in autonomous.  The linearopmode object so we can access its state
     * (i.e., if it is canceled)
     */
    var lop: LinearOpMode? = null

    /**
     * This should initialize all hardware on the robot, both from the perspective of software (e.g.
     * hardware mapping, etc.) and physically adjusting the parts to initial positions.
     */
    abstract fun init()

    /**
     * If there is anything different about the teleop init, you should use this class.  For example,
     * if an arm is already unretractably extended in autonomous, you may want to override this.
     */
    @TeleMethod fun teleInit() { init() }

    /**
     * This should be run every frame of the teleop loop.  It should contain controls, for example.
     * Never call from autonomous.  Even if you don't use your subsytem from teleop, you should still
     * lock the hardware in place.
     */
    @TeleMethod abstract fun loop()

    /**
     * This should be called in *all* autonomous methods to allow for cancelation.  Even in
     * initialization.
     */
    @AutoMethod fun checkOpModeCancel() {
        //This case should never run, but I'll probably misuse this at least once tbh
        if(lop == null) return
        if(lop!!.isStopRequested) throw OpModeStopException()
    }

    /**
     * This solves a problem we had in year 17-18.  In the actual autonomous program, our code should be
     * surrounded by a try...catch clause catching this. See {@link SubSystem#opModeIsActive()}
     */
    class OpModeStopException: Exception()

    /**
     * Decorative annotation to mark solely autonomous methods
     */
    @Retention(AnnotationRetention.SOURCE)
    protected annotation class AutoMethod

    /**
     * Decorative annotation to mark solely teleop methods
     */
    @Retention(AnnotationRetention.SOURCE)
    protected annotation class TeleMethod
}