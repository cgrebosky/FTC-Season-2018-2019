package c4.prod.vision

import c4.lib.C4PropFile
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.corningrobotics.enderbots.endercv.OpenCVPipeline
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.core.Mat


class GoldVision(val opmode: OpMode): OpenCVPipeline() {

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

    init {
        C4PropFile.loadPropFile()
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
        var contourList = ArrayList<MatOfPoint>()

        //region vision processing
        Imgproc.cvtColor(rgba, hsl, Imgproc.COLOR_RGB2HSV)
        Imgproc.blur(hsl, blurred, blurBox)
        Core.inRange(blurred, lowerThreshhold, upperThreshhold, threshholded)
        Imgproc.findContours(threshholded, contourList, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        //endregion

        //region filtering & sorting data
        var data = getContourData(contourList)
        var dataThreshholded = ArrayList<ContourDataHolder>()
        //filter doesnt work for some reason, so I've just made another version of it here
        for (i in data) {
            if(i.area > minSize && i.area < maxSize) {
                dataThreshholded.add(i)
            }
        }
        data = dataThreshholded
        data.sortBy { it.point.y }
        data.reverse()
        //endregion

        //region telemetry
        if(!data.isEmpty()) {
            Imgproc.drawMarker(rgba, data[0].point, Scalar(0.0, 255.0, 0.0), Imgproc.MARKER_CROSS, 10, 10)

            opmode.telemetry.addLine("area: ${data[0].area}")
            opmode.telemetry.addLine("point: ${data[0].point.x}, ${data[0].point.y}")
            opmode.telemetry.addLine("")
        } else {
            opmode.telemetry.addLine("No suitable contours found")
        }
        //endregion

        //region cleanup
        //This gets rid of (I think) all the memory leaks. Just in case that doesn't get everything,
        //the garbage collector is called every 50 frames. I'm not sure that actually does anything.
        contourList.forEach { it.release() }
        rgba?.release()
        gray?.release()

        if (++count % 50 == 0) {
            System.gc()
        }
        //endregion

        return threshholded
    }

    private fun getContourData(contours: ArrayList<MatOfPoint>): ArrayList<ContourDataHolder>  {
        var data = ArrayList<ContourDataHolder>()
        for (i in contours) {
            data.add(ContourDataHolder.fromContour(i))
        }
        return data
    }
}