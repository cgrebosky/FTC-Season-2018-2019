package c4.testing.macro

import c4.lib.C4PropFile
import c4.subsystems.Collector
import c4.subsystems.Lift
import c4.subsystems.MecanumObject
import c4.subsystems.MineralDepositor
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

    val mecanum = MecanumObject(this)
    val collector = Collector(this)
    val lift = Lift(opm = this)
    //val arms = MineralDepositor(this)

    lateinit var recording: LinkedList<MacroState>

    override fun runOpMode() {
        initialize()

        waitForStart()
        telePrint("RUNNING")

        var i = 0
        val t0 = System.currentTimeMillis()
        while(opModeIsActive() && i < recording.size - 3) {
            actState(recording[i])

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
        mecanum.init()
        collector.init()
        collector.hinge.power = C4PropFile.getDouble("hingeSpeed")
        lift.init()
        //arms.init()

        telePrint("Loading data")
        readData()

        telePrint("Ready")
    }

    fun actState(m: MacroState) {
        mecanum.motorLF.power = m.motorPowerFL
        mecanum.motorLB.power = m.motorPowerBL
        mecanum.motorRF.power = m.motorPowerFR
        mecanum.motorRB.power = m.motorPowerBR

        collector.extender.power = m.extenderPower
        collector.hinge.targetPosition = m.hingePosition
        collector.spinner.power = m.spinnerPower
        collector.limiter.position = m.limiterPosition

        lift.liftMotor.power = m.liftPower

//        arms.arms.power = m.armsPower
//        arms.leftLatch.fastGoToValue(m.leftLatchPosition)
//        arms.rightLatch.fastGoToValue(m.rightLatchPosition)
    }
}