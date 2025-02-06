package video

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import nu.pattern.OpenCV
import org.opencv.core.Mat
import org.opencv.videoio.VideoCapture
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import javax.swing.JFrame
import javax.swing.JPanel


class VideoUtil {

    lateinit var image:BufferedImage

    var _status = MutableStateFlow(VideoUtil.Status.NOT_READY)
    val status : StateFlow<Status> = _status

    enum class Status {
        NOT_READY,
        LOADING,
        READY
    }

    init {
        // Load the OpenCV library
        OpenCV.loadLocally()
    }

    suspend fun load(path:String) {
        _status.value = Status.LOADING
        // Specify the path to your video file
        val videoPath = path;

        // Create a VideoCapture object to open the video file
        val capture = VideoCapture(videoPath);

        // Check if the video file was opened successfully
        if (!capture.isOpened()) {
            System.out.println("Error opening video file.");
            _status.value = Status.NOT_READY
        } else {

            // Create a Mat object to store the frames
            val frame = Mat();

            // Loop through the video frames
            var frameCount = 0;
            capture.read(frame);
            this.image = matToBufferedImage(frame)
            // Release the VideoCapture object
            capture.release();
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

    companion object {
        val Stub: VideoUtil = VideoUtil()
    }

}
