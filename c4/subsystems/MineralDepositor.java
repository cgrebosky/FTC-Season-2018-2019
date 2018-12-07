package c4.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import c4.lib.C4PropFile;
import c4.lib.ControllerHelper;

public class MineralDepositor extends SubSystem {

    public UniversalFlicker leftArm, rightArm, leftLatch, rightLatch;

    public ControllerHelper aPress = new ControllerHelper();

    public MineralDepositor(LinearOpMode lop, OpMode opm) {
        super(lop, opm);
    }
    public MineralDepositor(OpMode opm) {
        super(null, opm);
    }

    @Override public void init() {
        leftArm = new UniversalFlicker(
                getOpm().hardwareMap.servo.get("arm_left"),
                C4PropFile.getDouble("leftArmUp"),
                C4PropFile.getDouble("leftArmDown")
        );
        leftArm.start();
        leftArm.setSpeed(C4PropFile.getDouble("armSpeed"));
        leftArm.slowClose();

        rightArm = new UniversalFlicker(
                getOpm().hardwareMap.servo.get("arm_right"),
                C4PropFile.getDouble("rightArmUp"),
                C4PropFile.getDouble("rightArmDown")
        );
        rightArm.start();
        rightArm.setSpeed(C4PropFile.getDouble("armSpeed"));
        rightArm.slowClose();

        leftLatch = new UniversalFlicker(
                getOpm().hardwareMap.servo.get("latch_left"),
                C4PropFile.getDouble("leftLatchOpen"),
                C4PropFile.getDouble("leftLatchClosed")
        );
        leftLatch.start();
        leftLatch.fastOpen();

        rightLatch = new UniversalFlicker(
                getOpm().hardwareMap.servo.get("latch_right"),
                C4PropFile.getDouble("rightLatchOpen"),
                C4PropFile.getDouble("rightLatchClosed")
        );
        rightLatch.start();
        rightLatch.fastOpen();
    }
    @Override public void loop() {
        boolean aPressed = getOpm().gamepad1.a;

        if(Collector.currentPosition != Collector.CollectorPosition.RAISED) {
            if(aPress.toggle(aPressed)) {
                if(!getOpm().gamepad2.y) {
                    leftLatch.fastClose();
                    rightLatch.fastClose();
                }

                leftArm.slowOpen();
                rightArm.slowOpen();
            } else {
                leftArm.slowClose();
                rightArm.slowClose();

                leftLatch.fastOpen();
                rightLatch.fastOpen();
            }
        }

        if(getOpm().gamepad2.y) {
            leftLatch.fastOpen();
            rightLatch.fastOpen();
        }
    }
    @Override public void stop() {
        //So this will slam down in TeleOp, but I can't really think of any other way to do it
        leftArm.kill();
        leftLatch.kill();
        rightArm.kill();
        rightLatch.kill();
    }
    @Override public void telemetry() {
        getOpm().telemetry.addLine("Mineral Depositor");
        getOpm().telemetry.addData("    Left Arm Position", leftArm.getPosition());
        getOpm().telemetry.addData("    Right Arm Position", rightArm.getPosition());
    }
}
