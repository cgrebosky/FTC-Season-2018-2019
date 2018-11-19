package c4.testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import c4.lib.Trace;
import c4.subsystems.TankDrive;

@TeleOp(name = "MacroRecorder", group = "Testing")
public class MacroRecorder extends OpMode {

    public static final String filename = "MacroRecording";

    List<TankDrive.DataHolder> data = new LinkedList<>();
    TankDrive td = new TankDrive(null, this);

    @Override
    public void init() {
        td.init();
    }

    @Override
    public void loop() {
        data.add(new TankDrive.DataHolder(td));
    }

    public void stop() {
        td.stop();

        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream objs = new ObjectOutputStream(file);

            objs.writeObject(data);

            file.close();
            objs.close();

            Trace.log("Data successfully Serialized and Written");
        } catch (IOException e) {
            Trace.log("Unable to find file");
            e.printStackTrace();
        }
    }
}
