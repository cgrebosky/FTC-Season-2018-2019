package c4.testing

import c4.lib.ControllerHelper
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo

@Disabled
@TeleOp(name="Servo Tester", group="Testing")
class ServoTester: OpMode() {

    lateinit var servo: Servo

    val a = ControllerHelper()
    val b = ControllerHelper()

    var stepValue = 0.0

    override fun init() {
        servo = hardwareMap.servo.get("test")
    }

    override fun loop() {

        if(a.press(gamepad1.a)) stepValue += 0.1
        if(b.press(gamepad1.b)) stepValue -= 0.1
        val pos = stepValue + (gamepad1.left_trigger * 0.1)

        servo.position = pos

        telemetry.addLine("Position: ${servo.position}")
        telemetry.update()
    }
}
