package c4.testing

import c4.subsystems.Collector
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.sun.tools.javac.api.DiagnosticFormatter

@TeleOp(name="TeleOpInitTest", group="Testing")
class TeleOpInit: LinearOpMode() {

    private lateinit var extenderLimit: DigitalChannel
    private lateinit var hingeLimit: DigitalChannel

    private val collector = Collector(this)

//    override fun init() {
//        extenderLimit = hardwareMap.get(DigitalChannel::class.java, "limit_extender")
//        hingeLimit = hardwareMap.get(DigitalChannel::class.java, "limit_hinge")
//    }
//
//    override fun loop() {
//        telemetry.addData("Extender", extenderLimit.state)
//        telemetry.addData("Hinge", hingeLimit.state)
//        telemetry.update()
//    }

    override fun runOpMode() {
        extenderLimit = hardwareMap.get(DigitalChannel::class.java, "limit_extender")
        hingeLimit = hardwareMap.get(DigitalChannel::class.java, "limit_hinge")

        collector.init()

        waitForStart()

        collector.hinge.mode = DcMotor.RunMode.RUN_USING_ENCODER

        while (!extenderLimit.state || !hingeLimit.state) {
            if(!extenderLimit.state)
                collector.extender.power = -0.3

            if(!hingeLimit.state)
                collector.hinge.power = -0.2

            telemetry.addData("Extender", extenderLimit.state)
            telemetry.addData("Hinge", hingeLimit.state)
            telemetry.update()
        }

        collector.hinge.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        collector.extender.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        collector.hinge.mode = DcMotor.RunMode.RUN_TO_POSITION

        telemetry.addData("Hinge Position", collector.extender.currentPosition)
        telemetry.addData("Extender Position", collector.hinge.currentPosition)
        telemetry.update()
    }
}