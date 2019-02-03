package c4.testing

import c4.subsystems.Lift
import c4.subsystems.MineralDepositor
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode


@Disabled
@Autonomous(name = "TEST", group = "Testing")
class Autotesting: LinearOpMode() {

    val lift = Lift(this, this)
    val depositor = MineralDepositor(this)

    override fun runOpMode() {

        lift.init()
        depositor.init()

        waitForStart()

        lift.completelyLowerLift(0.4)

        Thread.sleep(500)



        sleep(10000)
    }
}