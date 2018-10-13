package c4.lib

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.robotcore.external.Telemetry

object TelemetryHelper {
    lateinit var telemetry: Telemetry

    fun init(opm: OpMode) {
        telemetry = opm.telemetry
    }

    /**
     * Creates a list with a number of choosable options.  This returns null if the user hasn't made
     * a choice, and a value if it has.  This is *not* an abusing kotlins anti-null philosophy, because
     * a choice logically should be null before being made
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
