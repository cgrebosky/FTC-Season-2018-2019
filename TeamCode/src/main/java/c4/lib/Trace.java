package c4.lib;//import com.qualcomm.ftccommon.DbgLog;
//import com.qualcomm.ftccommon.RobotLog;
//import com.qualcomm.robotcore.util.RobotLog;

import android.util.Log;

/**
 * A simple class with static methods for writing messages to the logcat file with the prefix
 * "C4: ". Can enable/disable message logging by setting traceEnable to true or false.
 */

public class Trace {
    public static boolean traceEnable = true;

    public static void log( String msg ) {
        if ( traceEnable ) {
            //DbgLog.msg( "C4: " + msg );
            //RobotLog.d( "C4: " + msg );
            Log.d("C4", msg); // Log.d is for debug, Log.i is for info
        }
    }

    public static void msg( String msg ) {
        log( msg );
    }
}
