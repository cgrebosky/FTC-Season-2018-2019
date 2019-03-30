package c4.testing.subsystems

import c4.lib.C4PropFile
import c4.subsystems.MineralDepositor
import c4.subsystems.UniversalFlicker
import com.qualcomm.robotcore.eventloop.opmode.*

@Disabled
@TeleOp(name = "DepositorTesting", group ="Testing")
class DepositorTesting: OpMode() {

    init {
        C4PropFile.loadPropFile()
    }

    val md = MineralDepositor(this)

    override fun init() {
        md.init()
    }

    override fun loop() {
        md.loop()
        md.telemetry()
    }

    override fun stop() {
        md.stop()
    }
}