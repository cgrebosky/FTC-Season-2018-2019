package c4.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import c4.lib.C4PropFile;
import c4.subsystems.MecanumObject;

@Autonomous(name = "Auto MecanumTesting", group = "Testing")
public class AutoMecanumTesting extends LinearOpMode {

    MecanumObject mo = new MecanumObject(this);
    @Override
    public void runOpMode() throws InterruptedException {
        C4PropFile.loadPropFile();

        mo.init();

        waitForStart();

        mo.fwdTicks(1000);
        Thread.sleep(700);
        mo.backTicks(1000);
        Thread.sleep(700);
        mo.turnDegrees(90);
        Thread.sleep(700);
        mo.turnDegrees(-90);
        Thread.sleep(700);
    }
}
