package c4.testing

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DigitalChannel
import com.qualcomm.robotcore.hardware.DigitalChannelController

@TeleOp(name = "LED Testing", group = "Testing")
class LEDTesting: OpMode() {

    lateinit var greenLED: DigitalChannel
    lateinit var blueLED: DigitalChannel
    lateinit var redLED: DigitalChannel

    override fun init() {
        greenLED = hardwareMap.get(DigitalChannel::class.java, "green_LED")
        blueLED = hardwareMap.get(DigitalChannel::class.java, "blue_LED")
        redLED = hardwareMap.get(DigitalChannel::class.java, "red_LED")

        blueLED.setMode(DigitalChannelController.Mode.OUTPUT);
        greenLED.setMode(DigitalChannelController.Mode.OUTPUT);
        redLED.setMode(DigitalChannelController.Mode.OUTPUT);
    }

    override fun loop() {
        greenLED.state = gamepad1.x
        blueLED.state = gamepad1.y
        redLED.state = gamepad1.a
    }
}