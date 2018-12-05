package c4.testing.subsystems

import c4.lib.C4PropFile
import c4.subsystems.ResourceDetector
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode

@Autonomous(name = "Mineral Detector Test")
class MineralDetectorTesting: LinearOpMode() {

    val md = ResourceDetector(lop = this, opm = this as OpMode)

    override fun runOpMode() {
        C4PropFile.loadPropFile()

        md.init()

        waitForStart()

        md.activateTF()
        md.openCamera()

        while(opModeIsActive()) {
            val p = md.detectMinerals()

            telemetry.addLine(p.toString())

            telemetry.update()
        }

        md.stop()
    }

}