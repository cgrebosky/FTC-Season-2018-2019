package c4.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import c4.subsystems.SubSystem;
import c4.subsystems.TankDrive;

@Autonomous(name = "Macro Player", group = "Testing")
public class MacroPlayer extends LinearOpMode {
    TankDrive td = new TankDrive(this, (OpMode) this);

    @Override
    public void runOpMode() throws InterruptedException {
        td.init();

        try {
            FileInputStream file = new FileInputStream(MacroRecorder.filename);
            ObjectInputStream objs = new ObjectInputStream(file);
            @SuppressWarnings("unchecked")
            LinkedList<TankDrive.DataHolder> data = (LinkedList<TankDrive.DataHolder>) objs.readObject();

            long t = System.currentTimeMillis();
            while(opModeIsActive()) {
                int elem = 0;
                long diff = data.get(elem + 1).getTime() - data.get(elem).getTime();
                if(System.currentTimeMillis() - t >= diff) elem+=1;

                td.applyData(data.get(elem));
            }

        } catch (IOException | ClassNotFoundException | SubSystem.OpModeStopException e) {
            e.printStackTrace();
            td.stop();
        }
    }
}
