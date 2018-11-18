package c4.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import c4.prod.C4Autonomous;
import c4.subsystems.Lift;
import c4.subsystems.TankDrive;

@Autonomous(name = "Auto Test", group = "Testing")
public class AutoTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        TankDrive td = new TankDrive(this, this);
        td.init();
        Lift l = new Lift(this, this);
        l.init();

        l.raiseLift(0.2);
        sleep(2000);
        l.liftMotor.setPower(0);

        td.backward(300);
        td.forward(300);

        sleep(100);

    }
}
