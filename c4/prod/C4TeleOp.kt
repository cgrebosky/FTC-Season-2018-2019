package c4.prod

import c4.lib.C4PropFile
import c4.subsystems.*
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo

@TeleOp(name = "C4 Teleop", group = "Prod")
class C4TeleOp: OpMode() {

    init {
        C4PropFile.loadPropFile()
    }

    private val td = TankDrive(opm = this)
    private val lift = Lift(opm = this)
    private val flicker = Flicker(opm = this)
    private val collector = Collector(this)
    private val depositor = MineralDepositor(this)

    override fun init() {

        collector.init()
        td.init()
        lift.init()
        flicker.init()
        depositor.init()

        collector.lift = lift
    }

    override fun loop() {
        collector.loop()
        collector.telemetry()
        td.loop()
        td.telemetry()
        lift.loop()
        lift.telemetry()
        flicker.loop()
        flicker.telemetry()
        depositor.loop()
        depositor.telemetry()

        telemetry.update()
    }

    override fun stop() {
        collector.stop()
        td.stop()
        lift.stop()
        flicker.stop()
        depositor.stop()
    }
}