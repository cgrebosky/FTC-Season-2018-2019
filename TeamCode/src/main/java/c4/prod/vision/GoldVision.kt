package c4.prod.vision

import c4.lib.C4PropFile
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.corningrobotics.enderbots.endercv.OpenCVPipeline
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.core.Mat

/**
 * A class that controls our vision for the gold objects in autonomous.  This will give us the best
 * contour (in the form of {@link #ContourDataHolder}) that matches our desired object.
 *
 * @param opmode the opmode
 */
class GoldVision(val opmode: OpMode): OpenCVPipeline() {

    /**
     * A class that solely has the data we need.  Accessing data from MatOfPoints is kind of
     * difficult, so this just makes that easier.
     *
     * @param point the _CENTER_ of our object
     * @param area the total area in pixels of our object
     */
    private data class ContourDataHolder(val point: Point, val area: Double) {
        companion object {
            fun fromContour(contour: MatOfPoint): ContourDataHolder {
                val boundingRect = Imgproc.boundingRect(contour)
                val point = Point(
                        boundingRect.x + (boundingRect.width / 2).toDouble(),
                        boundingRect.y + (boundingRect.height / 2).toDouble()
                )
                val area = Imgproc.contourArea(contour)

                return ContourDataHolder(point, area)
            }
        }
    }

    val minSize = 1400
    val maxSize = 3000

    var mHierarchy = Mat()
    var threshholded = Mat()
    var hsl = Mat()
    var blurred = Mat()

    var blurBox = Size(C4PropFile.getDouble("blur"),C4PropFile.getDouble("blur"))
    var lowerThreshhold: Scalar = C4PropFile.getScalar("lowerThreshold")
    var upperThreshhold: Scalar = C4PropFile.getScalar("upperThreshold")

    var count = 0

    @Override
    override fun processFrame(rgba: Mat?, gray: Mat?): Mat {
        var contourList = processVision(rgba)
        var data = sortData(getContourData(contourList))

        Imgproc.drawMarker(rgba, data[0].point, Scalar(0.0, 255.0, 0.0), Imgproc.MARKER_CROSS, 10, 3)

        handleTelemetry(data)

        //This gets rid of (I think) all the memory leaks. Just in case that doesn't get everything,
        //the garbage collector is called every 50 frames.
        //Unfortunately, this cannot be outsourced to another function.  You need to release the
        //objects, and all functions are pass-by-reference in jvm, so this remains ugly :/
        contourList.forEach { it.release() }
        rgba?.release()
        gray?.release()
        if (++count % 50 == 0) {
            System.gc()
        }

        return threshholded
    }

    /**
     * Gets data from an ArrayList of MatOfPoints and converts it to an ArrayList of
     * { @link #ContourDataHolder }.
     */
    private fun getContourData(contours: ArrayList<MatOfPoint>): ArrayList<ContourDataHolder>  {
        var data = ArrayList<ContourDataHolder>()
        for (i in contours) {
            data.add(ContourDataHolder.fromContour(i))
        }
        return data
    }

    /**
     * Handle the telemetry.  Note that this *just* creates the telemetry, it does not update it.
     *
     * @param data The data which we shall display
     */
    private fun handleTelemetry(data: ArrayList<ContourDataHolder>) {
        if(!data.isEmpty()) {
            opmode.telemetry.addLine("area: ${data[0].area}")
            opmode.telemetry.addLine("point: ${data[0].point.x}, ${data[0].point.y}")
            opmode.telemetry.addLine("")
        } else {
            opmode.telemetry.addLine("No suitable contours found")
        }
    }

    /**
     * Sort the data.  This will arrange it in the form that we want, and cull all data that does not
     * match our parameters.
     */
    private fun sortData(data: ArrayList<ContourDataHolder>): ArrayList<ContourDataHolder> {
        var dataThreshholded = ArrayList<ContourDataHolder>()
        //ArrayList.filter doesn't work for some reason, so I've just made another version of it here
        for (i in data) {
            if(i.area > minSize && i.area < maxSize) {
                dataThreshholded.add(i)
            }
        }
        var tmp = dataThreshholded
        tmp.sortBy { it.point.y }
        tmp.reverse()

        return tmp
    }

    /**
     * Process our frame.
     *
     * @param rgba the frame at this moment in rgba form.
     * @return A list of contours that match our parameters (only the color parameter).
     */
    private fun processVision(rgba: Mat?): ArrayList<MatOfPoint> {
        var contourList = ArrayList<MatOfPoint>()

        Imgproc.cvtColor(rgba, hsl, Imgproc.COLOR_RGB2HSV)
        Imgproc.blur(hsl, blurred, blurBox)
        Core.inRange(blurred, lowerThreshhold, upperThreshhold, threshholded)
        Imgproc.findContours(threshholded, contourList, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        return contourList
    }

}