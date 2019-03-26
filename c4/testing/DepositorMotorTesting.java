package c4.testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;

import c4.lib.C4PropFile;

@Disabled
@TeleOp(name = "Depositor Motor Testing", group = "Testing")
public class DepositorMotorTesting extends OpMode {
    DcMotor motor;
    double pow;

    @Override
    public void init() {
        C4PropFile.loadPropFile();
        try {
            Thread.sleep(200);
        } catch(InterruptedException e) {}
        pow = C4PropFile.getDouble("_pow");

        motor = hardwareMap.dcMotor.get("TEST");
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop() {

        while(gamepad1.a) goUp();
        while (gamepad1.b) goDown();

        motor.setPower(0);

        telemetry.addData("Current Position", motor.getCurrentPosition());
        telemetry.addData("Current Power", motor.getPower());
        telemetry.update();
    }

    public void goUp() {
        motor.setPower(pow * errPow(
                C4PropFile.getInt("up") - motor.getCurrentPosition()
        ));
    }
    public void goDown() {
        motor.setPower(pow / 3 * errPow(
                C4PropFile.getInt("down") - motor.getCurrentPosition()
        ));
    }

    public double errPow(int error) {
        double err = Math.signum(error) * Math.pow(
                Math.abs(error / C4PropFile.getDouble("_div")),
                C4PropFile.getDouble("_exp")
        );
        return err;
    }
}
