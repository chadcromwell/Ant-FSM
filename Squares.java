/********************************************************************************************************************
Author: Chad Cromwell
Date: December 10th, 2017
Assignment: 2
Program: Squares.java
Description: A class that handles and generates the squares used in the ants program
Methods:
		tick() method - What is executed each frame
		paintComponents() method - Renders everything
		addRectangle() method - Accepts a Terrain ArrayList and then adds Terrain objects to the list. Used in nested for loops (for x and y)
		findPath() method - Finds the path using A* algorithm and simple heuristic that tries to move in a direction that is closer to the end square
		isPath() method - Called when a path is found, gathers all of the parent nodes from the end to home square, stores them in an ArrayList and reverses them.
		reset() method - Resets the animation frames, booleans related to animation and pathfinding, and clears the lists
		createSquares() method - Creates the squares and addes them to the squares ArrayList
********************************************************************************************************************/
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class Squares extends JPanel {

	private Random rand = new Random(); //Random seed
	private int randomInt = rand.nextInt(101-0) + 0;
	private int foodAmount = 10;
	private int waterAmount = 10;
	private int poisonAmount = 4;

	//Squares variables
	private int width; //Width of screen
	private int height; //Height of screen
	public int divisions; //How many divisions
	public int rowHeight; //Width of each row
	public int rowWidth; //Height of each row
	public int x; //x position of square
	public int y; //y position of square
	
	//ArrayLists
	public ArrayList<Terrain> squares; //List to hold squares

	//Objects
	public Terrain home; //Terrain object to hold home square
	private Terrain current; //Terrain object to hold the current square

	//Colours
	private Color openColor = new Color(255, 255, 255); //Colour for open terrain, WHITE
	private Color foodColor = new Color(0, 200, 0); //Colour for grass terrain, GREEN
	private Color waterColor = new Color(0, 255, 255); //Colour for swamp terrain, DARK GREEN
	private Color poisonColor = new Color(50, 40, 60); //Colour for obstacle terrain, OBSIDIAN
	private Color homeColor = new Color(0, 0, 230); //Colour for home square, BLUE

	//Level constructor - Accepts an int to determine what level to load
	public Squares(int w, int d) {
		width = w; //Get the width of the map image, assign it to width
		height = width; //Get the height of the map image, assign it to height
		divisions = d; //Capture divisions amount into the square object
		rowHeight = height/divisions; //Calculate the height of the rows
		rowWidth = width/divisions; //Calculate the width of the rows
		squares = new ArrayList<Terrain>(); //Initialize ArrayList comprised of Terrain objects, used for displaying the squares
		home = new Terrain(); //Initialize home square Terrain object

		//For the number of divisions horizontally and vertically
		createSquares();
	}

	//tick() method - What is executed each frame
	public void tick() {
		repaint(); //Repaint the squares
	}
	
	//paintComponents() method - Renders everything
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Iterate through every element in the squares ArrayList
		for(int i = 0; i < squares.size(); i++) {
			//If it is a home square
			if(squares.get(i).type == "home") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(homeColor); //Set the colour to startColor (blue)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the colour to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else {
					g.setColor(homeColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
			//If it is an open square
			if(squares.get(i).type == "open") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(openColor); //Set the colour to openColor (white)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the colour to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else{
					g.setColor(openColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
			//If it is a grass square
			if(squares.get(i).type == "food") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(foodColor); //Set the colour to foodColor (green)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the colour to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else {
					g.setColor(foodColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
			//If it is a swamp square
			if(squares.get(i).type == "water") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(waterColor); //Set the color to waterColor (dark green)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the color to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else {
					g.setColor(waterColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
			//If it is an obsatcle square
			if(squares.get(i).type == "poison") {
				//If it isn't highlighted (not hovered over)
				if(!squares.get(i).highlighted) {
					g.setColor(poisonColor); //Set the color to poisonColor (obsidian)
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
					g.setColor(Color.BLACK); //Set the color to black
					g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the black border
				}
				else {
					g.setColor(poisonColor.brighter()); //Make the colour brighter
					g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height); //Draw the square
				}
			}
		}
	}

	//addRectangle() method - Accepts a Terrain ArrayList and then adds Terrain objects to the list. Used in nested for loops (for x and y).
	public void addRectangle(ArrayList<Terrain> l){
			l.add(new Terrain(new Rectangle(x*rowWidth, y*rowHeight, rowWidth, rowHeight)));
	}

	//clear() method - Resets the enabled parameters of the home and end squares as well as animation frames. It also clears the squares List and creates a new set of squares (prevents memory leak in the heap space)
	public void clear() {
		squares.clear(); //Clear squares List
		createSquares(); //Create new squares
	}

	//createSquares() method - Creates the squares and addes them to the squares ArrayList
	public void createSquares() {
		//For all squares
		for(y = 0; y < divisions; y++) {
			for(x = 0; x < divisions; x++) {
				addRectangle(squares); //Call addRectangle, creating Terrain objects and putting them into the squares ArrayList
			}
		}
		//For all squares
		for(int i = 0; i < squares.size(); i++) {
			squares.get(i).type = "open"; //Initialize all squares as open
		}
		//For the amount of food
		for(int i = 0; i < foodAmount; i++) {
			generateInt(); //Call generateInt() to get a new random int to use for assigning food
			squares.get(randomInt).type = "food"; //Initialize squares as food
		}
		//For the amount of water
		for(int i = 0; i < waterAmount; i++) {
			generateInt(); //Call generateInt() to get a new random int to use for assigning water
			squares.get(randomInt).type = "water"; //Initialize squares as water
		}
		//For the amount of poison
		for(int i = 0; i < poisonAmount; i++) {
			generateInt(); //Call generateInt() to get a new random int to use for assigning poison
			squares.get(randomInt).type = "poison"; //Initialize squares as poison
		}
		squares.get((squares.size()/2)+(Ants.DIVISIONS/2)).type = "home"; //Set middle square to be the ants' home
		home.x = squares.get((squares.size()/2)+(Ants.DIVISIONS/2)).x; //Assign the home square's x position
		home.y = squares.get((squares.size()/2)+(Ants.DIVISIONS/2)).y; //Assign the home square's y position
	}

	//generateInt() method - Generates a random int from 0 to the number of squares available. If it genereates the same int, it will recursively call itself until it generates a unique int.
	public void generateInt() {
		int oldRandomInt = randomInt; //Save the current randomInt, used to prevent duplicate random Ints. Guaruntees that all food, water, and poison squares are generated as unique squares
		randomInt = rand.nextInt((squares.size())-0) + 0; //Assign new random integer
		if(oldRandomInt == randomInt){ //If the new random integer is the same as the old one
			generateInt(); //Call recursively until new integer is found
		}
	}
}