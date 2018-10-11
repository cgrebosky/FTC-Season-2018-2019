package c4.testing.software

import c4.lib.ControllerHelper
import c4.lib.TelemetryHelper
import c4.lib.MyMath
import c4.lib.TelemetryHelper.printChoosableList
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "ControllerHelperTesting", group = "Testing")
class ControllerHelperTesting: OpMode() {

    val gamepad_x = ControllerHelper()
    val gamepad_y = ControllerHelper()

    val list = arrayListOf("1","2","3","4","5")
    var ptr = 0
    private var choice: Values? = null

    private enum class Values(name: String) {
        Red("Red"), Blue("Blue"), Green("Gree");

        override fun toString(): String {
            return name
        }
    }

    override fun init() {
        msStuckDetectInitLoop = 100_000
        msStuckDetectInit = 100_000
        msStuckDetectLoop = 100_000
        TelemetryHelper.init(this)

        while (!gamepad1.a) {
            choice = TelemetryHelper.printChoosableList(
                    caption = "Choose Color.  X to go up, Y to go down, A to choose",
                    list = Values.values(),
                    ptr = ptr,
                    choose = gamepad1.a)
            if(gamepad_x.press(gamepad1.x)) MyMath.limitNumber(ptr++, 0, Values.values().size)
            if(gamepad_y.press(gamepad1.y)) MyMath.limitNumber(ptr--, 0, Values.values().size)

        }

    }

    override fun loop() {

    }



}