package c4.testing

import c4.prod.vision.GoldVision
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.corningrobotics.enderbots.endercv.CameraViewDisplay

@Disabled
@TeleOp(name = "GoldVisionOpMode", group = "Testing")
class GoldVisionOpMode: OpMode() {
    lateinit var goldVision: GoldVision

    override fun init() {
        goldVision = GoldVision(this)
        goldVision.init(hardwareMap.appContext, CameraViewDisplay.getInstance())
        goldVision.enable()

    }

    override fun loop() {

    }

    override fun stop() {
        goldVision.disable()
    }
}