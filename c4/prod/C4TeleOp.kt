package c4.prod

import c4.subsystems.Flicker
import c4.subsystems.Lift
import c4.subsystems.TankDrive
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo

@TeleOp(name = "C4 Teleop", group = "Prod")
class C4TeleOp: OpMode() {

    val td = TankDrive(opm = this)
    val lift = Lift(opm = this)
    val flicker = Flicker(opm = this)

    lateinit var motorLift: DcMotor
    lateinit var servoLift: Servo

    override fun init() {
        td.init()
        lift.init()
        flicker.init()
    }

    override fun loop() {
        td.loop()
        td.telemetry()
        lift.loop()
        lift.telemetry()
        flicker.loop()
        flicker.telemetry()

        telemetry.update()
    }
}