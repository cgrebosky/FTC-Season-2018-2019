package c4.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import c4.lib.C4PropFile;
import c4.lib.ControllerHelper;

public class MineralDepositor extends SubSystem {

    final double pow = C4PropFile.getDouble("depositorPower");
    final double dpow = C4PropFile.getDouble("depositorDownPower");

    public UniversalFlicker leftLatch, rightLatch;
    public DcMotor arms;

    public ControllerHelper aPress = new ControllerHelper();

    public MineralDepositor(LinearOpMode lop, OpMode opm) {
        super(lop, opm);
    }
    public MineralDepositor(OpMode opm) {
        super(null, opm);
    }

    @Override public void init() {
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

        arms = getOpm().hardwareMap.dcMotor.get("arms");
        arms.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        arms.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }
    @Override public void loop() {
        boolean aPressed = getOpm().gamepad1.a;

        if(Collector.currentPosition != Collector.CollectorPosition.RAISED) {
            if(aPress.toggle(aPressed)) {
                if(!getOpm().gamepad2.y) {
                    leftLatch.fastClose();
                    rightLatch.fastClose();
                }

                goUp();
            } else {
                goDown();

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
        goDown();
        leftLatch.kill();
        rightLatch.kill();
    }
    @Override public void telemetry() {
        getOpm().telemetry.addLine("Mineral Depositor");
        getOpm().telemetry.addData("    Arm Position", arms.getCurrentPosition());
    }

    private void goUp() {
        arms.setPower(pow * errPow(
                C4PropFile.getInt("depositorUp") - arms.getCurrentPosition()
        ));
    }
    private void goDown() {

        if(arms.getCurrentPosition() < C4PropFile.getInt("depositorSlowThresh")) arms.setPower(dpow * C4PropFile.getDouble("depositorSlowCoef"));
        else if(arms.getCurrentPosition() < C4PropFile.getInt("depositorSlowBottom")) arms.setPower(dpow);
        else arms.setPower(0);
    }

    private double errPow(int error) {
        double err = Math.signum(error) * Math.pow(
                Math.abs(error / C4PropFile.getDouble("depositorDiv")),
                C4PropFile.getDouble("depositorExp")
        );
        return err;
    }
}
