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

    private enum class Sides { RED, BLUE }
    private enum class Positions { FAR, NEAR } //far/near with respect to the crater.
    private var position: Positions? = null
    private val xControlled = ControllerHelper()
    private val aControlled = ControllerHelper()

    private var simpleCrater = true

    private val mecanum = MecanumObject(this)
    private val lift = Lift(lop = this, opm = this as OpMode)
    private val flicker = Flicker(lop = this, opm = this as OpMode)
    private val collector = Collector(this, this as OpMode)
    private val vision = ResourceDetector(lop = this, opm = this as OpMode)
    private val depositor = MineralDepositor(this, this)

    override fun runOpMode() {
        try {
            initProperties()
            initAll()

            driverChoosing()
            hang()

            telemetry.addLine("Ready")
            telemetry.update()

            waitForStart()

            releaseFromLander()

            //region vision processing
            telemetry.addLine("Unfolding camera")
            telemetry.update()

            vision.openCamera()

            telemetry.addLine("Vision")
            telemetry.update()

            vision.activateTF()

            while (vision.detectMinerals() == null);
            sleep(1500) //Wait for it to get the image - it starts out with a black screen
            // and defaults to right, so we have to delay here

            var p: ResourceDetector.GoldBlockPosition? = null
            val t = time

            var left = 0
            var mid = 0
            var right = 0
            var n = 0

            while(time - t < 1 && opModeIsActive()) {
                p = vision.detectMinerals()

                telemetry.addLine("Current Position: $p")
                telemetry.update()

                if(p == ResourceDetector.GoldBlockPosition.LEFT) left++
                if(p == ResourceDetector.GoldBlockPosition.MIDDLE) mid++
                if(p == ResourceDetector.GoldBlockPosition.RIGHT) right++
                if(p == null) n++
            }

            telemetry.addData("Left", left)
            telemetry.addData("Mid", mid)
            telemetry.addData("Right", right)
            telemetry.addData("Nulls", n)

            if(left > mid && left > right) p = ResourceDetector.GoldBlockPosition.LEFT
            else if(mid > left && mid > right) p = ResourceDetector.GoldBlockPosition.MIDDLE
            else if(right > mid && right > left) p = ResourceDetector.GoldBlockPosition.RIGHT
            else p = ResourceDetector.GoldBlockPosition.RIGHT //Default to right

            telemetry.addData("Position", p)
            telemetry.update()

            vision.closeCamera()
            //endregion vision processing

            sleep(300)

            if(p == ResourceDetector.GoldBlockPosition.MIDDLE) {
                collector.goToHovering()
                sleep(2000)
            }

            if(position == Positions.NEAR) {

                if(p == ResourceDetector.GoldBlockPosition.MIDDLE) mecanum.backTicks(C4PropFile.getInt("backMid"))
                else {
                    mecanum.backTicks(C4PropFile.getInt("backSides"))
                }

                if(p==ResourceDetector.GoldBlockPosition.LEFT) flicker.leftFlicker.fastOpen()
                if(p==ResourceDetector.GoldBlockPosition.RIGHT) flicker.rightFlicker.fastOpen()

                sleep(1000)

                flicker.leftFlicker.slowClose()
                flicker.rightFlicker.slowClose()
                collector.goToRaised()

                sleep(500)

                if (simpleCrater) {
                    if(p == ResourceDetector.GoldBlockPosition.MIDDLE) mecanum.backTicks(700)

                    collector.goToLowered()
                } else {

                    if(p == ResourceDetector.GoldBlockPosition.MIDDLE) mecanum.fwdTicks(C4PropFile.getInt("backMid") - C4PropFile.getInt("nearDiff"))
                    else mecanum.fwdTicks(C4PropFile.getInt("backSides") - C4PropFile.getInt("nearDiff"))

                    mecanum.turnDegrees(C4PropFile.getDouble("nearTurn1"))

                    mecanum.backTicks(C4PropFile.getInt("nearBack1"))
                    mecanum.turnDegrees(C4PropFile.getDouble("nearTurn2"))
                    mecanum.backTicks(C4PropFile.getInt("nearBack2"))
                    mecanum.turnDegrees(C4PropFile.getDouble("nearTurn3"))
                    mecanum.backTicks(C4PropFile.getInt("nearBack3"))

                    collector.goToHovering()

                    sleep(500)

                    flicker.leftFlicker.slowClose()
                    flicker.rightFlicker.slowClose()

                    sleep(1000)
                    collector.push()
                    sleep(2500)
                    collector.zero()
                    collector.goToRaised()
                    sleep(1000)

                    mecanum.fwdTicks(C4PropFile.getInt("nearFwd1"))
                    mecanum.setMotorPowers(0.25, C4PropFile.getDouble("nearEndDir"), 0.0)
                    sleep(2000)
                    mecanum.zero()


                }
            } else {
                mecanum.backTicks(C4PropFile.getInt("backSides"))

                sleep(1000)

                collector.goToHovering()

                if(p==ResourceDetector.GoldBlockPosition.LEFT) flicker.leftFlicker.fastOpen()
                if(p==ResourceDetector.GoldBlockPosition.RIGHT) flicker.rightFlicker.fastOpen()

                sleep(1000)

                flicker.leftFlicker.slowClose()
                flicker.rightFlicker.slowClose()

                collector.extend()
                sleep(2000)
                collector.push()
                sleep(1500)
                collector.zero()
                collector.goToRaised()
                collector.retract()
                sleep(1000)

                mecanum.fwdTicks(C4PropFile.getInt("fwdSides"))

                mecanum.turnDegrees(C4PropFile.getDouble("farTurn1"))
                mecanum.backTicks(C4PropFile.getInt("farBack1"))
                mecanum.turnDegrees(C4PropFile.getDouble("farTurn2"))
                mecanum.backTicks(C4PropFile.getInt("farBack2"))

                mecanum.setMotorPowers(0.25, 30.0, 0.0)
                collector.goToLowered()
                sleep(2000)
                mecanum.zero()
            }

            stopAll()
        } catch (e: SubSystem.OpModeStopException) {
            Trace.log("Autonomous stopped prematurely")
            stopAll()
        }
    }

    fun flickMineral(pos: ResourceDetector.GoldBlockPosition?) {
        when(pos) {
            ResourceDetector.GoldBlockPosition.MIDDLE ->
                /*if(simpleCrater) collector.goToLowered()
                else*/ collector.goToHovering()
            ResourceDetector.GoldBlockPosition.LEFT -> flicker.leftFlicker.slowOpen()
            ResourceDetector.GoldBlockPosition.RIGHT -> flicker.rightFlicker.slowOpen()
            null -> {} //Do nothing - Is the penalty worth it tho???  Maybe just default to one?
        }
    }

    /**
     * Choose our position, near or far from the crater.
     */
    fun driverChoosing() {
        while(!aControlled.press(gamepad1.y) && !isStopRequested) {
            xControlled.toggle(gamepad1.x)

            position = if(xControlled.state) {
                Positions.FAR
            } else {
                Positions.NEAR
            }

            telemetry.addLine("Press X to toggle & Y to choose your option")
            telemetry.addLine("Position: $position")
            telemetry.update()

            if(isStopRequested) throw SubSystem.OpModeStopException()
        }

        if(position == Positions.NEAR) {
            while (!aControlled.press(gamepad1.y) && !isStopRequested) {
                xControlled.toggle(gamepad1.x)

                simpleCrater = xControlled.state

                telemetry.addLine("Press X to toggle & Y to choose your option")
                telemetry.addLine("Simple Crater: $simpleCrater")
                telemetry.update()

                if(isStopRequested) throw SubSystem.OpModeStopException()
            }
        }
    }
    /**
     * This is how we start to hang our robot.  It does not just statically hang in place, this
     * allows us to control the robot, starting the hanging routine, and following that hangs statically
     */
    fun hang() {
        mecanum.init()
        while(!aControlled.press(gamepad1.y) && !isStopRequested) {
            lift.loop()
            mecanum.loop()

            telemetry.addLine("Now make the robot hang;  Right trigger to raise")
            telemetry.addLine("Press Y to finalize your power.")
            telemetry.addLine("Current Power: ${lift.liftMotor.power}")
            telemetry.update()

            if(isStopRequested) throw SubSystem.OpModeStopException()
        }
    }

    /**
     * This initializes the prop file errors.  This means that our robot must be set up perfectly at
     * the beginning of autonomous, not when the app restarts.  We can also change these values via
     * EncoderCalibrator.kt
     */
    fun initProperties() {
        collector.init() //We need to init this to get the currentPosition.  We will reinit this later to update the errors

        C4PropFile.loadPropFile()
        C4PropFile.set("hingeErr", "" + collector.hinge.currentPosition)
        C4PropFile.set("extenderErr", "" + collector.extender.currentPosition)
        C4PropFile.writeFile()
    }

    /**
     * Initailize all our components / subsystems
     */
    fun initAll() {
        mecanum.init()
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
        mecanum.stop()
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
        lift.goToRaised()


        mecanum.setMotorPowers(-0.3, 90.0, 0.0)
        sleep(100)


        Thread.sleep(300)
        mecanum.setMotorPowers(0.3, 165.0, 0.0)
        Thread.sleep(500);
        mecanum.backTicks(500)
        mecanum.setMotorPowers(0.3,0.0,0.0)
        Thread.sleep(600)
        mecanum.setMotorPowers(0.0,0.0,0.0)
        mecanum.fwdTicks(500)


    }

}