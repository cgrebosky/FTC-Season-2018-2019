package c4.testing.software

import c4.lib.C4PropFile
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.opencv.core.Scalar

@TeleOp(name = "C4PropFile testing", group = "Testing")
class C4PropFileTesting: OpMode() {

    var s = Scalar(0.0,0.0,0.0)
    lateinit var d: DoubleArray

    override fun init() {
        C4PropFile.loadPropFile()
        s = C4PropFile.getScalar("a")
        d = C4PropFile.getDoubleArray("a")
        val ds = "{${d[0]},${d[1]},${d[2]},${d[3]},${d[4]}"

        telemetry.addLine("s: $s")
        telemetry.addLine("da: $ds")
        telemetry.update()
    }

    override fun loop() {

    }
}