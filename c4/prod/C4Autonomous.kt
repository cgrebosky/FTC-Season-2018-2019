package c4.prod

import c4.lib.*
import c4.subsystems.*
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import javax.annotation.processing.SupportedSourceVersion

@Autonomous(name = "C4 Autonomous", group = "Prod")
class C4Autonomous: LinearOpMode() {

    init {
        C4PropFile.loadPropFile()
    }

    private enum class Sides { RED, BLUE }
    private enum class Positions { FAR, NEAR } //far/near with respect to the crater.
    private var team: Sides? = null
    private var position: Positions? = null
    private val xControlled = ControllerHelper()
    private val aControlled = ControllerHelper()

    private val td = TankDrive(lop = this, opm = this as OpMode)
    private val lift = Lift(lop = this, opm = this as OpMode)
    private val flicker = Flicker(lop = this, opm = this as OpMode)
    private val collector = Collector(this, this as OpMode)
    private val vision = ResourceDetector(lop = this)
    private val depositor = MineralDepositor(this, this)

    override fun runOpMode() {
        try {
            initAll()

            choosePosition()
            //hang()

            telemetry.addLine("Position: $position")
            telemetry.addLine("Power: ${lift.liftMotor.power}")
            telemetry.update()

            waitForStart()

            //releaseFromLander()

            sleep(1000)

            telemetry.addLine("Unfolding camera")
            telemetry.update()

            depositor.leftArm.slowGoToValue(0.24)
            depositor.rightArm.slowGoToValue(0.71)

            flicker.leftFlicker.slowGoToValue(C4PropFile.getDouble("cameraOut"))

            sleep(1000)

            telemetry.addLine("Vision")
            telemetry.update()

            vision.activateTF()

            while (vision.detectMinerals() == null);
            sleep(2000) //Wait for it to get the image - it starts out with a black screen
            // and defaults to right, so we have to delay here

            var p: ResourceDetector.GoldBlockPosition? = null
            val t = time
            while(time - t < 3 && opModeIsActive()) {
                p = vision.detectMinerals()
                telemetry.addLine("$p")
                telemetry.update()
            }

            flicker.leftFlicker.slowClose()

            sleep(500)

            flicker.flickMineral(p, collector)

            sleep(5000)



            if(p == ResourceDetector.GoldBlockPosition.MIDDLE)
                td.backward(C4PropFile.getInt("back1"))
            else
                td.backward(C4PropFile.getInt("back1"))

            sleep(500)

            flicker.leftFlicker.slowClose()
            flicker.rightFlicker.slowClose()
            collector.goToRaised()

            td.forward(C4PropFile.getInt("fwd1"))
            td.left(C4PropFile.getInt("turn1"))
            td.backward(C4PropFile.getInt("back2"))
            td.left(C4PropFile.getInt("turn2"))
            td.pow /= 3 //Just so we don't slam into the crater
            td.backward(C4PropFile.getInt("back3"))

            /*Crater side autonomous; Implement in future?
            td.forwardDead(-1300)
            sleep(300)
            td.turnDead(-C4PropFile.getInt("t1"))
            sleep(300)
            td.forwardDead(-4143)
            sleep(300)
            td.turnDead(-C4PropFile.getInt("t2"))
            sleep(300)
            td.forwardDead(-3800)
            sleep(300)
            collector.goToLowered()
            sleep(500)
            collector.push()
            sleep(1000)
            collector.goToRaised()
            sleep(500)
            td.backwardDead(10000)
            */

            stopAll()


        } catch (e: SubSystem.OpModeStopException) {
            Trace.log("Autonomous stopped prematurely")
            stopAll()
        }
    }

    /**
     * Choose our position, near or far from the crater.
     */
    fun choosePosition() {
        while(!aControlled.press(gamepad1.y) && !isStopRequested) {
            xControlled.toggle(gamepad1.x)

            position = if(xControlled.state) {
                Positions.FAR
            } else {
                Positions.NEAR
            }

            telemetry.addLine("Press X to toggle & Y to choose your option")
            telemetry.addLine("Current Option: $position")
            telemetry.update()
        }
    }
    /**
     * This is how we start to hang our robot.  It does not simply statically hang in place, this
     * allows us to control the robot, starting the hanging routine, and following that hangs statically
     */
    fun hang() {
        while(!aControlled.press(gamepad1.y) && !isStopRequested) {
            lift.loop()

            telemetry.addLine("Now make the robot hang;  Right trigger to raise")
            telemetry.addLine("Press Y to finalize your power.")
            telemetry.addLine("Current Power: ${lift.liftMotor.power}")
            telemetry.update()
        }
    }

    /**
     * Initailize all our components / subsystems
     */
    fun initAll() {
        td.init()
        lift.init()
        flicker.init()
        collector.init()
        vision.init()
        depositor.init()
    }
    /**
     * Stop all our components / subsystems
     */
    fun stopAll() {
        td.stop()
        lift.stop()
        flicker.stop()
        collector.stop()
        vision.stop()
        depositor.stop()
    }

    /**
     * Lower the robot from the lander and release it.
     */
    fun releaseFromLander() {
        lift.raiseLift(0.2)
        Thread.sleep(3000)
        lift.raiseLift(0.7)
        Thread.sleep(1000)
        lift.raiseLift(0.0)

    }

    fun farPath() {

    }
}