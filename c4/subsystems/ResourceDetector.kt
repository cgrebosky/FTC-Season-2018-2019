package c4.subsystems

import c4.lib.C4PropFile
import c4.lib.Trace
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.Recognition
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector

class ResourceDetector(lop: LinearOpMode?, opm: OpMode): SubSystem(lop = lop, opm = opm) {


    private val Y_THRESHOLD = 600

    private val TFOD_MODEL_ASSET = "RoverRuckus.tflite"
    private val LABEL_GOLD_MINERAL = "Gold Mineral"
    private val LABEL_SILVER_MINERAL = "Silver Mineral"

    private val VUFORIA_KEY = "AYBNsh3/////AAABmUprTCAGrUL7h8odXetdmu1r1oSQv23+Msoyvu1ArLHWA3Sm2bnZ+0sICt5iRYmEcpqRaMLrN0L1h1oQ25TuZZprUiFU2qcf3lqvwaZWDpocwbtc5Kry55NqesfKgDCa/Sjcd5dkwYbwT858hsg9FnV1wZ73KNyJsek9LdqhT7GI8EUmZsGdjgysyN2z57IpvSS/0JydDjY3u+X7oRgWlIR2qfkZJbOf1jqv35hP2R9YqLCIyDvFriMLn+EIy/Ho/JqQuBsfZEJ9U6z14IIniAwfHQ7ZffhfPDx2k1MquqHzZVU0jX5ry6sN5RoKRUrsFfoumfwQI7XX3oG/o9UtIiUpjBzBOxjqFhPFnfttXvcu"

    private val CLOSED = C4PropFile.getDouble("cameraClosed")
    private val OPEN = C4PropFile.getDouble("cameraOpen")
    private lateinit var cameraFlicker: UniversalFlicker

    private lateinit var vf: VuforiaLocalizer
    private var tfod: TFObjectDetector? = null

    public enum class GoldBlockPosition {
        LEFT, MIDDLE, RIGHT;

        override fun toString(): String {
            return when(this) {
                GoldBlockPosition.MIDDLE -> "MIDDLE"
                GoldBlockPosition.LEFT -> "LEFT"
                GoldBlockPosition.RIGHT -> "RIGHT"
            }
        }
    }

    override fun init() {
        initServo()
        vf = initVuforia()
        tfod = initTF()
    }
    override fun stop() {
        tfod?.deactivate()
        cameraFlicker.kill()
    }
    override fun teleInit() {
        initServo()
        cameraFlicker.slowClose()
    }
    override fun loop() {
        cameraFlicker.slowClose()
    }
    override fun telemetry() {
        val pos = detectMinerals()

        opm.telemetry.addLine(
                when(pos) {
                    GoldBlockPosition.MIDDLE -> "MIDDLE"
                    GoldBlockPosition.LEFT -> "LEFT"
                    GoldBlockPosition.RIGHT -> "RIGHT"
                    else -> "Unable to detect minerals"
                }
        )
        opm.telemetry.update()
    }

    @AutoMethod fun initVuforia(): VuforiaLocalizer {
        val parameters = VuforiaLocalizer.Parameters()

        parameters.vuforiaLicenseKey = VUFORIA_KEY
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK

        return ClassFactory.getInstance().createVuforia(parameters)
    }
    @AutoMethod fun initTF(): TFObjectDetector {
        //No checks are being done on this to assure it's stability.  If this throws an error around
        //here, that means the device is not supported.

        val tfodMonitorViewId = opm.hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", opm.hardwareMap.appContext.getPackageName())
        val tfodParameters = TFObjectDetector.Parameters(tfodMonitorViewId)
        tfodParameters.useObjectTracker = false
        tfodParameters.minimumConfidence = 0.65
        val tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vf)
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL)

        return tfod
    }

    @AutoMethod fun activateTF() {
        tfod?.activate()
    }
    @AutoMethod fun detectMinerals(): GoldBlockPosition? {
        //By the time we'll call this, tfod will be nonnull
        var recognitions: List<Recognition>? = tfod?.recognitions

        if(recognitions == null) {
            opm.telemetry.addLine("NULL")
            return null
        }

        if(recognitions.isEmpty()) {
            opm.telemetry.addLine("EMPTY")
            return GoldBlockPosition.RIGHT
        }

        recognitions = recognitions.filter { it.width > C4PropFile.getInt("heightThresh") }
        recognitions = recognitions.filter { it.label == LABEL_GOLD_MINERAL }

        for(i in recognitions) {
            opm.telemetry.addLine("Height: ${i.width}, Bottom: ${i.bottom}")
        }

        val r = recognitions.firstOrNull()

        if(r == null) {
            return GoldBlockPosition.RIGHT
        } else if(r.bottom < C4PropFile.getInt("bottomThresh")) { //500
            return GoldBlockPosition.LEFT
        } else if(r.bottom >= C4PropFile.getInt("bottomThresh")) {
            return GoldBlockPosition.MIDDLE
        } else {
            return null //This should never run
        }
    }

    fun initServo() {
        cameraFlicker = UniversalFlicker(
                opm.hardwareMap.servo.get("servo_camera"),
                C4PropFile.getDouble("cameraOpen"),
                C4PropFile.getDouble("cameraClosed")
        )
    }
}