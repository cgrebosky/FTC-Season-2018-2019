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

    var pow = C4PropFile.getDouble("autoPower")

    override fun init() {
        motorLF = opm.hardwareMap.dcMotor.get("drive_fl")
        motorLB = opm.hardwareMap.dcMotor.get("drive_bl")
        motorRF = opm.hardwareMap.dcMotor.get("drive_fr")
        motorRB = opm.hardwareMap.dcMotor.get("drive_br")

        motorRF.direction = DcMotorSimple.Direction.REVERSE
        motorRB.direction = DcMotorSimple.Direction.REVERSE

        motorLF.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorLB.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorRF.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorRB.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }
    override fun loop() {
        motorLF.power = -Math.pow(opm.gamepad1.left_stick_y.toDouble(),3.0)
        motorLB.power = -Math.pow(opm.gamepad1.left_stick_y.toDouble(),3.0)
        motorRF.power = -Math.pow(opm.gamepad1.right_stick_y.toDouble(),3.0)
        motorRB.power = -Math.pow(opm.gamepad1.right_stick_y.toDouble(),3.0)
    }
    override fun telemetry() {
        opm.telemetry.addLine("DRIVE")
        opm.telemetry.addLine("    motorLF Power: ${motorLF.power}")
        opm.telemetry.addLine("    motorLB Power: ${motorLB.power}")
        opm.telemetry.addLine("    motorRF Power: ${motorRF.power}")
        opm.telemetry.addLine("    motorRB Power: ${motorRB.power}")
        opm.telemetry.addLine("")
    }

    @AutoMethod fun forward(ticks: Int) {
        val targetPosition = motorLF.currentPosition + ticks

        while(targetPosition > motorLF.currentPosition) {
            power(pow)
            checkOpModeCancel()
        }
        zero()
    }
    @AutoMethod fun backward(ticks: Int) {
        val targetPosition = motorLF.currentPosition - ticks

        while (targetPosition < motorLF.currentPosition) {
            power(-pow)
            checkOpModeCancel()
        }
        zero()
    }
    @AutoMethod fun left(ticks: Int) {
        val targetPosition = motorLF.currentPosition - ticks

        while(targetPosition < motorLF.currentPosition) {
            left(pow)
            checkOpModeCancel()
        }
        zero()
    }
    @AutoMethod fun right(ticks: Int) {
        val targetPosition = motorLF.currentPosition + ticks

        while (targetPosition > motorLF.currentPosition) {
            right(pow)
            checkOpModeCancel()
        }
        zero()
    }

    @AutoMethod fun zero() {
        motorLF.power = 0.0
        motorLB.power = 0.0
        motorRF.power = 0.0
        motorRB.power = 0.0
    }
    @AutoMethod fun power(power: Double) {
        motorLF.power = power
        motorLB.power = power
        motorRF.power = power
        motorRB.power = power
    }
    @AutoMethod fun left(power: Double) {
        motorLF.power = -power
        motorLB.power = -power
        motorRF.power = power
        motorRB.power = power
    }
    @AutoMethod fun right(power: Double) {
        motorLF.power = power
        motorLB.power = power
        motorRF.power = -power
        motorRB.power = -power
    }
}