package c4.subsystems

import android.drm.DrmRights
import c4.lib.C4PropFile
import c4.lib.ControllerHelper
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode

class Flicker(lop: LinearOpMode? = null, opm: OpMode): SubSystem(lop = lop, opm = opm) {

    val leftToggle = ControllerHelper()
    val rightToggle = ControllerHelper()

    lateinit var leftFlicker: UniversalFlicker
    lateinit var rightFlicker: UniversalFlicker

    override fun init() {

        leftFlicker = UniversalFlicker(
                opm.hardwareMap.servo.get("flicker_left"),
                C4PropFile.getDouble("flickerLeftOpen"),
                C4PropFile.getDouble("flickerLeftClosed")
        )
        leftFlicker.start()
        leftFlicker.fastClose()

        rightFlicker = UniversalFlicker(
                opm.hardwareMap.servo.get("flicker_right"),
                C4PropFile.getDouble("flickerRightOpen"),
                C4PropFile.getDouble("flickerRightClosed")
        )
        rightFlicker.start()
        rightFlicker.fastClose()
    }
    override fun loop() {
        rightFlicker.slowClose()

        if(opm.gamepad2.dpad_up || opm.gamepad2.dpad_up || opm.gamepad2.dpad_left || opm.gamepad2.dpad_right)
            leftFlicker.slowOpen()
        else
            leftFlicker.slowClose()
    }
    override fun telemetry() {
        opm.telemetry.addLine("FLICKER")
        opm.telemetry.addLine("    Left Position: ${leftFlicker.position}")
        opm.telemetry.addLine("    Right Position: ${rightFlicker.position}")
        opm.telemetry.addLine("")
    }
    override fun stop() {
        rightFlicker.kill()
        leftFlicker.kill()
    }

    @AutoMethod fun flickMineral(pos: ResourceDetector.GoldBlockPosition?, collector: Collector) {
        when(pos) {
            ResourceDetector.GoldBlockPosition.MIDDLE -> collector.goToHovering()
            ResourceDetector.GoldBlockPosition.LEFT -> leftFlicker.slowOpen()
            ResourceDetector.GoldBlockPosition.RIGHT -> rightFlicker.slowOpen()
            null -> {} //Do nothing - Is the penalty worth it tho???  Maybe just default to one?
        }
    }
}