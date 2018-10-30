package c4.subsystems

import c4.lib.C4PropFile
import c4.lib.pctError
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

class TankDrive(lop: LinearOpMode? = null, opm: OpMode): SubSystem(lop, opm) {

    private lateinit var motorLF: DcMotor
    private lateinit var motorLB: DcMotor
    private lateinit var motorRF: DcMotor
    private lateinit var motorRB: DcMotor

    override fun init() {
        motorLF = opm.hardwareMap.dcMotor.get("drive_fl")
        motorLB = opm.hardwareMap.dcMotor.get("drive_bl")
        motorRF = opm.hardwareMap.dcMotor.get("drive_fr")
        motorRB = opm.hardwareMap.dcMotor.get("drive_br")

        motorLF.direction = DcMotorSimple.Direction.REVERSE
        motorLB.direction = DcMotorSimple.Direction.REVERSE

        motorLF.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorLB.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorRF.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorRB.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    override fun loop() {
        motorLF.power = -opm.gamepad1.left_stick_y.toDouble()
        motorLB.power = -opm.gamepad1.left_stick_y.toDouble()
        motorRF.power = -opm.gamepad1.right_stick_y.toDouble()
        motorRB.power = -opm.gamepad1.right_stick_y.toDouble()
    }

    override fun telemetry() {
        opm.telemetry.addLine("DRIVE")
        opm.telemetry.addLine("    motorLF Power: ${motorLF.power}")
        opm.telemetry.addLine("    motorLB Power: ${motorLB.power}")
        opm.telemetry.addLine("    motorRF Power: ${motorRF.power}")
        opm.telemetry.addLine("    motorRB Power: ${motorRB.power}")
        opm.telemetry.addLine("")
    }

    /**
     * Turn a set number of degrees via dead reckoning and motor encoders.  Left is negative, right
     * is positive
     * @param deg the desired change in position in degrees
     */
    @AutoMethod private fun turnDead(ticks: Int) {

    }

    /**
     * Go forward a number of centimeters via dead reckoning and motor encoders.  Positive cm is
     * forward, negative is backwards
     */
    @AutoMethod private fun forwardDead(ticks: Int) {
    }
}