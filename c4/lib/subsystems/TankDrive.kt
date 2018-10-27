package c4.lib.subsystems

import c4.lib.C4PropFile
import c4.lib.pctError
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.PIDCoefficients

class TankDrive(lop: LinearOpMode? = null, opm: OpMode): SubSystem(lop, opm) {

    private lateinit var lMotor: DcMotor
    private lateinit var rMotor: DcMotor

    override fun init() {
        lMotor = opm.hardwareMap.dcMotor.get("drive_left")
        rMotor = opm.hardwareMap.dcMotor.get("drive_right")

        lMotor.direction = DcMotorSimple.Direction.FORWARD
        rMotor.direction = DcMotorSimple.Direction.REVERSE
    }

    override fun loop() {
        lMotor.power = -opm.gamepad1.left_stick_y.toDouble()
        rMotor.power = -opm.gamepad1.right_stick_y.toDouble()
    }

    /**
     * Turn a set number of degrees via dead reckoning and motor encoders.  Left is negative, right
     * is positive
     * @param deg the desired change in position in degrees
     */
    @AutoMethod fun turnDead(deg: Int) {
        //TODO: Test this
        val coef = C4PropFile.getInt("turningCoefficient")
        //While there is a targetPosition property on motors, it should not be used.  We can't stop
        //the robot while that is being used, so I've decided not to use it.
        var lPos = lMotor.currentPosition
        var rPos = rMotor.currentPosition
        val lTarget = lPos + coef * deg
        val rTarget = rPos - coef * deg

        val accuracy = 40

        while(pctError(lTarget, lMotor.currentPosition) > accuracy || pctError(rTarget, rMotor.currentPosition) > accuracy) {

        }
    }

    /**
     * Go forward a number of centimeters via dead reckoning and motor encoders.  Positive cm is
     * forward, negative is backwards
     */
    @AutoMethod fun forwardDead(cm: Int) {
        //TODO: Test this
        val coef = C4PropFile.getInt("forwardCoefficient")

        lMotor.targetPosition = lMotor.currentPosition + cm * coef
        rMotor.targetPosition = rMotor.currentPosition + cm * coef
    }

}