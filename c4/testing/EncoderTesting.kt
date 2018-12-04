package c4.testing

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference

@TeleOp(name="Encoder Testing", group = "Testing")
class EncoderTesting: OpMode() {
    lateinit var motor: DcMotor
    lateinit var imu: BNO055IMU

    override fun init() {
        motor = hardwareMap.dcMotor.get("TEST")


        val parameters = BNO055IMU.Parameters()
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json" // see the calibration sample opmode
        parameters.loggingEnabled = true
        parameters.loggingTag = "IMU"
        parameters.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()

        imu = hardwareMap.get(BNO055IMU::class.java, "imu")
        imu.initialize(parameters)
    }

    override fun loop() {
        motor.power = (gamepad1.left_trigger - gamepad1.right_trigger).toDouble()

        telemetry.addLine("Power: ${motor.power}")
        telemetry.addLine("Position: ${motor.currentPosition}")
        telemetry.addLine("Angle: ${imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle + 180}")
        telemetry.update()
    }
}