package c4.prod

import c4.lib.C4PropFile
import c4.subsystems.Collector
import c4.subsystems.Lift
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@Autonomous(name="Encoder Calibrator", group = "Prod")
class EncoderCalibrator(): LinearOpMode() {

    val lift = Lift(opm = this)
    val collector = Collector(this)

    override fun runOpMode() {
        lift.init()
        collector.init()

        waitForStart()

        calibrateHinge()
        sleep(700)

        calibrateExtender()

        lift.stop()
        collector.stop()
    }

    private fun calibrateHinge() {
        collector.hinge.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        while (opModeIsActive() && !gamepad1.y) {
            collector.hinge.power = (gamepad1.left_trigger - gamepad1.right_trigger).toDouble()
            telemetry.addLine("HINGE CALIBRATOR")
            telemetry.addLine("Move HINGE to folded position")
            telemetry.addData("Current Power", collector.hinge.power)
            telemetry.addData("Current Position", collector.hinge.currentPosition)
            telemetry.update()
        }
        collector.hinge.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        collector.hinge.mode = DcMotor.RunMode.RUN_TO_POSITION

        telemetry.update()
    }

    private fun calibrateExtender() {
        while (opModeIsActive() && !gamepad1.y) {
            collector.extender.power = (gamepad1.left_trigger - gamepad1.right_trigger).toDouble()
            telemetry.addLine("EXTENDER CALIBRATOR")
            telemetry.addLine("Move EXTENDER to closed position")
            telemetry.addData("Current Power", collector.hinge.power)
            telemetry.addData("Current Position", collector.extender.currentPosition)
            telemetry.update()

        }
        collector.extender.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        collector.extender.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }
}