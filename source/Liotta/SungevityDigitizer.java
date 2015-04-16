package Liotta;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;



public class SungevityDigitizer extends JPanel {

	/**initializing variables
	**/
	private Boolean isScaling = false;
	private Boolean isScaled = false; 
	private Boolean isStraightening = false; 
	private Boolean straightenedAlready = false;
	private BufferedImage myImage;
	private BufferedImage Screencapture;
	private DefaultTableModel tableModel;
	private float mouseY0;
	private float mouseY1;
	private float mouseY2;
	private float mouseY1Straight;
	private float mouseY2Straight;
	private float mouseX1Straight;
	private float mouseX2Straight;
	private int result;
	private int rowCount;
	private int totalSum=0;
	private int userValue;
	private int dataPointCount = 0;
	private int scalingIteration = 0;
	private int straighteningIteration = 0;
	private int zoomBoxWidth = 250;
	private int zoomBoxHeight = 250;
	private JFrame frame;
	private JTable table;
	private MyDrawPanel drawPanel;
	private MyZoomPanel zoomPanel;
	private ScreenCapture SCap;
	private String input;
	private Point ovalLocation;
	private double horizontal;
	private double vertical;
	private double oppadj;
	private String errorStr;


	/** Main Method
		**/

	public static void main(String[] Args) {
		//intitialize gui
		SungevityDigitizer gui = new SungevityDigitizer();
		//launch gui
		gui.go();
	}//close main

	public void go() {
		//initialize frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/** Add Components to frame
		**/
		//initialize draw panel
		drawPanel = new MyDrawPanel();
		//add mouse listeners to draw panel to track mouse movements
		drawPanel.addMouseListener(new HandlerListener());
		drawPanel.addMouseMotionListener(new HandlerListener());
		//initialize zoomPanel
		zoomPanel = new MyZoomPanel();
		//set preferred size so that the draw panel is not cut off in the gui
		zoomPanel.setPreferredSize(new Dimension(zoomBoxWidth,zoomBoxHeight));
	
		//intitialize control panel(the panel on right side with the buttons and zoom panel)
		JPanel panel = new JPanel();
		//set color of control panel
		panel.setBackground(Color.darkGray);
		//set layout to box so buttons stack neatly 
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	
		/** Initialize Buttons, Add Buttons and button listeners
		**/
	
		JButton pasteButton = new JButton("Paste");
		pasteButton.addActionListener(new PasteListener());
	
		JButton resizeButton = new JButton("Resize");
		resizeButton.addActionListener(new ResizeListener());
		
		JButton rotateButton = new JButton("Straighten");
		rotateButton.addActionListener(new RotateListener());
	
		JButton scaleButton = new JButton("Scale");
		scaleButton.addActionListener(new ScaleListener());
		
		JButton totalButton = new JButton("Total Rows");
		totalButton.addActionListener(new TotalListener());
	
		JButton clearDataButton = new JButton("Clear Data");
		clearDataButton.addActionListener(new ClearDataListener());
	
		JButton clearScaleButton = new JButton("Clear Scale");
		clearScaleButton.addActionListener(new ClearScaleListener());

		JButton deleteRowButton = new JButton("Delete Selected Row");
		deleteRowButton.addActionListener(new DeleteSelectedRowListener());
	
	
		panel.add(pasteButton);
		panel.add(resizeButton);
		panel.add(rotateButton);
		panel.add(scaleButton);
		panel.add(totalButton);
		panel.add(clearDataButton);
		panel.add(clearScaleButton);
		panel.add(deleteRowButton);
		
		//add zoombox
		panel.add(zoomPanel);
		
		/** Add Table
		**/
		//create a string for table headers
		String col[] = {"Data Point","Y Value"};
		//create table model with o starting size and table headers found in col
		tableModel = new DefaultTableModel(col, 0);
		//initialize table using the tabel model
		table = new JTable(tableModel);
		//create scrollpane that contains the table in it
		JScrollPane scrollPane = new JScrollPane(table);
		//sets table to full height of container
		table.setFillsViewportHeight(false);
		//set preffered size of scroll pane to not waste space in the gui
		scrollPane.setPreferredSize(new Dimension(200, 10));
	
		
		/** Position Content in Frame and set frame properties
		**/
	
		frame.getContentPane().add(BorderLayout.EAST, panel);
		frame.getContentPane().add(BorderLayout.WEST, scrollPane);
		frame.getContentPane().add(BorderLayout.CENTER, drawPanel);
	
		//set frame to full screensize
		frame.setSize(1650,1080);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//this is always needed
		frame.setVisible(true);


	}//close go()

	/** Methods excluding main method
		**/
	//asks user for a value corresponding to a point they click on in the drawpanel
	public void setUserValue() {
		//dialog to show in the box that requests the user to input a value
		String input = (String)JOptionPane.showInputDialog(frame, "Input known value:", "Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, null, null);
		// convert the retrieved user value to an integer and set it to the variable userValue
		userValue = (int) Integer.parseInt(input); 
	}
	//returns tracked values to their initial state so that the program knows the user needs to rescale
	public void clearScale(){
		scalingIteration = 0;
		isScaled = false;
		isScaling = false;
		drawPanel.clearPointList();
	}
	//takes a screenshot of the location around the mouse so the zoombox can be updated when the mouse moves
	public void screenCapture(){
		//get information regarding mouse location
		PointerInfo a = MouseInfo.getPointerInfo();
		//set variable d to a the point at where the mouse is located
		Point d = a.getLocation();
		try{
			//initialize object screencapture
			SCap = new ScreenCapture();
			//uses robot object to capture screen at x and y location minus a factor to center image and sets image to zoombox size
			Screencapture = SCap.createScreenCapture(d.getX()-zoomBoxWidth/2, d.getY()-zoomBoxHeight/2, zoomBoxWidth, zoomBoxHeight);
			//set image in zoompanel to the captured image
			zoomPanel.setImage(Screencapture);
		}
    	catch (AWTException c){
    		c.printStackTrace();
    	}
		
    	catch (IOException c){
    		c.printStackTrace();
    	}
	}

	/** Action Events
		**/

	class ResizeListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			drawPanel.resizeImage();
			clearScale();
		}//close action event
	}//close inner class	
	
	class RotateListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			if (straightenedAlready == false){
				JOptionPane.showMessageDialog(frame, "Click two points that should be horizontal to each other.");
			}
			else{
				JOptionPane.showMessageDialog(frame, "Please repaste picture to straighten again");
			}
			isStraightening = true;
			clearScale();
		}//close action event
	}//close inner class

	class PasteListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			//gets image off of system clipboard	
			BufferedImage myImage = drawPanel.getImageFromClipboard();
			// displays image in drawpanel
			drawPanel.setImage(myImage);
			clearScale();
			straightenedAlready = false; 
					
		}//close action event
	}//close inner class

	class ScaleListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			clearScale();
			//change is scaling to true so that the next time the user clicks the drawpanel the scaling event triggers
			isScaling = true;
			//give user instructions
			JOptionPane.showMessageDialog(frame, "Select point on graph corresponding to the 0 value.");		
		}//close action event
	}//close inner class
	
	class TotalListener implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			rowCount = table.getModel().getRowCount();
			for (int i=0; i< rowCount; i++){
				System.out.println(table.getModel().getValueAt(i,1));
				totalSum =  (int)table.getModel().getValueAt(i,1) + totalSum;
			}
			// take result of interpolation and datapoint counter and make them ready to be displayed in table
			Object[] objs = {"Total", totalSum};
				//add the result to the table
			tableModel.addRow(objs);
			totalSum =0;
		}//close action event
	}//close inner class

	class ClearDataListener implements ActionListener {
		public void actionPerformed(ActionEvent event){
			//clears table counter	
			dataPointCount = 0;
			//clears tablle
    		tableModel.setRowCount(0);
    		//clears arraylist of points where ovals are drawn
    		drawPanel.clearPointList();
		
		}//close action event
	}//close inner class

		class ClearScaleListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {	
				clearScale();		
			}//close action event
	}//close inner class

		class DeleteSelectedRowListener implements ActionListener{
			public void actionPerformed(ActionEvent event) {	
				int selRow = table.getSelectedRow();
                		if(selRow != -1) {
                    			tableModel.removeRow(selRow);
                		}	
			}//close action event
	}//close inner class

	class HandlerListener extends MouseAdapter{
		//when mouse is clicked this determines if the program is straightening scaled scaling on unscaled and then acts accordingly
		public void mousePressed(MouseEvent e) {
			
			Point b = e.getPoint();
			if (isStraightening == true){
				BufferedImage myImage = drawPanel.getImageFromClipboard();
				if (straightenedAlready == false){
					if (straighteningIteration == 0){
						drawPanel.addPoint(b);
						mouseY1Straight = (float) b.getY();
						mouseX1Straight = (float) b.getX();
						straighteningIteration = 1;
					}
					else{
						drawPanel.addPoint(b);
						mouseY2Straight = (float) b.getY();
						mouseX2Straight = (float) b.getX();
						horizontal = mouseX2Straight-mouseX1Straight;
						vertical = mouseY2Straight-mouseY1Straight;
						straighteningIteration = 0;
						isStraightening = false;
						straightenedAlready = true;
						oppadj =(vertical)/(horizontal);
						double rotationRequired = Math.atan(oppadj);
						drawPanel.rotateImage(myImage, rotationRequired);
					
					}
				}
			}
			// Output results after scaling is finished
			if (isScaled == true) {
				//count number of data points so table can print proper itteration number
				dataPointCount++;
				// set Y value of known point user scalled with to variable mouseY2
				mouseY2 = (float) b.getY();
				// interpolate location clicked based of y values of 0 value and known value
				result = Math.round(userValue*((mouseY2-mouseY0)/(mouseY1-mouseY0)));
				// take result of interpolation and datapoint counter and make them ready to be displayed in table
				Object[] objs = {dataPointCount, result};
				//add the result to the table
				tableModel.addRow(objs);
				// add a point to arraylist in drawpanel such that an oval is drawn at the clicked location
				drawPanel.addPoint(b);
				
				
			}
			//Scale if it is not Scaled yet
			else if (isScaling == true) {
				if (scalingIteration == 0){
					// save coordinates of the 0 value of the graph
					mouseY0 = (float) b.getY();
					// give user instructions
					JOptionPane.showMessageDialog(frame, "Select point on graph corresponding to the other known value.");
				}
				else{
					//save the value of the other known value on the graph
					mouseY1 = (float) b.getY();
					//tell program scaling is complete
					isScaling = false;
					//tell program graph is scaled
					isScaled = true;
					//call method that asks user to input a value for the known value just clicked
					setUserValue();
					//give user instructions
					JOptionPane.showMessageDialog(frame, "Scaling complete. You can now select data points to measure.");
				}
				//count so prgram knows how far we are in scaling process
				scalingIteration++;
			}
			//if it is unscaled this outputs a warning message
			//else if (isScaling == false && isStraightening == false){
				//give user instructions
				//JOptionPane.showMessageDialog(frame, "Please frist Scale the graph");
			//}
		
		}
		
		//unused
		public void mouseExited(MouseEvent e) {
	
		}
		// invokes screencapture method when mouse enters drawpanel
		public void mouseEntered(MouseEvent e) {
			screenCapture();
		}
		//unused
		public void mouseClicked(MouseEvent e) {
		}
		//invokes screencapture method when mouse moves
		public void mouseMoved(MouseEvent e) {
			screenCapture();
	    	}
		//unused
		public void mouseReleased(MouseEvent e) {
		}
	}//close inner class
	
}//close SungevityDigitizer
