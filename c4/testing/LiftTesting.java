package c4.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import c4.subsystems.Lift;

@TeleOp(name = "Lift Testing", group = "Testing")
public class LiftTesting extends OpMode {

    private Lift lift = new Lift(null, this);

    @Override
    public void init() {
        lift.init();
    }

    @Override
    public void loop() {
        lift.loop();

        lift.telemetry();
        telemetry.update();
    }
}
