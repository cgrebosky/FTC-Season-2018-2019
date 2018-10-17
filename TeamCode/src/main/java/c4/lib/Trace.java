package c4.lib;

import android.util.Log;

/**
 * A simple class with static methods for writing messages to the logcat file with the prefix
 * "C4: ". Can enable/disable message logging by setting traceEnable to true or false.
 */

public class Trace {
    public static boolean traceEnable = true;

    /**
     * Log to our log files.  Messages delivered from this function shall be prefixed with "C4:"
     * Messages will only appear if {@link #traceEnable} is true
     *
     * @param msg the message to log, not including prefix
     */
    public static void log( String msg ) {
        if ( traceEnable ) {
            //DbgLog.msg( "C4: " + msg );
            //RobotLog.d( "C4: " + msg );
            Log.d("C4", msg); // Log.d is for debug, Log.i is for info
        }
    }

    /**
     * @see #log(String)
     * @param msg {@see #log(String)}
     */
    public static void msg( String msg ) {
        log( msg );
    }
}
