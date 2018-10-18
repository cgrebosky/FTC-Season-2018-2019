package c4.lib.subsystems

import c4.lib.C4PropFile
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

class TankDrive(opm: OpMode): SubSystem(opm = opm) {

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
     * Turn a set number of degrees.  Left is negative, right is positive
     * @param deg the desired change in position in degrees
     */
    @AutoMethod fun turnDegrees(deg: Int) {
        //TODO: Test this
        val coef = C4PropFile.getInt("turningCoefficient")

        lMotor.targetPosition = lMotor.currentPosition + coef * deg
        rMotor.targetPosition = rMotor.currentPosition - coef * deg
    }

    /**
     * Go forward a number of centimeters.  Positive cm is forward, negative is backwards
     */
    @AutoMethod fun forwardCm(cm: Int) {
        //TODO: Test this
        val coef = C4PropFile.getInt("forwardCoefficient")

        lMotor.targetPosition = lMotor.currentPosition + cm * coef
        rMotor.targetPosition = rMotor.currentPosition + cm * coef
    }

}