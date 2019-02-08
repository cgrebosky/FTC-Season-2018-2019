package c4.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ResourceBundle;

import c4.lib.C4PropFile;
import c4.lib.ControllerHelper;

public class Collector extends SubSystem {

    public enum CollectorPosition {
        FOLDED, RAISED, LOWERED
    }

    public Lift lift; //Exists so we can automatically put this in vertical position

    public static CollectorPosition currentPosition = CollectorPosition.FOLDED;

    private CRServo spinner;
    public DcMotor hinge;
    public DcMotor extender;

    private double collectorSpeed = C4PropFile.getDouble("collectorSpeed");
    public double loweredPosition = C4PropFile.getDouble("collectorLowered");
    public double raisedPosition = C4PropFile.getDouble("collectorRaised");
    private double hingeSpeed = C4PropFile.getDouble("hingeSpeed");

    private ControllerHelper aPress = new ControllerHelper();
    private ControllerHelper bPress = new ControllerHelper();

    public Collector(LinearOpMode lop, OpMode opm) {
        super(lop, opm);
    }
    public Collector(OpMode opm) {
        super(null, opm);
    }

    @Override public void init() {
        hinge = getOpm().hardwareMap.dcMotor.get("collector_hinge");
        hinge.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        extender = getOpm().hardwareMap.dcMotor.get("collector_extender");
        extender.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        spinner = getOpm().hardwareMap.crservo.get("collector_spinner");
    }
    @Override public void loop() {
        if(getOpm().gamepad2.right_bumper) collect();
        else if(getOpm().gamepad2.left_bumper) push();
        else zero();

        aPress.press(getOpm().gamepad2.a);
        bPress.press(getOpm().gamepad2.b);

        if(currentPosition == CollectorPosition.RAISED) {
            if(aPress.getState()) goToLowered();
            else if(bPress.getState()) goToFolded();
        } else if(currentPosition == CollectorPosition.LOWERED) {
            if(aPress.getState()) goToRaised();
            else if(bPress.getState()) goToFolded();
            // ^Not recommended, but I guess it could be useful /sometimes/
        } else if(currentPosition == CollectorPosition.FOLDED){
            if(aPress.getState()) goToLowered();
            else if(bPress.getState()) goToRaised();
        }


        //Positive extends outward
        double pow = -getOpm().gamepad2.left_trigger + getOpm().gamepad2.right_trigger;

        if(extender.getCurrentPosition() <= 30 && pow < 0) pow = 0;
        if(extender.getCurrentPosition() >= 1450 && pow > 0) pow = 0;
        if(extender.getCurrentPosition() <= 300 && extender.getCurrentPosition() > 30 && pow < 0) pow /= 3;

        extender.setPower(pow);


    }
    @Override public void telemetry() {
        getOpm().telemetry.addLine("Collector\n");
        getOpm().telemetry.addData("    Hinge Position", hinge.getCurrentPosition());
        getOpm().telemetry.addData("    Spinner Power", spinner.getPower());
        getOpm().telemetry.addData("    Extender Position", extender.getCurrentPosition());
        getOpm().telemetry.addData("    Extender Power", extender.getPower());

    }
    @Override public void stop() {
        spinner.setPower(0);
    }

    public void goToRaised() {
        hinge.setPower(hingeSpeed);
        hinge.setTargetPosition((int) raisedPosition);
        currentPosition = CollectorPosition.RAISED;
    }
    private void goToFolded() {
        hinge.setPower(hingeSpeed);
        hinge.setTargetPosition(0);
        currentPosition = CollectorPosition.FOLDED;
    }
    public void goToLowered() {
        hinge.setPower(hingeSpeed);
        hinge.setTargetPosition(((int) loweredPosition));
        currentPosition = CollectorPosition.LOWERED;
    }
    @AutoMethod public void goToHovering() {
        hinge.setPower(C4PropFile.getDouble("autoHingeSpeed"));
        hinge.setTargetPosition(C4PropFile.getInt("hover"));
        currentPosition = CollectorPosition.LOWERED;
    }

    public void collect() {
        spinner.setPower(-collectorSpeed);
    }
    public void push() {
        spinner.setPower(collectorSpeed);
    }
    public void zero() {
        spinner.setPower(0.0);
    }
    @AutoMethod public void extend() {
        while (extender.getCurrentPosition() < 1300) extender.setPower(0.7);
        extender.setPower(0.0);
    }
    @AutoMethod public void retract() {
        while (extender.getCurrentPosition() > 100) extender.setPower(-0.5);
        extender.setPower(0.0);
    }

}
