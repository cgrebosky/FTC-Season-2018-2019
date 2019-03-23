package c4.prod

import c4.lib.C4PropFile
import c4.subsystems.*
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.Servo

@TeleOp(name = "C4 Teleop", group = "Prod")
class C4TeleOp: OpMode() {

    var calibrating = false
    var liftCalib = false
    var extenderCalib = false
    var hingeCalib = false

    init {
        C4PropFile.loadPropFile()
    }

    private val mecanum = MecanumObject(this)
    private val lift = Lift(opm = this)
    private val flicker = Flicker(opm = this)
    private val collector = Collector(this)
    private val depositor = MineralDepositor(this)
    private val vision = ResourceDetector(null, this);

    private lateinit var extenderLimit: DigitalChannel
    private lateinit var hingeLimit: DigitalChannel

    override fun init() {
        C4PropFile.loadPropFile()

        extenderLimit = hardwareMap.get(DigitalChannel::class.java, "limit_extender")
        hingeLimit = hardwareMap.get(DigitalChannel::class.java, "limit_hinge")

        collector.init()
        mecanum.init()
        lift.init()
        flicker.init()
        depositor.init()
        vision.teleInit()

        collector.lift = lift

        telemetry.addLine("Ready")
        telemetry.update()
    }

    override fun loop() {

        calibrating = gamepad2.x && gamepad1.x

        if(!calibrating) {
            collector.loop()
            collector.telemetry()
            mecanum.loop()
            mecanum.telemetry()
            lift.loop()
            lift.telemetry()
            flicker.loop()
            flicker.telemetry()
            depositor.loop()
            depositor.telemetry()
            //vision.telemetry()
            vision.loop()
        } else {
            telemetry.addLine("CALIBRATING")


            if(!extenderLimit.state)
                collector.extender.power = -0.7
            else if(!extenderCalib) {
                extenderCalib = true
                collector.extender.power = 0.0
                collector.extender.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
                telemetry.addLine("EXTENDER DONE")
                collector.extender.mode = DcMotor.RunMode.RUN_USING_ENCODER
            }

            if(!hingeLimit.state) {
                collector.hinge.mode = DcMotor.RunMode.RUN_USING_ENCODER
                collector.hinge.power = -0.5
            } else {
                hingeCalib = true
                collector.hinge.power = 0.0
                collector.hinge.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
                telemetry.addLine("HINGE DONE")
                collector.hinge.mode = DcMotor.RunMode.RUN_TO_POSITION
            }

            if(!lift.upperTouchSensor.state)
                lift.lowerLift(0.7)
            else if(!liftCalib) {
                liftCalib = true
                lift.lowerLift(0.0)
                lift.liftMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
                telemetry.addLine("LIFT DONE")
                lift.liftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

            }
        }

        telemetry.addData("hingeState", hingeLimit.state)
        telemetry.update()
    }

    override fun stop() {
        collector.stop()
        mecanum.stop()
        lift.stop()
        flicker.stop()
        depositor.stop()
        vision.stop()
    }
}