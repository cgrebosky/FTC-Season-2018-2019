package c4.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import c4.lib.C4PropFile;

public class DepositorMotorController {

    public DcMotor motor;

    final double pow = C4PropFile.getDouble("depositorPower");

    public void goUp() {
        motor.setPower(pow * errPow(
                C4PropFile.getInt("depositorUp") - motor.getCurrentPosition()
        ));
    }
    public void goDown() {
        motor.setPower(pow / 3 * errPow(
                C4PropFile.getInt("depositorDown") - motor.getCurrentPosition()
        ));
    }

    public double errPow(int error) {
        double err = Math.signum(error) * Math.pow(
                Math.abs(error / C4PropFile.getDouble("depositorDiv")),
                C4PropFile.getDouble("depositorExp")
        );
        return err;
    }
}
