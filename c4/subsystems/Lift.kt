package c4.subsystems

import c4.lib.C4PropFile
import c4.lib.ControllerHelper
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.Servo

class Lift(lop: LinearOpMode? = null, opm: OpMode): SubSystem(lop, opm) {
    private var liftUpPosition = C4PropFile.getInt("liftUpPosition")

    private var raisingFlag = false

    public lateinit var liftMotor: DcMotor

    public lateinit var lowerTouchSensor: DigitalChannel
    public lateinit var upperTouchSensor: DigitalChannel
    private lateinit var led: DigitalChannel

    private val dpadToggle = ControllerHelper()

    override fun init() {
        liftMotor = opm.hardwareMap.dcMotor.get("lift_motor")

        liftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        liftMotor.direction = DcMotorSimple.Direction.REVERSE

        lowerTouchSensor = opm.hardwareMap.get(DigitalChannel::class.java, "limit_lower")
        upperTouchSensor = opm.hardwareMap.get(DigitalChannel::class.java, "limit_upper")

        led = opm.hardwareMap.get(DigitalChannel::class.java, "green_led")
        led.mode = DigitalChannel.Mode.OUTPUT
    }
    override fun loop() {
        var pow = (opm.gamepad1.left_trigger - opm.gamepad1.right_trigger).toDouble()

        if(lowerTouchSensor.state && pow > 0)
            pow = 0.0
        if(upperTouchSensor.state && pow < 0)
            pow = 0.0

        led.state = !lowerTouchSensor.state

        if(opm.gamepad1.b && pow == 0.0) raisingFlag = true
        if(liftMotor.currentPosition >= liftUpPosition) raisingFlag = false
        if(raisingFlag && liftMotor.currentPosition < liftUpPosition && !lowerTouchSensor.state) {
            liftMotor.power = 0.6
        } else {
            liftMotor.power = pow
        }
    }
    override fun telemetry() {
        opm.telemetry.addLine("LIFT")
        opm.telemetry.addLine("    Lift Motor Power: ${liftMotor.power}")
        opm.telemetry.addLine("    Lower Limit: ${lowerTouchSensor.state}")
        opm.telemetry.addLine("    Upper Limit: ${upperTouchSensor.state}")
        opm.telemetry.addLine("    Position: ${liftMotor.currentPosition}")
        opm.telemetry.addLine("")

    }
    override fun stop() {
    }

    fun raiseLift(pow: Double) {

        var p = pow

        if(lowerTouchSensor.state && pow < 0)
            p = 0.0
        if(upperTouchSensor.state && pow > 0)
            p = 0.0

        liftMotor.power = p
    }
    fun lowerLift(pow: Double) {
        var p = pow

        if(lowerTouchSensor.state && pow > 0)
            p = 0.0
        if(upperTouchSensor.state && pow < 0)
            p = 0.0

        liftMotor.power = -p
    }
    fun completelyRaiseLift(pow: Double) {
        while(!lowerTouchSensor.state) {
            raiseLift(pow)
        }
        raiseLift(0.0)
    }
    fun completelyLowerLift(pow: Double) {
        while(!upperTouchSensor.state) {
            lowerLift(pow)
        }
        raiseLift(0.0)
    }

    fun goToRaised() {
        val t = System.currentTimeMillis()
        while(liftMotor.currentPosition < liftUpPosition || System.currentTimeMillis() - t > 4000) {
        liftMotor.power = 0.3
        }
        raiseLift(0.0)
    }

}