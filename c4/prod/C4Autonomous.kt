package c4.prod

import c4.lib.*
import c4.lib.subsystems.SubSystem
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous(name = "C4 Autonomous", group = "Prod")
class C4Autonomous: LinearOpMode() {
    //Need to set these variables before the actual runOpMode to ignore the stuck-in-loop detection
    init {
        msStuckDetectInit = 100_000
        msStuckDetectInitLoop = 100_000
        msStuckDetectStart = 100_000
        msStuckDetectLoop = 100_000
        msStuckDetectStop = 100_000
    }

    enum class Sides { RED, BLUE }
    enum class Positions { FAR, NEAR } //far/near with respect to the crater.

    var team: Sides? = null
    var position: Positions? = null


    private val xControlled = ControllerHelper()
    private val aControlled = ControllerHelper()

    override fun runOpMode() {
        try {
            TelemetryHelper.init(this)

            var ptr = 0

            Team@ while (team == null && opModeIsActive()) {
                team = TelemetryHelper.chooseFromList(
                        caption = "Press X to toggle, press A to choose",
                        list = Sides.values(),
                        ptr = ptr,
                        choose = aControlled.press(gamepad1.a)
                )

                if (xControlled.press(gamepad1.x)) ptr = ptr.loopingInc(max = 1)

            }
            Position@ while (position == null && opModeIsActive()) {
                position = TelemetryHelper.chooseFromList(
                        caption = "Press X to toggle, press A to choose",
                        list = Positions.values(),
                        ptr = ptr,
                        choose = aControlled.press(gamepad1.a)
                )

                if (xControlled.press(gamepad1.x)) ptr = ptr.loopingInc(max = 1)
            }

            telemetry.addLine("Team: $team")
            telemetry.addLine("Position: $position")
            telemetry.update()

            waitForStart()
        } catch (e: SubSystem.OpModeStopException) {
            Trace.log("Autonomous stopped prematurely")
        }
    }
}