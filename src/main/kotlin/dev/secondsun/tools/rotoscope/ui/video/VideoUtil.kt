package dev.secondsun.tools.rotoscope.ui.video

//import org.bytedeco.javacpp.Loader
//import org.bytedeco.opencv.opencv_java
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.path
import jthemedetecor.util.OsInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.bytedeco.javacpp.Loader
import org.bytedeco.opencv.opencv_java
import org.opencv.core.Mat
import org.opencv.videoio.VideoCapture
import org.opencv.videoio.Videoio
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte


class VideoUtil(val path: String) {

    private var capture: VideoCapture
    var _image = MutableStateFlow(BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB))
    val image : StateFlow<BufferedImage> = _image

    var _status = MutableStateFlow(Status.NOT_READY)
    val status : StateFlow<Status> = _status

    enum class Status {
        NOT_READY,
        LOADING,
        READY
    }

    init {
        // Load the OpenCV library

         if (OsInfo.isMacOsMojaveOrLater) {
            System.loadLibrary("opencv_java4120")
        } else {
             Loader.load(opencv_java::class.java)
        }



        _status.value = Status.LOADING
        // Specify the path to your video file
        val videoPath = path

        // Create a VideoCapture object to open the video file
        this.capture = VideoCapture(videoPath)

        // Check if the video file was opened successfully
        if (!capture.isOpened) {
            System.out.println("Error opening video file.")
            _status.value = Status.NOT_READY
        } else {

            // Create a Mat object to store the frames
            val frame = Mat()

            // Loop through the video frames
            var frameCount = 0
            capture.read(frame)
            _image.value = matToBufferedImage(frame)
            println("Frame ${frame}")
            // Release the VideoCapture object
            _status.value = Status.READY
        }
    }

    private fun matToBufferedImage(frame: Mat): BufferedImage {
        var type = 0
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR
        }
        val image = BufferedImage(frame.width(), frame.height(), type)
        val raster = image.raster
        val dataBuffer = raster.dataBuffer as DataBufferByte
        val data = dataBuffer.data
        frame[0, 0, data]

        return image
    }

    //seeks video to the percent location
   // ie seek .5 changes the frame to the one in the middle of the
   // video
    fun seek(it: Float) {
        capture.set(Videoio.CAP_PROP_POS_MSEC, it.toDouble() * (200f/30f) *1000)

        val framecount = capture.get(Videoio.CAP_PROP_FRAME_COUNT)
        val fps = capture.get(Videoio.CAP_PROP_FPS)
        println("Framecount $framecount, fps $fps, frame ${framecount*it.toDouble()}")
        // Create a Mat object to store the frames
        val frame = Mat()

        // Loop through the video frames
        var frameCount = 0
        capture.read(frame)
        println("Seek ${it.toDouble()}, frame (${frame.width()})x(${frame.height()})")
        _image.value = matToBufferedImage(frame)
        // Release the VideoCapture object


    }


}
 object VideoUtilBuilder {
    fun open(file: PlatformFile): VideoUtil{
        return VideoUtil(file.path)
    }
}