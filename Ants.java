/********************************************************************************************************************
Author: Chad Cromwell
Date: December 10th, 2017
Assignment: 2
Program: Ants.java
Description: A program that simulates a user selected number of ants that have 4 different states:
																									Searching for food: The ant will randomly walk around looking for food.
																									Searching for water: The ant will randomly walk around looking for water.
																									Going home: The ant will walk as quick as it can home with food once it finds it.
																									Alive/Dead: If the ant is alive, it will search for food until it finds it and then return it home.
																												Once it drops the food off, it will be thirsty and search for water. Once it finds water
																												it will begin searching for food again. If the ant walks on a poison square, it will die.
																									When food is delivered to the home square, and new ant will spawn.
															
Methods:
		start() method - Starts the thread for the program if it isn't already running
		stop() method - Stops the thread for the program if it is running
		tick() method - What happens each frame
		render() method - Renders each frame
		run() method - The program loop
********************************************************************************************************************/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Ants extends Frame implements Runnable {

	//Finals
	private static final int CONTROLSWIDTH = 200; //Width the of the control panel
	public static final int GRIDWIDTH = 800; //Width of the squares
	public static final int DIVISIONS = 32; //Split it 32x32 (make sure this number is a divisor of GRIDWIDTH)
	private static final int WIDTH = GRIDWIDTH+CONTROLSWIDTH; //Window width
	private static final int HEIGHT = GRIDWIDTH+22; //Window height (+22 to account for MacOS window decorations)
	private static final String TITLE = "Ants!"; //Window title
	public static final double FPS = 60.0; //Desired FPS

	//Program variables
	public static HandlerClass handler; //HandlerClass objet, handles mouse clicks and movement
	private static JFrame frame; //Frame
	private static JPanel panel; //Panel that goes in frame
	private static JLayeredPane layeredPane;
	private static JTextField antNumberField;
	private static JLabel textLabel;
	private static JLabel currentAnts;
	private static JButton startButton; //Button for "Find path" - When clicked the path will be calculated
	private static JButton resetButton; //Button for "Clear" - When clicked, everything will be cleared from the display
	private static GridBagConstraints gbc; //Holds GridBagConstraints

	//Booleans
	private boolean isRunning = false; //Boolean to keep track of whether program is running or not

	//Objects
	private Thread thread; //Thread
	public static Squares squares; //Squares object, draws and handles the squares
	public static AntPanel antPanel; //AntPanel object, draws the ants
	private Terrain current; //Current object, holds the current Terrain object

	//FPS variables, initialized in run() method
	private int fps; //Holds the count of the current fps
	private double timer; //Holds current time in milliseconds, used to display FPS
	private long lastTime; //Holds the last time the run method was called
	private double targetTick; //Holds desired FPS
	private double d; //Holds error amount between actual fps and desired fps
	private double interval; //Interval between ticks
	private long now; //Holds the current time for the new frame

	//Ants constructor
	public Ants(){
		handler = new HandlerClass(); //Initialize handler
		squares = new Squares(GRIDWIDTH, DIVISIONS); //Initialize squares
		antPanel = new AntPanel(); //Initialize squares
		frame = new JFrame(); //Initialize frame
		frame.setMinimumSize(new Dimension(WIDTH, HEIGHT)); //Set minimum size of the frame, can't get smaller than the content of the window

		//Buttons
		startButton = new JButton("Start"); //Initialize "Start" button
		startButton.setPreferredSize(new Dimension(CONTROLSWIDTH, 40)); //Set the size of the button
		//Add action listener
		startButton.addActionListener(new ActionListener() {
			//If button is pressed
			public void actionPerformed(ActionEvent e) {
				try{
					antPanel.startingAnts = Integer.parseInt(antNumberField.getText()); //Capture integer input, assign to antPanel.startingAnts, the number of ants to start with
					//If the user entered a number greater than 0
					if(antPanel.startingAnts > 0) {
						startButton.setEnabled(false); //Disbale the start button
						antPanel.start(); //Start the animation of ants
					}
					//If the user entered 0
					else {
						JOptionPane.showMessageDialog(null, "Enter a number greater than 0", "Alert", JOptionPane.PLAIN_MESSAGE); //Tell the user to enter a number greater than 0
					}
				}
				//If the user enters something other than integers
				catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Please only enter integers", "Alert", JOptionPane.PLAIN_MESSAGE); //Tell the user to enter only integers
				}
			}
		});

		resetButton = new JButton("Reset"); //Initialize "Reset" button
		resetButton.setPreferredSize(startButton.getPreferredSize()); //Set the size of the button, to be the same as the previous button
		//Add action listener
		resetButton.addActionListener(new ActionListener() {
			//If button is pressed
			public void actionPerformed(ActionEvent e) {
				antNumberField.setText(""); //Reset the text field to empty
				startButton.setEnabled(true); //Re-enable the start button
				antPanel.stop(); //Stop the animation of ants
				antPanel.antArray.clear(); //Clear the antArray
				squares.clear(); //Clear the squares
			}
		});

		textLabel = new JLabel("Starting ants:"); //Initialize JLabel, used as a label for the text field
		currentAnts = new JLabel("Current ants: " + antPanel.antArray.size()); //Initialize JLabel, used as a label for the text field

		//Text Field
		antNumberField = new JTextField(); //Initialize new JTextField, used for user input
		antNumberField.setToolTipText("Enter number of ants"); //Set tooltip "Enter number of ants" for when user hovers over the JTextField
		//Add key listener
		antNumberField.addKeyListener(new KeyListener() {
			//If key is typed
			@Override
			public void keyTyped(KeyEvent e) {
				//If the animation is not started, allow the user to press enter to enter an integer
				if(!antPanel.start) {
					//If ENTER is pressed
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
							try{
							antPanel.startingAnts = Integer.parseInt(antNumberField.getText()); //Capture integer input, assign to antPanel.startingAnts, the number of ants to start with
							//If the user entered a number greater than 0
							if(antPanel.startingAnts > 0) {
								startButton.setEnabled(false); //Disable the start button
								antPanel.start(); //Start the animation of ants
							}
							//If the user entered 0
							else {
								JOptionPane.showMessageDialog(null, "Enter a number greater than 0", "Alert", JOptionPane.PLAIN_MESSAGE); //Tell the user to enter a number great than 0
							}
						}
						//If the user enters something other than integers
						catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(null, "Please only enter integers", "Alert", JOptionPane.PLAIN_MESSAGE); //Tell the user to enter only integers
						}
					}
				}
			}
			//Override keyReleased, not used
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			//If a key is pressed
			@Override
			public void keyPressed(KeyEvent e) {
				//If the animation is not started, allow the user to press enter to enter an integer
				if(!antPanel.start) {
					//If ENTER is pressed
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						try{
							antPanel.startingAnts = Integer.parseInt(antNumberField.getText()); //Capture integer input, assign to antPanel.startingAnts, the number of ants to start with
							//If the user entered a number greater than 0
							if(antPanel.startingAnts > 0) {
								startButton.setEnabled(false); //Disable the start button
								antPanel.start(); //Start the animation of ants
							}
							//If the user entered 0
							else {
								JOptionPane.showMessageDialog(null, "Enter a number greater than 0", "Alert", JOptionPane.PLAIN_MESSAGE); //Tell the user to enter a number great than 0
							}
						}
						catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(null, "Please only enter integers", "Alert", JOptionPane.PLAIN_MESSAGE); //Tell the user to enter only integers
						}
					}
				}
			}
		});
		
		//Initialize panel
		layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, WIDTH, HEIGHT);
		frame.add(layeredPane); //Add the panel to the frame
		panel = new JPanel(new GridBagLayout()); //Create panel, using GridBagLayout
		panel.setBounds(0, 0, WIDTH, HEIGHT);
		antPanel.setBounds(0, 0, WIDTH, HEIGHT);
		antPanel.setOpaque(false);
		panel.addMouseListener(handler); //Add mouse listener to the panel
		panel.addMouseMotionListener(handler); //Add mouse motion listener to the panel
		gbc = new GridBagConstraints(); //Initialize GridBagConstraints

		//Squares, place in top left, taking up 4 wide, 10 high grids
		gbc.fill = GridBagConstraints.BOTH; //Fill horizontally and vertically
		gbc.gridwidth = 4; //4 grids wide
		gbc.gridheight = 5; //10 grids tall
		gbc.weightx = 1; //1 weight in x plane
		gbc.weighty = 1; //1 weight in y plane
		gbc.gridx = 0; //x position 0
		gbc.gridy = 0; //y position 0
		panel.add(squares, gbc); //Add squares to panel with GridBagConstraints

		//Text label
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0;  //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 0; //y position 0 
		panel.add(textLabel, gbc); //Add label to panel with GridBagConstraints

		//Ant number text field
		gbc.fill = GridBagConstraints.HORIZONTAL; //Do not fill
		gbc.gridwidth = 1; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0;  //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 5; //x position 5
		gbc.gridy = 0; //y position 0 
		panel.add(antNumberField, gbc); //Add button to panel with GridBagConstraints

		//Buttons
		//Start button
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 2; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0;  //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 1; //y position 0 
		panel.add(startButton, gbc); //Add button to panel with GridBagConstraints

		//Reset button
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 2; //1 grid wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0; //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 2; //y position 2
		panel.add(resetButton, gbc); //Add button to panel with GridBagConstraints

		//Current ants label
		gbc.fill = GridBagConstraints.NONE; //Do not fill
		gbc.gridwidth = 2; //2 grids wide
		gbc.gridheight = 1;  //1 grid high
		gbc.weightx = 0;  //0 weight in x plane
		gbc.weighty = 0; //0 weight in y plane
		gbc.gridx = 4; //x position 4
		gbc.gridy = 3; //y position 03
		panel.add(currentAnts, gbc); //Add label to panel with GridBagConstraints

		layeredPane.add(panel, new Integer(0)); //Add panel to the bottom layer
		layeredPane.add(antPanel, new Integer(1)); //Add antPanel to the layer above
		frame.setTitle(TITLE); //Add the title to the frame
		frame.setResizable(true); //Window cannot be resized
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit the program when the window is closed
		frame.setLocationRelativeTo(null); //Open the window in the middle
		frame.setVisible(true); //Make the window visible
	}

	//start() method - Starts the thread for the program if it isn't already running
	public synchronized void start() {
		if(isRunning) return; //If the program is already running, exit method
		isRunning = true; //Set boolean to true to show that it is running
		thread = new Thread(this); //Create a new thread
		thread.start(); //Start the thread
	}

	//stop() method - Stops the thread for the program if it is running
	public synchronized void stop() {
		if(!isRunning) return; //If the program is stopped, exit method
		isRunning = false; //Set boolean to false to show that the program is no longer running
		//Attempt to join thread (close the threads, prevent memory leaks)
		try {
			thread.join();
		}
		//If there is an error, print the stack trace for debugging
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	//tick() method - What happens each frame
	private void tick() {
		currentAnts.setText("Current ants: " + antPanel.antArray.size()); //Update the currentAnts JLabel
		squares.tick(); //Tick squares
		antPanel.tick(); //Tick antPanel
	}

	//run() method - The program loop
	@Override
	public void run() {
		requestFocus(); //So window is selected when it opens
		fps = 0; //Counts current fps
		timer = System.currentTimeMillis(); //Keep track of current time in milliseconds, used to display FPS
		lastTime = System.nanoTime(); //Keep track of the last time the method was called
		targetTick = FPS; //Set desired FPS
		d = 0; //Varible used to keep track if it is running at desired FPS/used to compensate
		interval = 1000000000/targetTick; //Interval between ticks

		while(isRunning) {
			now = System.nanoTime(); //Capture the time now
			d += (now - lastTime)/interval; //Calculate d
			lastTime = now; //Update lastTime

			//If d is >= 1 we need to render to stay on fps target
			while(d >= 1) {
				tick(); //Call tick method
				fps++; //Increment fps
				d--; //Decrement d
			}

			//If the difference between the current system time is greater than 1 second than last time check, print the fps, reset fps to 0, and increase timer by 1 second
			if(System.currentTimeMillis() - timer >= 1000) {
				fps = 0; //Set fps to 0
				timer+=1000; //Increase timer by 1 second
			}
		}
		stop(); //Stop the program
	}

	//Main
	public static void main(String[] args) {
		Ants ants = new Ants(); //Initialize new Ants object
		ants.start(); //Call start method in program object, starts the program
	}
}