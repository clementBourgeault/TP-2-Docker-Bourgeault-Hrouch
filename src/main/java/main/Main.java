package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

public class Main {

    private static final String TROLL_IMAGE = "trollface.jpg";
    private static final String OPENCV_HAARCASCADES_HOME = "/home/barais/git/opencv/data/haarcascades/";


    
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
	public static void main(String[] args) throws Exception {
		//Mat original = Imgcodecs.imread("/home/barais/Images/PhotosLancieux/IMG_4514.JPG");

//		drawNewImage(original);
		 Server server = new Server(8080);

		    WebAppContext root = new WebAppContext();
		    root.setContextPath("/");
		    root.setDescriptor("src/main/webapp/WEB-INF/web.xml");
		    URL webAppDir = Thread.currentThread().getContextClassLoader().getResource("webapp");
		    if (webAppDir == null) {
		        throw new RuntimeException("No webapp directory was found into the JAR file");
		    }
		    root.setResourceBase(webAppDir.toURI().toString());
		    root.setParentLoaderPriority(true);

		    server.setHandler(root);
		    server.start();	
		

	}
	
	public static BufferedImage drawNewImage(Mat original)  {
        Rect[] faces = faceDetect(original);
        BufferedImage detectedImage = toBufferedImage(original);

        BufferedImage overlay = loadImage(TROLL_IMAGE);

        Graphics2D g = detectedImage.createGraphics();
        for (int i = 0; i < faces.length; i++) {
            Rect rect = faces[i];
            g.drawImage(overlay, rect.x, rect.y, rect.x + rect.width,
                    rect.y + rect.height, 0, 0, overlay.getWidth(),
                    overlay.getHeight(), null);
        }
        g.dispose();


       	return detectedImage;
			// ImageIO.write(detectedImage, "JPEG", new File("/tmp/test.jpg"));
		
	}
	
    protected static BufferedImage loadImage(String file) {
        try {
        	InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        	return ImageIO.read(in);
//            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Rect[] faceDetect(Mat image) {

		// Create a face detector from the cascade file in the resources
        // directory.
        CascadeClassifier faceDetector = new CascadeClassifier(OPENCV_HAARCASCADES_HOME
                + "haarcascade_frontalface_default.xml");

		// Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections, 1.2, 4, 0,
                new org.opencv.core.Size(10, 10), new org.opencv.core.Size(
                        1000, 1000));

        System.out.println(String.format("Detected %s faces",
                faceDetections.toArray().length));

        return faceDetections.toArray();

    }

    public static BufferedImage toBufferedImage(Mat mat) {
        MatOfByte mb = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, mb);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO
                    .read(new ByteArrayInputStream(mb.toArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

}
