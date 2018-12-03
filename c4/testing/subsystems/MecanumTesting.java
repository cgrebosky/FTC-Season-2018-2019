package c4.testing.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import c4.subsystems.MecanumObject;

@TeleOp(name = "Mecanum Testing", group = "Testing")
public class MecanumTesting extends OpMode {

    private MecanumObject mecanumObject = new MecanumObject(this);

    @Override
    public void init() {
        mecanumObject.init();
    }

    @Override
    public void loop() {
        mecanumObject.loop();


        mecanumObject.telemetry();
    }

    @Override
    public void stop() {
        mecanumObject.stop();
    }
}
