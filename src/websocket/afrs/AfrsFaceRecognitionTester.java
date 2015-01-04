package websocket.afrs;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import static org.bytedeco.javacpp.opencv_contrib.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

/**
 * For the simplicity of this post, the class also requires that the training images have
 * filename format: <label>-rest_of_filename.png. For example:
 *
 * 1-jon_doe_1.png
 * 1-jon_doe_2.png
 * 2-jane_doe_1.png
 * 2-jane_doe_2.png
 * ...and so on.
 *
 * Source: http://pcbje.com/2012/12/doing-face-recognition-with-javacv/
 */
public class AfrsFaceRecognitionTester {
    public static void main(String[] args) {
//        String trainingDir = args[0];
        final String TRAINING_DIR = "c:\\work\\hackathon\\training";
        final String TEST_IMAGE = "c:\\work\\hackathon\\test\\test.jpg";
//        Mat testImage = imread(args[1], CV_LOAD_IMAGE_GRAYSCALE);
        Mat testImage = imread(TEST_IMAGE, CV_LOAD_IMAGE_GRAYSCALE);

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

        FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
//        FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
//          FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();

        faceRecognizer.train(images, labels);

        int predictedLabel = faceRecognizer.predict(testImage);

        System.out.println("Predicted label: " + predictedLabel);
    }
}
