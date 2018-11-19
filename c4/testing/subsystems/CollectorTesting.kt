package c4.testing.subsystems

import c4.lib.C4PropFile
import c4.subsystems.Collector
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo

@TeleOp(name = "Collector Testing", group = "Testing")
class CollectorTesting: OpMode() {

    //val c = Collector(this)
    lateinit var crs: CRServo

    override fun init() {
        crs = hardwareMap.crservo.get("collector_spinner")
    }

    override fun loop() {
        crs.power = gamepad1.left_stick_y.toDouble()

        telemetry.addLine(""+crs.power)
        telemetry.update()
    }
}