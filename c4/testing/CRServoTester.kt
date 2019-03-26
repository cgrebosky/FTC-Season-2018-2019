package c4.testing

import c4.lib.ControllerHelper
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.Servo

@Disabled
@TeleOp(name="CRServo Tester", group="Testing")
class CRServoTester: OpMode() {

    lateinit var servo: CRServo

    val a = ControllerHelper()
    val b = ControllerHelper()

    var stepValue = 0.0

    override fun init() {
        servo = hardwareMap.crservo.get("test")
    }

    override fun loop() {

        servo.power = (gamepad1.left_trigger - gamepad1.right_trigger).toDouble()

        telemetry.addLine("Pow: ${servo.power}")
        telemetry.update()
    }
}
