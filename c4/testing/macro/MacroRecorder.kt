package c4.testing.macro

import android.os.Environment
import c4.subsystems.Collector
import c4.subsystems.Lift
import c4.subsystems.MecanumObject
import c4.subsystems.MineralDepositor
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
    val collector = Collector(this)
    val lift = Lift(opm = this)
    //val arms = MineralDepositor(this)

    var recording = LinkedList<MacroState>()

    private var state = State.UNINITIALIZED;

    override fun init() {
        mecanum.init()
        collector.init()
        lift.init()
        //arms.init()

        telemetry.addLine("Ready")
        telemetry.update()
    }

    override fun loop() {
        mecanum.loop()
        collector.loop()
        lift.loop()
        //arms.loop()

        if(state == State.UNINITIALIZED && gamepad1.a) state = State.RUNNING
        if(state == State.RUNNING && gamepad1.x) state = State.STOPPED

        if(state == State.RUNNING)
            recording.add( createCurrentState() )
        else if(state == State.STOPPED) {
            serializeData()
            stop()
        }

        printTelemetry()
    }

    fun serializeData() {
        //"reindex" the data so that time starts at 0.  This just makes it easier to deal with.
        val t0 = recording[0].time
        for(i in 0 until (recording.size - 1)) {
            recording[i].time -= t0
        }

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

    fun createCurrentState(): MacroState {
        val m = MacroState(System.currentTimeMillis())
        m.extenderPower   = collector.extender.power
        m.hingePosition   = collector.hinge.targetPosition
        m.spinnerPower    = collector.spinner.power
        m.limiterPosition = collector.limiter.position

        m.liftPower       = lift.liftMotor.power

        m.motorPowerFL    = mecanum.motorLF.power
        m.motorPowerFR    = mecanum.motorRF.power
        m.motorPowerBL    = mecanum.motorLB.power
        m.motorPowerBR    = mecanum.motorRB.power

//        m.armsPower = arms.arms.power
//        m.leftLatchPosition = arms.leftLatch.position
//        m.rightLatchPosition = arms.rightLatch.position

        return m
    }
}