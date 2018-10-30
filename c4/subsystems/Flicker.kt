package c4.subsystems

import android.drm.DrmRights
import c4.lib.C4PropFile
import c4.lib.ControllerHelper
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Servo

class Flicker(lop: LinearOpMode? = null, opm: OpMode): SubSystem(lop = lop, opm = opm) {
    lateinit var flickerLeft: Servo
    lateinit var flickerRight: Servo

    val flickerLeftClosed = C4PropFile.getDouble("flickerLeftClosed")
    val flickerLeftOpen = C4PropFile.getDouble("flickerLeftOpen")
    val flickerRightClosed = C4PropFile.getDouble("flickerRightClosed")
    val flickerRightOpen = C4PropFile.getDouble("flickerRightOpen")

    val leftToggle = ControllerHelper()
    val rightToggle = ControllerHelper()

    override fun init() {
        flickerLeft = opm.hardwareMap.servo.get("flicker_left")
        flickerRight = opm.hardwareMap.servo.get("flicker_right")
    }

    //This is for testing purposes only, it really isn't needed in the actual production code
    override fun loop() {
        val left = if(leftToggle.toggle(opm.gamepad1.x)) {
            flickerLeftOpen
        } else {
            flickerLeftClosed
        }
        val right = if(rightToggle.toggle(opm.gamepad1.y)) {
            flickerRightOpen
        } else {
            flickerRightClosed
        }

        //flickerLeft.position = opm.gamepad1.left_trigger.toDouble()
        //flickerRight.position= opm.gamepad1.right_trigger.toDouble()

        flickerLeft.position = left
        flickerRight.position = right
    }

    override fun telemetry() {
        opm.telemetry.addLine("FLICKER")
        opm.telemetry.addLine("    Left Position: ${flickerLeft.position}")
        opm.telemetry.addLine("    Right Position: ${flickerRight.position}")
        opm.telemetry.addLine("")
    }
}