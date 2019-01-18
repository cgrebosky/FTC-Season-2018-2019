package c4.testing.subsystems

import c4.subsystems.Lift
import c4.subsystems.MecanumObject
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous(name = "AutoHangTest", group ="Testing")
class AutoHangTest: LinearOpMode() {

    val lift = Lift(this, this)
    val mec = MecanumObject(this)

    override fun runOpMode() {

        lift.init()
        mec.init()

        waitForStart()

        lift.completelyRaiseLift(0.4)
        Thread.sleep(500)
        mec.setMotorPowers(0.3, 0.0, 0.0)
        Thread.sleep(100);
        mec.backTicks(500)
        mec.setMotorPowers(-0.3,0.0,0.0)
        Thread.sleep(70)
        mec.setMotorPowers(0.0,0.0,0.0)


        mec.stop()
        lift.stop()
    }
}