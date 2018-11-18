package c4.testing

import c4.lib.C4PropFile
import c4.subsystems.MineralDepositor
import c4.subsystems.UniversalFlicker
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp


@TeleOp(name = "DepositorTesting", group ="Testing")
class DepositorTesting: OpMode() {

    init {
        C4PropFile.loadPropFile()
    }

    lateinit var leftArm: UniversalFlicker
    lateinit var rightArm: UniversalFlicker

    override fun init() {
        leftArm = UniversalFlicker(
                hardwareMap.servo.get("arm_left"),
                C4PropFile.getDouble("leftArmUp"),
                C4PropFile.getDouble("leftArmDown")
        )
        leftArm.start()
        leftArm.speed = C4PropFile.getDouble("armSpeed")

        rightArm = UniversalFlicker(
                hardwareMap.servo.get("arm_right"),
                C4PropFile.getDouble("rightArmUp"),
                C4PropFile.getDouble("rightArmDown")
        )
        rightArm.start()
        rightArm.speed = C4PropFile.getDouble("armSpeed")
    }

    override fun loop() {
       if(gamepad1.a)
           leftArm.slowOpen()
        else
           leftArm.slowClose()

        if(gamepad1.b)
            rightArm.slowOpen()
        else
            rightArm.slowClose()

        telemetry.addLine("left arm pos: ${leftArm.position}")
        telemetry.addLine("right arm pos: ${rightArm.position}")
    }

    override fun stop() {
        leftArm.kill()
        rightArm.kill()
    }
}