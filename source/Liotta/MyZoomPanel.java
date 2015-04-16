package Liotta;

import java.awt.*;
import java.awt.image.*;
import java.awt.datatransfer.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.AffineTransform;

public class MyZoomPanel extends JPanel{ 
    private BufferedImage setImage;
    private int imageHeight;
	private int imageWidth;
	private int buffer = 150;//used to push zoombox away from  buttons
	private float fWidth = 12f; //scale factor
	private float fHeight = 12f;//scale factor

	public void paint(Graphics g) {
		super.paintComponent(g);
		//set color to red
		g.setColor(Color.red);
		//draw image in zoombox while cursor is moving
		g.drawImage(setImage, 0, buffer, imageWidth, imageHeight, this);
		//draw verticle crosshair (int x, int y, int width, int height)
		g.drawRect (30, imageHeight/2+buffer, imageWidth-60, 1); 
		//draw horizontal crosshair
		g.drawRect (imageWidth/2, buffer+30, 1, imageHeight-60); 

	}

	public void setImage(BufferedImage myImage) {
		//get image height and width variables
		imageWidth = myImage.getWidth();
		imageHeight = myImage.getHeight();
	/**scale image**/
		//create image to use after affinetransform
		BufferedImage afterScaling = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		//initialize affine transform
		AffineTransform at = new AffineTransform();	
		//set translate of affinetransform
		at.translate(-(fWidth-1)*imageWidth/2,-(fHeight-1)*imageHeight/2);	
		//set scale of affinetransform
		at.scale(fWidth, fHeight);
		//initialize affinetransform operation
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		//make image to be painted the scaled image
		setImage = scaleOp.filter(myImage, afterScaling);
		repaint();
	}//end method


}