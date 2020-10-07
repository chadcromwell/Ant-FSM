/********************************************************************************************************************
Author: Chad Cromwell
Date: December 10th, 2017
Assignment: 2
Program: AntPanel.java
Description: A class that handles the animation of the ants
Methods:
		start() method - Initialization of ants, what is first executed
		stop() method - When animation neeeds to stop
		tick() method - What is executed each frame
		paintComponents() method - Renders everything
********************************************************************************************************************/
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class AntPanel extends JPanel {
	public static ArrayList<Ant> antArray; //An ArrayList of Ant objects
	public int startingAnts; //How many ants to start with
	public boolean start = false; //Whether to start animating
	private Color foodColor = new Color(0, 255, 0); //Color of food, GREEN
	private Color waterColor = new Color(0, 255, 255); //Color of water, CYAN
	private Color antColor = Color.BLACK; //Color of ants, BLACK

	//AntPanel() constructor
	public AntPanel() {
		antArray = new ArrayList<Ant>(); //Initialize the ArrayList
	}

	//start() method - Initialization of ants, what is first executed
	public void start() {
		//For the number of ants the user chooses
		for(int i = 0; i < startingAnts; i++) {
			antArray.add(new Ant(Ants.squares.home.x, Ants.squares.home.y)); //Add that many ants to the ArrayList
		}
		start = true; //The animation has started
	}

	//stop() method - When animation neeeds to stop
	public void stop() {
		start = false; //The animation is stopped
	}

	//tick() method - What is executed each frame
	public void tick() {
		//If the animation has been started
		if(start) {
			//For all ants in the ArrayList
			for(int i = 0; i < antArray.size(); i++) {
				antArray.get(i).tick(); //Tick each ant
			}
			repaint(); //Repaint
		}
	}

	//paintComponents() method - Renders everything
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		//For all ants in the ArraryList
		for(int i = 0; i < antArray.size(); i++) {

			//If the ant is alive
			if(antArray.get(i).alive){
				//If the ant is in the searching for food state
				if(antArray.get(i).state == "foodSearching") {
					g.setColor(foodColor); //Set the stroke colour to foodColor GREEN
				}
				//If the ant is in the searching for water sate
				if(antArray.get(i).state == "waterSearching") {
					g.setColor(waterColor); //Set the stroke colour to waterColor CYAN
				}
				g.fillOval((int)antArray.get(i).x, (int)antArray.get(i).y, Ant.ANTSIZE, Ant.ANTSIZE); //Draw the circle
				g.setColor(antColor); //Set the colour back to antColor BLACK
				g.fillOval((int)antArray.get(i).x+3, (int)antArray.get(i).y+3, Ant.ANTSIZE-6, Ant.ANTSIZE-6); //Draw a smaller circle of black, this creates a coloured stroke effect

				//If the ant is in the returning food to home state
				if(antArray.get(i).state == "returningFood") {
					g.setColor(foodColor); //Set the ant's colour to foodColor GREEN
					g.fillOval((int)antArray.get(i).x, (int)antArray.get(i).y, Ant.ANTSIZE, Ant.ANTSIZE); //Draw the ant as foodColor GREEN to show that it is carrying the food back home
				}
			}
			//If the ant isn't alive
			if(!antArray.get(i).alive) {
				antArray.remove(i); //Remove it from the ArrayList (kill it)
			}
		}
	}
}