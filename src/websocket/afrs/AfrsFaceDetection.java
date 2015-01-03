package websocket.afrs;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

// Detects faces in an image, draws boxes around them, and writes the results
// to "faceDetection.png".
class AfrsFaceDetection {

    private static final String CASCADE_FILE = "/lbpcascade_frontalface.xml";
    
    // Load the native OpenCV code once.
    static {
        System.loadLibrary("opencv_java2410");
    }

    public byte[] convert(byte [] imageData) throws IOException {
        return this.convert(ByteBuffer.wrap(imageData)).array();       
    }
    
    public ByteBuffer convert(ByteBuffer imageData) throws IOException {

        // Create a face detector from the cascade file.
        // TODO: Remove the leading slash if this is windows.
        CascadeClassifier faceDetector = new CascadeClassifier(getClass()
                .getResource(CASCADE_FILE).getPath().substring(1));
        
        Path p = Paths.get("/captureRaw.png");
        FileChannel fileOut = FileChannel.open(p, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        fileOut.write(imageData);
        //Mat oImage = Highgui.imdecode( new MatOfByte(imageData.array()), Highgui.IMREAD_UNCHANGED);
        
        //BufferedImage bi = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
        //bi = ImageIO.read(new ByteArrayInputStream(imageData.array()));
        //byte [] ba = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        //ImageIO.write(image, "png", new File("/","snap.png"));
                
        Mat oImage = new Mat(1, imageData.array().length, CvType.CV_8UC3);
        //Mat oImage = new Mat(320, 240, CvType.CV_8UC3);
        
        Path path = Paths.get("capture.png");
        java.nio.file.Files.write(path, imageData.array());
        oImage = Highgui.imread("capture.png");
        
        //oImage.put(0, 0, imageData.array());
        
        Mat nImage = new Mat(1, imageData.array().length, CvType.CV_8UC1);
        //Mat nImage = new Mat(320, 240, CvType.CV_8UC1);
        Imgproc.cvtColor(oImage, nImage, Imgproc.COLOR_BGR2GRAY);
        

        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(nImage, faceDetections);

        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            Core.rectangle(nImage, new Point(rect.x, rect.y), new Point(rect.x
                    + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }

        // convert the resulting image back to an array
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        BufferedImage imgb = (BufferedImage)toBufferedImage(nImage);
        ImageIO.write(imgb, "png", bout);
        
        ByteBuffer byteBuff = ByteBuffer.wrap(bout.toByteArray());
        return byteBuff;
    }
    
    private Image toBufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);  
        return image;

    }
    
    
    private BufferedImage mat2BufferedImage(Mat image) {
        MatOfByte byteMat = new MatOfByte();
        Highgui.imencode(".png", image, byteMat);
        byte[] bytes = byteMat.toArray();
        InputStream input = new ByteArrayInputStream(bytes);
        BufferedImage bImage = null;
        try {
            bImage = ImageIO.read(input);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bImage;
    } 
    
    /**
     * Converts/writes a Mat into a BufferedImage.
     * 
     * @param matrix Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
     */
    private BufferedImage matToBufferedImage(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int)matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;

        matrix.get(0, 0, data);

        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;

            case 3: 
                type = BufferedImage.TYPE_3BYTE_BGR;

                // bgr to rgb
                byte b;
                for(int i=0; i<data.length; i=i+3) {
                    b = data[i];
                    data[i] = data[i+2];
                    data[i+2] = b;
                }
                break;

            default:
                return null;
        }

        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);

        return image;
    }
    
}
