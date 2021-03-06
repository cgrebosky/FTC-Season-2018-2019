package c4.subsystems

import c4.lib.C4PropFile
import c4.lib.ControllerHelper
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.Servo

class Lift(lop: LinearOpMode? = null, opm: OpMode): SubSystem(lop, opm) {
    public lateinit var liftMotor: DcMotor

    public lateinit var lowerTouchSensor: DigitalChannel
    private lateinit var upperTouchSensor: DigitalChannel

    override fun init() {
        liftMotor = opm.hardwareMap.dcMotor.get("lift_motor")
        liftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        lowerTouchSensor = opm.hardwareMap.get(DigitalChannel::class.java, "limit_lower")
        upperTouchSensor = opm.hardwareMap.get(DigitalChannel::class.java, "limit_upper")


    }
    override fun loop() {
        var pow = (opm.gamepad1.left_trigger - opm.gamepad1.right_trigger).toDouble()

        if(lowerTouchSensor.state && pow > 0)
            pow = 0.0
        if(upperTouchSensor.state && pow < 0)
            pow = 0.0

        liftMotor.power = pow


    }
    override fun telemetry() {
        opm.telemetry.addLine("LIFT")
        opm.telemetry.addLine("    Lift Motor Power: ${liftMotor.power}")
        opm.telemetry.addLine("    Lower Limit: ${lowerTouchSensor.state}")
        opm.telemetry.addLine("    Lower Limit: ${upperTouchSensor.state}")
        opm.telemetry.addLine("")

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
        while(!upperTouchSensor.state) {
            raiseLift(pow)
        }
        raiseLift(0.0)
    }

}