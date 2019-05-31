package fr.esir.mdi.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

@Path("/hello")
public class SampleWebService {
	
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

	
	public SampleWebService() {
	
		
	}

	
	@Path("/photo") 
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	@Produces(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String fileSize( @FormDataParam("imageData") String f) {
		
			String base64Image = f.split(",")[1];
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
			try {
				BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
				
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			    ImageIO.write(img, "png", byteArrayOutputStream);
			    byteArrayOutputStream.flush();
			    Mat m =  Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
			    String s = encodeToString(main.Main.drawNewImage(m),"png");
//				 System.err.println(s);
				return "data:image/png;base64,"+s;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "";
	}
	
	public static String encodeToString(BufferedImage image, String type) {
	    String imageString = null;
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();

	    try {
	        ImageIO.write(image, type, bos);
	        byte[] imageBytes = bos.toByteArray();

	        Base64.Encoder encoder = Base64.getEncoder();
	        imageString = encoder.encodeToString(imageBytes);

	        bos.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return imageString;
	}
	

	

}
