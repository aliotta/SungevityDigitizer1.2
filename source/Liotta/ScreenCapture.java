package Liotta;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

class ScreenCapture {
  public BufferedImage createScreenCapture(double x, double y, int width, int height) throws
           AWTException, IOException {
     // capture the whole screen
     BufferedImage screenCapture = new Robot().createScreenCapture(
           new Rectangle((int) x,(int) y, width, height) );

	return screenCapture;
  }
}