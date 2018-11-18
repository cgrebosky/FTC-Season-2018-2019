package c4.testing

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@TeleOp(name="Encoder Testing", group = "Testing")
class EncoderTesting: OpMode() {
    lateinit var motor: DcMotor

    override fun init() {
        motor = hardwareMap.dcMotor.get("TEST")

    }

    override fun loop() {
        motor.power = (gamepad1.left_trigger - gamepad1.right_trigger).toDouble()

        telemetry.addLine("Power: ${motor.power}")
        telemetry.addLine("Position: ${motor.currentPosition}")
        telemetry.update()
    }
}