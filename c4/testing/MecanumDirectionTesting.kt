package c4.testing

import c4.subsystems.MecanumObject
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@Disabled
@TeleOp(name = "Mecanum Directional Testing", group = "Testing")
class MecanumDirectionTesting: OpMode() {
    val m = MecanumObject(this)

    override fun init() {
        m.init()
    }

    override fun loop() {
        m.setMotorPowers(gamepad1.left_stick_y.toDouble(), gamepad1.right_stick_x.toDouble() * 360, 0.0)
        telemetry.addData("Pow",gamepad1.left_stick_y)
        telemetry.addData("Dir", gamepad1.right_stick_x.toDouble() * 360)
        telemetry.update()
    }

}