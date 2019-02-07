package c4.lib;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import kotlin.jvm.internal.Reflection;
import kotlin.text.Regex;

/**
 * C4 Robotics
 * 3/8/2017
 *
 * The C4PropFile object allows properties to be saved and retrieved from the C4Properties.txt
 * file on the phone. It was created so diagnotics and hardware characterization programs
 * can save tuning parameters to a file so they can be retrieved and used later by autonomous
 * and teleop programs, but other properties can be saved there as well.
 *
 * Note the two public methods get() and set() methods are static (along with everything else),
 * and this object cannot be instantiated (the default contructor is private).
 * This is done to avoid the situation where there are multiple instances of the C4PropFile
 * object with Hashtables in different states. It also simplifies using C4PropFile since it
 * doesn't have to be instantiated, just call C4PropFile.get() and C4PropFile.set() directly.
 *
 * The C4Properties.txt file is stored in the phone/FIRST directory and is viewable with
 * the File Manager app.
 *
 */

public class C4PropFile {
    private static String fileName = "C4Properties.txt";
    private static Hashtable ht = null;

    static {
        loadPropFile();
    }

    public static void loadPropFile() {
        loadFile();
    }

    /**
     * Retrieves the value associated with the given name in the property file.
     * Returns null if the name is not found.
     */
    public static String get( String name ) {
        if ( ht == null ) loadFile();
        String s = (String) ht.get( name );
        return s;
    }

    public static int getInt(String name) {
        return Integer.parseInt( get(name) );
    }

    public static double getDouble(String name) {
        return Double.parseDouble( get(name) );
    }

    public static double[] getDoubleArray(String name) {
        String str = get(name);
        String regex = "[^0-9.,]"; //Matches all 0-9, ',', '.' characters
        str = str.replaceAll(regex, "");

        String[] list = str.split(",");
        double[] dList = new double[list.length];

        for(int i=0; i<list.length; i++) {
            dList[i] = Double.parseDouble(list[i]);
        }
        return dList;
    }

    /**
     * Saves a name/value pair to the property file. If the property already exists it is overwritten.
     */
    public static void set( String name, String value ) {
        if ( ht == null ) loadFile();
        ht.put(name, value);
        writeFile();
    }

    /**
     * default constructor - don't allow!
     */
    private C4PropFile() {
    }

    /**
     * Load the file from /sdcard/FIRST/{@link #fileName}.  Internal function
     * @see #fileName
     */
    private static void loadFile() {
        if ( ht == null ) ht = new Hashtable();
        try {
            // open file
            File myFile = new File("/sdcard/FIRST/" + fileName); // is in the /phone/FIRST directory on the phone
            if ( !myFile.exists() ) return;
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader( new InputStreamReader(fIn) );

            // load name/value pairs into hashtable
            String line;
            String name;
            String value;
            while ( ( line = myReader.readLine() ) != null ) {
                line = line.trim();
                if ( line.length() >= 3 && !line.startsWith("//") && line.contains(":") ) { // skip comment lines (start with "//")
                    String[] strs = line.split(":", 2);
                    if ( strs.length == 2 ) { // skip lines without a ':'
                        name = strs[0].trim();
                        value = strs[1].trim();

                        int i = value.indexOf("//"); // remove comments from 'value'
                        if ( i != -1 ) {
                            value = value.substring( 0, i ).trim();
                        }

                        if ( name.length() > 0 && value.length() > 0 ) {
                            ht.put(name, value);
                        }
                    }

                }
            }

            // close file
            myReader.close();
            fIn.close();
            Trace.msg("C4Properties.txt file loaded successfully");
        } catch (Exception e) {
            Trace.msg("Error reading C4Properties.txt file: " + e.toString());
        }
    }

    /**
     * Write current hashtable to our file.
     *
     * This is VERY dangerous (I.E., it could remove our current hashtable if not properly loaded beforehand)
     * & should never be used without proper safety
     */
    public static void writeFile() {
        if ( ht == null ) return;
        try {
            // create file
            File myFile = new File("/sdcard/FIRST/" + fileName); // is in the /phone/FIRST directory on the phone
            if ( myFile.exists() ) myFile.delete();
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            BufferedWriter myWriter = new BufferedWriter(osw);

            // write timestamp at top of file
            Date date = new Date();
            myWriter.append("// Last updated: " + date.toString());
            myWriter.newLine();

            /*
            // write hastable "name:value" pairs to file
            String str;
            Enumeration pairs = ht.keys();
            while( pairs.hasMoreElements() ) {
                str = (String) pairs.nextElement();
                myWriter.append( str + ":" + ht.get(str));
                myWriter.newLine();
            }
            */

            // write hastable "name:value" pairs to file in alphabetical order (for better readability)
            String str;
            Vector vec = new Vector(ht.keySet());
            Collections.sort(vec);
            Iterator itr = vec.iterator();
            while( itr.hasNext() ) {
                str = (String) itr.next();
                myWriter.append( str + ":" + ht.get(str));
                myWriter.newLine();
            }

            // close file
            myWriter.close();
            osw.close();
            fOut.close();
            Trace.msg("C4Properties.txt file written successfully");
        } catch (Exception e) {
            Trace.msg("Error writing C4Properties.txt file: " + e.toString());
        }
    }

}
