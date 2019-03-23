package c4.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import c4.subsystems.MecanumObject;

@Autonomous(name = "Distance Sensor Test", group = "Testing")
public class AutonomousDistanceSensorTesting extends LinearOpMode {

    private MecanumObject mecanum;
    private DistanceSensor distanceSensor;

    @Override
    public void runOpMode() throws InterruptedException {
        mecanum = new MecanumObject(this);
        mecanum.init();

        distanceSensor = hardwareMap.get(DistanceSensor.class, "distance");

        waitForStart();

        mecanum.setMotorPowers(0.3, 0, 0);
        boolean running = true;
        while(running) {
            double d = distanceSensor.getDistance(DistanceUnit.INCH);
            if(d < 6 || d == Double.NaN) running = false;

            telemetry.addData("Distance", d);
            telemetry.update();
        }
        mecanum.setMotorPowers(0,0,0);
    }
}
