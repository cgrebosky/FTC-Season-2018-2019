package c4.testing.software

import c4.prod.vision.GoldVision
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.corningrobotics.enderbots.endercv.CameraViewDisplay

@TeleOp(name = "GoldVisionOpMode", group = "Testing")
class GoldVisionOpMode: OpMode() {
    var goldVision: GoldVision? = null

    override fun init() {
        goldVision = GoldVision(this)
        goldVision!!.init(hardwareMap.appContext, CameraViewDisplay.getInstance())
        goldVision!!.enable()

    }

    override fun loop() {

    }

    override fun stop() {
        goldVision!!.disable()
    }
}