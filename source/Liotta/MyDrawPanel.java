package Liotta;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class MyDrawPanel extends JPanel{
	//initialization
	private BufferedImage setImage;
	private int imageHeight;
	private int imageWidth;
	public double rotationRequired;
	public ArrayList<Point> pointList = new ArrayList<Point>();

	//paints the image from the clipboard to the drawPanel
	public void paint(Graphics g) {
		super.paintComponent(g);
		//paints image with a small 50 pixel buffer from top and edge and with a size that can change
		g.drawImage(setImage, 50, 50, imageWidth, imageHeight, this);
		if (pointList.isEmpty() == false){
			for (int i=0; i < pointList.size();i++){
				
				g.setColor(Color.red);
				g.drawOval((int) pointList.get(i).getX()-1, (int) pointList.get(i).getY()-1, 2, 2);

			}
			
		}
	}
	//TODO when clear data button is pressed clear arraylist

	// get image off clipboard and return the image
	public BufferedImage getImageFromClipboard() {
  		Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
   			try {
				return (BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor);
			}//end try
    			catch (UnsupportedFlavorException e) {
      				// handle this as desired
      				e.printStackTrace();
    			} //end catch
    			catch (IOException e) {
     				// handle this as desired
				e.printStackTrace();
    			} //end catch
  		} //end if
  		else {
    			System.err.println("getImageFromClipboard: That wasn't an image!");
  		}//end else
  		return null;
	} //end method
	
	// sets the captured image from clipboard to the image that will be drawn and gathers info about how large it will be
	public void setImage(BufferedImage myImage) {
		setImage = myImage;
		imageWidth = myImage.getWidth();
		imageHeight = myImage.getHeight();
		repaint();
	}//end method

	//increases the size of the image by 1.5 if the user clicks the resize button
	public void resizeImage() {
		imageWidth = (int) Math.round(imageWidth * 1.5);
		imageHeight = (int) Math.round(imageHeight * 1.5);
		repaint();
	}//end method
	
	public void rotateImage(BufferedImage myImage, double rotationRequired) {
		//initialize so multiple presses dont keep translating image by 50,50
		
		imageHeight = myImage.getHeight();
		imageWidth = myImage.getWidth();
		//change size of image so after rotate it fits
		int widthDelta = (int)Math.abs(Math.floor(imageWidth*Math.cos(rotationRequired)+imageHeight*Math.sin(rotationRequired))-imageWidth), heightDelta = (int)Math.abs(Math.floor(imageHeight*Math.cos(rotationRequired)+imageWidth*Math.sin(rotationRequired))-imageHeight);
		imageHeight = (int)heightDelta + imageHeight ;
		imageWidth = (int)widthDelta + imageWidth;
		//if(myImage.getWidth()>400 || myImage.getHeight()>400){
		//	imageHeight = (int)imageHeight*7/10;
		//	imageWidth = (int)imageWidth*7/10;
		//}
		//create image to use after affietransform
		BufferedImage afterRotate = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		// Rotation information

		
		
		//set up rotation and translation affine transform through center of image
		AffineTransform tx = new AffineTransform();
		tx.translate(widthDelta/2,heightDelta/2);
		tx.translate(myImage.getWidth() / 2,myImage.getHeight() / 2);
		tx.rotate(-1*rotationRequired);
		tx.translate(-myImage.getWidth() / 2,-myImage.getHeight() / 2);
		//if(myImage.getWidth()>400 || myImage.getHeight()>400){
		//	tx.scale(.7,.7);
		//}
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		// Drawing the rotated image at the required drawing locations
		setImage = op.filter(myImage, afterRotate);
		repaint();
	}//end method
	
	public void addPoint(Point ovalLocation){
		pointList.add(ovalLocation);
		repaint();
	}
	
	public void clearPointList(){
		pointList.clear();
		repaint();
	}



}//end class