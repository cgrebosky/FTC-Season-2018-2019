package c4.testing

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo


@Disabled
@TeleOp(name = "ServoTester", group = "Testing")
class ServoTester: OpMode() {

    private lateinit var servo: Servo

    override fun loop() {
        servo = hardwareMap.servo.get("servo")

    }

    override fun init() {
        val pwr = gamepad1.left_stick_y.toDouble()
        servo.position = pwr

        telemetry.addLine("power/position: $pwr")
        telemetry.update()
    }
}