package websocket.afrs;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.imread;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.opencv_contrib.FaceRecognizer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.*;

import static org.bytedeco.javacpp.opencv_contrib.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class AfrsFaceRecognizer {
    
        private AfrsFaceDetection afd = new AfrsFaceDetection(); 
        private FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
//      private FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
//      private FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
        
        final String TEST_IMAGE = "c:\\work\\hackathon\\test\\test.jpg";
        final String TRAINING_DIR = "c:\\work\\hackathon\\training";
        
    public void train(){
        
        File root = new File(TRAINING_DIR);

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
            }
        };

        File[] imageFiles = root.listFiles(imgFilter);

        MatVector images = new MatVector(imageFiles.length);
        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.getIntBuffer();

        int counter = 0;
        for (File image : imageFiles) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
            String s = image.getName().split("\\-")[0];
            int label = Integer.parseInt(s);
            images.put(counter, img);
            labelsBuf.put(counter, label);
            counter++;
        }

        faceRecognizer.train(images, labels);
    }
        
     public FaceMatch compareImage(ByteBuffer bb) throws IOException {  
         train();
//        Mat testImage = imread(TEST_IMAGE, CV_LOAD_IMAGE_GRAYSCALE);
         int predictedLabel = -1;
            ByteBuffer gbb = afd.getGreyByteBuffer(bb);
            InputStream in = new ByteArrayInputStream(gbb.array());
            BufferedImage bi = ImageIO.read(in);
            Mat mat = Mat.createFrom(bi);
            int[] ia = new int[1];
            double[] da = new double[1];
            faceRecognizer.predict(mat,ia,da);
         
         return new FaceMatch(ia[0], da[0]);
    }
}
