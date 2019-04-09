package c4.testing.macro

import android.os.Environment
import c4.subsystems.MecanumObject
import c4.testing.macro.MacroState.Companion.filename
import c4.testing.macro.MacroState.Companion.path
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*

@TeleOp(name = "Macro Recorder")
class MacroRecorder: OpMode() {

    private enum class State {UNINITIALIZED, RUNNING, STOPPED}

    val mecanum = MecanumObject(this)
    var recording = LinkedList<MacroState>()

    private var state = State.UNINITIALIZED;

    override fun init() {
        mecanum.init()
        telemetry.addLine("Ready")
        telemetry.update()
    }


    override fun loop() {
        mecanum.loop()

        if(state == State.UNINITIALIZED && gamepad1.a) state = State.RUNNING
        if(state == State.RUNNING && gamepad1.x) state = State.STOPPED

        if(state == State.RUNNING) {
            recording.add(
                    MacroState(
                            time = System.currentTimeMillis(),
                            motorPowerFL = mecanum.motorLF.power,
                            motorPowerFR = mecanum.motorRF.power,
                            motorPowerBL = mecanum.motorLB.power,
                            motorPowerBR = mecanum.motorRB.power
                    )
            )
        }

        printTelemetry()

        if(state == State.STOPPED) {
            serializeData()
            stop()
        }
    }

    fun serializeData() {
        val f = File("$path/$filename")
        val fos = FileOutputStream(f)
        val oos = ObjectOutputStream(fos)

        oos.writeObject(recording)
    }

    fun printTelemetry() {
        telemetry.addLine("Press [A] to start, [X] to stop")
        telemetry.addData("Status", state)

        telemetry.update()
    }
}