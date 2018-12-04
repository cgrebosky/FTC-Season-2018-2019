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

    private val mecanum = MecanumObject(this)
    private val lift = Lift(opm = this)
    private val flicker = Flicker(opm = this)
    private val collector = Collector(this)
    private val depositor = MineralDepositor(this)
    private val vision = ResourceDetector(null, this);

    override fun init() {

        C4PropFile.loadPropFile()
        Thread.sleep(200)

        collector.init()
        mecanum.init()
        lift.init()
        flicker.init()
        depositor.init()
        vision.teleInit()

        collector.lift = lift

        telemetry.addLine("Ready")
        telemetry.update()
    }

    override fun loop() {



        collector.loop()
        collector.telemetry()
        mecanum.loop()
        mecanum.telemetry()
        lift.loop()
        lift.telemetry()
        flicker.loop()
        flicker.telemetry()
        depositor.loop()
        depositor.telemetry()
        //vision.telemetry()
        vision.loop()

        telemetry.update()
    }

    override fun stop() {
        collector.stop()
        mecanum.stop()
        lift.stop()
        flicker.stop()
        depositor.stop()
        vision.stop()
    }
}