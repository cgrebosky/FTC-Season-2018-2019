package c4.lib

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.robotcore.external.Telemetry

object TelemetryHelper {
    lateinit var telemetry: Telemetry

    /**
     * Initializes the TelemetryHelper object.
     *
     * @param opm The current opmode.  This will allow us to get the telemetry.
     */
    fun init(opm: OpMode) {
        telemetry = opm.telemetry
    }

    /**
     * Creates a list with a number of options.  It is useful for choosing your options
     *
     * @param caption The initial caption that should appear above our telemetry stuff
     * @param list The list that we shall display.  For enums you can use .values()
     * @param ptr The pointer that points to our current choice.  This should be managed by outside
     * this class
     * @param choose when is a value chosen?  When this is true, we will return a value
     *
     * @return if we have not chosen a value, this will return null.  Otherwise this will be our
     * selected value
     */
    fun <T> chooseFromList(caption: String, list: Array<T>, ptr: Int, choose: Boolean): T? {
        telemetry.addLine(caption)
        for (i in 0 until list.size) {
            val pre = if(i == ptr) ">" else "  "

            telemetry.addLine("$pre ${list[i]}")
        }

        telemetry.update()

        if(choose) {
            return list[ptr]
        }
        return null
    }

}
