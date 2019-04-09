package c4.testing.macro

import c4.subsystems.MecanumObject
import c4.testing.macro.MacroState.Companion.filename
import c4.testing.macro.MacroState.Companion.path
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInput
import java.io.ObjectInputStream
import java.util.*

@Autonomous(name = "Macro Player")
class MacroPlayer: LinearOpMode() {

    lateinit var mecanum: MecanumObject

    lateinit var recording: LinkedList<MacroState>

    override fun runOpMode() {
        initialize()

        waitForStart()

        var i = 0
        val t0 = System.currentTimeMillis()
        while(opModeIsActive() && i < recording.size - 3) {
            mecanum.motorLF.power = recording[i].motorPowerFL
            mecanum.motorLB.power = recording[i].motorPowerBL
            mecanum.motorRF.power = recording[i].motorPowerFR
            mecanum.motorRB.power = recording[i].motorPowerBR

            telemetry.addData("i",i);
            telemetry.update()


            while (System.currentTimeMillis() - t0 < recording[i].time) sleep(1)

            i++
        }
    }

    fun readData() {
        val f = File("$path/$filename")
        val fis = FileInputStream(f)
        val ois = ObjectInputStream(fis)

        recording = ois.readObject() as LinkedList<MacroState>
    }

    fun telePrint(msg: String) {
        telemetry.addLine(msg)
        telemetry.update()
    }

    fun initialize() {
        telePrint("Initializing robot")
        mecanum = MecanumObject(this)
        mecanum.init()

        telePrint("Loading data")
        readData()
        val t0 = recording[0].time
        for(i in 0 until (recording.size - 1)) {
            recording[i].time -= t0
        }

        telePrint("${recording[0].time}   ${recording[3].time}")
    }
}