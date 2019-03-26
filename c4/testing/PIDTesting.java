package c4.testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import c4.lib.C4PropFile;
import c4.subsystems.Collector;

@Disabled
@TeleOp(name = "PID Testing", group = "Testing")
public class PIDTesting extends OpMode {

    public DcMotorEx motor;
    private double hingeSpeed = C4PropFile.getDouble("hingeSpeed");
    public double loweredPosition = C4PropFile.getDouble("collectorLowered");
    public double raisedPosition = C4PropFile.getDouble("collectorRaised");

    private PIDFCoefficients pidc;

    public void init() {
        motor = (DcMotorEx) hardwareMap.dcMotor.get("TEST");
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    public void loop() {
        pidc = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION);

        updatePIDC();

        telemetry();

        goToPosition();
    }

    private void goToRaised() {
        motor.setPower(hingeSpeed);
        motor.setTargetPosition((int) raisedPosition);
    }
    private void goToFolded() {
        motor.setPower(hingeSpeed);
        motor.setTargetPosition(40);
    }
    private void goToLowered() {
        motor.setPower(hingeSpeed);
        motor.setTargetPosition(((int) loweredPosition));
    }

    private void updatePIDC() {
        if(gamepad1.a) pidc.p += 0.01;
        if(gamepad1.b) pidc.p -= 0.01;
        if(gamepad1.x) pidc.i += 0.01;
        if(gamepad1.y) pidc.i -= 0.01;
        if(gamepad1.left_trigger > 0.4) pidc.d += 0.01;
        if(gamepad1.right_trigger > 0.4) pidc.d -= 0.01;

        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, pidc);
    }
    private void goToPosition() {
        if(gamepad1.left_bumper) goToRaised();
        if(gamepad1.right_bumper) goToLowered();
        if(gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right || gamepad1.dpad_up) goToFolded();
    }
    private void telemetry() {
        telemetry.addLine("PID UPDATER");
        telemetry.addLine("Left Bumper: Raise");
        telemetry.addLine("Right Bumper: Lower");
        telemetry.addLine("DPad: Fold");
        telemetry.addLine("");
        telemetry.addLine("P++: A, P--: B");
        telemetry.addLine("I++: X, I--: Y");
        telemetry.addLine("D++: Left Trigger, D--: Right Trigger");
        telemetry.addLine("");

        telemetry.addData("P", pidc.p);
        telemetry.addData("I", pidc.i);
        telemetry.addData("D", pidc.d);

        telemetry.update();
    }
}
