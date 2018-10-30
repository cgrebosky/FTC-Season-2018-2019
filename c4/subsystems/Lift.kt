package c4.subsystems

import c4.lib.C4PropFile
import c4.lib.ControllerHelper
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo

class Lift(lop: LinearOpMode? = null, opm: OpMode): SubSystem(lop, opm) {
    private lateinit var liftMotor: DcMotor
    private lateinit var latch: Servo

    private val latchOpen = C4PropFile.getDouble("latchOpen")
    private val latchClosed = C4PropFile.getDouble("latchClosed")

    private val latchToggle = ControllerHelper()

    override fun init() {
        liftMotor = opm.hardwareMap.dcMotor.get("lift_motor")
        latch = opm.hardwareMap.servo.get("lift_latch")
    }

    override fun loop() {
        toggleLatch()
        liftMotor.power = (opm.gamepad1.left_trigger - opm.gamepad1.right_trigger).toDouble()
    }

    override fun telemetry() {
        opm.telemetry.addLine("LIFT")
        opm.telemetry.addLine("    Lift Motor Power: ${liftMotor.power}")
        opm.telemetry.addLine("    Latch Position: ${latch.position}")
        opm.telemetry.addLine("")

    }

    fun toggleLatch() {
        latchToggle.toggle(opm.gamepad1.a)

        val latchValue = if(latchToggle.state) {
            latchOpen
        } else {
            latchClosed
        }

        latch.position = latchValue
    }

}