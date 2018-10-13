package c4.testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * main class for our tank drive system
 * @author Carson
 */


@Disabled
@TeleOp(name = "TankDrive Test", group = "Testing")
public class TankDrive extends OpMode {
    private DcMotor lMotor, rMotor;

    @Override
    public void init() {
        lMotor = hardwareMap.dcMotor.get("left_drive_motor");
        rMotor = hardwareMap.dcMotor.get("right_drive_motor");

        rMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        lMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void loop() {
        double leftWheel = gamepad1.left_stick_y;
        double rightWheel = gamepad1.right_stick_y;

        lMotor.setPower(leftWheel);
        rMotor.setPower(rightWheel);

        telemetry.addLine("leftWheel: " + leftWheel);
        telemetry.addLine("rightWheel: " + rightWheel);
        telemetry.update();
    }
}