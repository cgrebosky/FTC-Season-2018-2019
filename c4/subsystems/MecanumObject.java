package c4.subsystems;


import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.Collections;

import c4.lib.PIDController;
import c4.lib.Trace;


/**
 * Created by Miklos on 11/15/16.
 */

public class MecanumObject extends SubSystem {

    private static double TURNING_CORRECTION = 30;

    private DcMotor motorRF;
    private DcMotor motorLF;
    private DcMotor motorRB;
    private DcMotor motorLB;

    private double motorPowerFR;
    private double motorPowerFL;
    private double motorPowerBR;
    private double motorPowerBL;

    private double heading;
    private double targetHeading = Double.NaN;
    private boolean turnFlag = false;
    private double turnPower;

    private BNO055IMU imu;

    //Aight, PIDs are nice 'n all, but it seems overkill for this IMO.
    private PIDController strafeAdjust = new PIDController(0.02, 0.0002, 0);
    private PIDController turnAdjust = new PIDController(0.01, 0.0006, 0.0);

    @AutoMethod public MecanumObject(LinearOpMode lop) {
        super(lop, lop);
    }
    public MecanumObject(OpMode opm) {
        super(null, opm);
    }
    @AutoMethod public boolean turnAnglePID(double angle, double originalHeading, double tolerance) throws InterruptedException {
        originalHeading = Math.abs(originalHeading);
        heading = Math.abs(imu.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(AxesOrder.ZYX).firstAngle);
        Trace.log(" turnAnglePID heading = " + heading);
        heading -= originalHeading;
        if (heading > 180) heading -= 360;
        heading = Math.abs(heading);
        if (Math.abs(heading - Math.abs(angle)) < TURNING_CORRECTION) {
            if (!turnFlag) {
                turnFlag = true;
                turnAdjust.reset();
                setMotorPowers(0,0,0);
                Thread.sleep(250);
            }
            Trace.log(" Hey!");
            turnAdjust.calculateError(Math.signum(angle) * Math.signum(Math.abs(angle) - heading) * Math.abs((Math.abs(angle) - heading)));
            turnPower = Math.signum(turnAdjust.totalCorrection)*Range.clip(Math.abs(turnAdjust.totalCorrection), 0.165, 0.25);
        } else {
            turnFlag = false;
            turnPower = Math.signum(angle) * 0.35;
        }

        setMotorPowers(0,0,turnPower);
        Trace.log(" Turn Power:  " + turnPower);
        Thread.sleep(100);
        if (Math.abs(heading - Math.abs(angle)) >= tolerance) {
            return true;
        } else {
            turnAdjust.reset();
            Thread.sleep(250);
            heading = Math.abs(imu.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(AxesOrder.ZYX).firstAngle);
            heading -= originalHeading;
            if (heading > 180) heading -= 360;
            heading = Math.abs(heading);
            return (Math.abs(heading - Math.abs(angle)) >= tolerance);
        }
    }
    @AutoMethod public boolean turnAngle(double angle, double originalHeading) throws InterruptedException {
        heading = Math.abs(imu.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(AxesOrder.ZYX).firstAngle);

        heading -= originalHeading;
        if (heading > 180) heading -= 360;
        heading = Math.abs(heading);

        turnPower = Math.signum(angle)*Math.signum(Math.abs(angle)-heading)*Range.clip(Math.abs((Math.abs(angle)-heading))/90,0.12,1);


        Trace.log("Turn Power:  " + turnPower);
        Trace.log("Angle Error: " + Math.abs(heading - Math.abs(angle)));
        //if (Math.abs(turnPower) < 0.1) return false;

        setMotorPowers(0,0, turnPower);

        if (Math.abs(heading - Math.abs(angle)) >= 2.5) {
            return true;
        } else {
            setMotorPowers(0, 0, 0);
            Thread.sleep(250);
            heading = Math.abs(imu.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(AxesOrder.ZYX).firstAngle);
            heading -= originalHeading;
            if (heading > 180) heading -= 360;
            heading = Math.abs(heading);
            return (Math.abs(heading - Math.abs(angle)) >= 2.5);
        }
    }
    private void setMotorPowers (double power, double direction, double rotation) {
        direction = Math.toRadians(direction);

        motorPowerFL = 1.414*power*Math.sin(direction - (Math.PI / 4)) + rotation;
        motorPowerFR = 1.414*power*Math.cos(direction - (Math.PI / 4)) - rotation;
        motorPowerBL = 1.414*power*Math.cos(direction - (Math.PI / 4)) + rotation;
        motorPowerBR = 1.414*power*Math.sin(direction - (Math.PI / 4)) - rotation;

        scaleMotorPowers();

        motorRF.setPower(motorPowerFR);
        motorLF.setPower(motorPowerFL);
        motorRB.setPower(motorPowerBR);
        motorLB.setPower(motorPowerBL);
    }
    //I'm not sure if we really need this tbh.  Drivers can account for this w/o PID, and if our IMU is more accurate, we may not need this for turning.
    public void setMotorPowersPID (double power, double direction) {
        direction = Math.toRadians(direction);
        heading = imu.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(AxesOrder.ZYX).firstAngle;
        Trace.log("Strafe heading " + heading);
        if (Double.isNaN(targetHeading)) targetHeading = heading;
        double error = strafeAdjust.calculateError(heading - targetHeading);

        motorPowerFL = 1.414*power*Math.sin(direction - (Math.PI / 4)) + error;
        motorPowerFR = 1.414*power*Math.cos(direction - (Math.PI / 4)) - error;
        motorPowerBL = 1.414*power*Math.cos(direction - (Math.PI / 4)) + error;
        motorPowerBR = 1.414*power*Math.sin(direction - (Math.PI / 4)) - error;

        scaleMotorPowers();

        motorRF.setPower(motorPowerFR);
        motorLF.setPower(motorPowerFL);
        motorRB.setPower(motorPowerBR);
        motorLB.setPower(motorPowerBL);
    }
    /**
     * Reset all the PID controllers and targets
     */
    public void resetPID() {
        strafeAdjust.reset();
        turnAdjust.reset();
        targetHeading = Double.NaN;
        heading = Double.NaN;
        turnFlag = false;
    }
    /**
     * Stop for a number of milliseconds then resume.  This will not stop the whole robot, just this subsystem
     * @param milliseconds The # of millis you wish to stop for
     */
    @AutoMethod public void stop (double milliseconds) {
        ElapsedTime stopTime = new ElapsedTime();
        while (stopTime.milliseconds() < milliseconds) {
            checkOpModeCancel();
            setMotorPowers(0, 0, 0);
        }
    }
    /**
     * Set the motor behaviour at power = 0
     * @param behavior The behaviour.  See {@link DcMotor.ZeroPowerBehavior} for choices
     */
    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        motorRF.setZeroPowerBehavior(behavior);
        motorLF.setZeroPowerBehavior(behavior);
        motorRB.setZeroPowerBehavior(behavior);
        motorLB.setZeroPowerBehavior(behavior);
    }
    public void scaleMotorPowers() {
        Double[] pwrs = {motorPowerFL, motorPowerBL, motorPowerBR, motorPowerFR};

        double max = Collections.max(Arrays.asList(pwrs));

        motorPowerFL /= max;
        motorPowerFR /= max;
        motorPowerBL /= max;
        motorPowerBR /= max;
    }

    @Override public void init() {
        motorLF = getOpm().hardwareMap.dcMotor.get("drive_fl");
        motorLB = getOpm().hardwareMap.dcMotor.get("drive_bl");
        motorRF = getOpm().hardwareMap.dcMotor.get("drive_fr");
        motorRB = getOpm().hardwareMap.dcMotor.get("drive_br");

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); //This should *theoretically* avoid slipping as much

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = getOpm().hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }
    @Override public void loop() {
        //TODO: Maybe change this controller layout?  RN it is left joystick for turn, right for strafe
        double turn = scaleControllerInput(getOpm().gamepad1.left_stick_x);
        double fwd  = scaleControllerInput(getOpm().gamepad1.right_stick_y);
        double side = scaleControllerInput(getOpm().gamepad1.right_stick_x);

        double dir = Math.atan(side / fwd) * 90 / (2 * Math.PI);

        setMotorPowers(1, dir, turn);
    }
    @Override public void stop() {
        setMotorPowers(0,0,0);
    }

    @Contract(pure = true) private double scaleControllerInput(double x) {
        return Math.pow(x, 3);
    }
}
