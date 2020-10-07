/********************************************************************************************************************
Author: Chad Cromwell
Date: December 10th, 2017
Assignment: 2
Program: Ant.java
Description: A class that creates an ant that has 4 sates:
															foodSearching - Where it searches for food
															waterSearching - Where it searches for water
															returningFood - Where it returns food it has found
															alive - Whether it is alive or dead
Methods:
		tick() - What happens each frame
		findResources() method - A state where the ant is searching for food or water
		goHome() method - A state where the ant is bringing food back to the home square
		randomDir() method - Generates a random direction for the ant to move in
********************************************************************************************************************/
import javax.swing.*;
import java.util.*;

public class Ant extends JPanel {
	private Random rand = new Random(); //Random seed
	private int dir; //Holds an int between 0-7, to determine which direction the ant should move
	private int timer; //Holds the amount of ticks that have passed, used to determine when the ant should change direction
	private int changeDirTime = 200; //How often the ants should change direction, 200 is a good amount. The higher the number, the longer the ants will continue in their chosen direction
	private int randomTimer; //Holds the randomTimer, which is a randomized time limit for how long the ants move in one direction, this uses the changeDirTime to vary the amount each time
	private int xIndex; //Holds the xIndex in squares instead of ant x position
	private int yIndex; //Holds the yIndex in squares instead of any y position
	private int elementIndex; //Holds the element index of the current square the ant is over
	private Terrain currentSquare; //Terrain object that represents the current square the ant is one

	//Finals
	public static final int ANTSIZE = 24; //Size of ant
	private static final int WIDTH = Ants.GRIDWIDTH; //Width of the map
	private static final int HEIGHT = WIDTH; //Height of the map

	//For key presses
	private boolean up; //If moving up
	private boolean down; //If moving down
	private boolean left; //If moving left
	private boolean right; //If moving right
	private boolean canMove = true; //Whether the ant can move in it's current direction
	private boolean food = true; //Whether or not the ant has or is searching for water
	private boolean water; //Whether or not the ant is searching for water
	private boolean searching = true; //Whether or not the ant is searching for resources
	public boolean alive = true; //Whether or not the ant is alive

	//Ant variables
	private double speed = 2; //Ant speed
	private double diagSpeed = 1; //Ant diagonal speed
	int x; //x position
	int y; //y position
	public String state; //The state of the ant, it can be searching for food, water, or returning food

	//Ant() constructor - Take x and y position
	public Ant(int x, int y) {
		randomTimer = rand.nextInt(changeDirTime-(int)(changeDirTime*.7)) + (int)(changeDirTime*.7); //Initialize the randomTimer
		state = "foodSearching"; //Initialize ant in searching for food state
		randomDir(); //Randomize the ant's direction
		this.x = x; //Capture x position in ant object
		this.y = y; //Capture y position in ant object
	}

	//tick() - What happens each frame
	public void tick() {
		//Calculate the current location of the ant in square terms
		xIndex = (x+(ANTSIZE/2))/Ants.squares.rowWidth; //X index in squares instead of x position
		yIndex = (y+(ANTSIZE/2))/Ants.squares.rowWidth; //Y index in squares instead of y position
		elementIndex = (xIndex)+(yIndex*Ants.squares.divisions); //Element index of the current square
		currentSquare = Ants.squares.squares.get(elementIndex); //A Terrain object that holds the square the ant is currently over

		//If the ant is on a food square, and it is searching for food
		if(currentSquare.type == "food" && state == "foodSearching") {
			state = "returningFood"; //Set state to return food
		}

		//If the ant is on a water square, and it is searching for water
		if(currentSquare.type == "water" && state == "waterSearching") {
			state = "foodSearching"; //Set state to search for food
		}

		//If the ant is on a poison square, kill it
		if(currentSquare.type == "poison") {
			alive = false; //The ant is dead
		}

		//If the ant is on the home square and is currently not searching, AKA it is returning home to deliver food
		if(currentSquare.type == "home" && state == "returningFood") {
				state = "waterSearching"; //Set state to search for water
				Ants.antPanel.antArray.add(new Ant(Ants.squares.home.x, Ants.squares.home.y)); //Because food was dropped off, spawn a new ant
		}

		//If the ant is in returning food state
		if(state == "returningFood") {
			goHome(); //Call goHome() and head to home square
		}

		//If the ant is in food searching state
		if(state == "foodSearching") {
			findResources(); //Call findResources() and start searching for food
		}

		//If the ant is in water seraching state
		if(state == "waterSearching") {
			findResources(); //Call findResources() and start searching for water
		}
	}

	//findResources() method - A state where the ant is searching for food or water
	public void findResources() {
		//If the timer is greater than the randomTimer, it's time to change direction
		if(timer > randomTimer) {
				randomDir(); //Call randomDir(), move in a random direction
				randomTimer = rand.nextInt(changeDirTime-(int)(changeDirTime*.9)) + (int)(changeDirTime*.9); //Update randomTimer
				timer = 0; //Reset timer to 0 because direction just changed
			}
			//If moving right and next move will hit the right border
			if(right && x+speed > WIDTH-ANTSIZE) {
				canMove = false;
				right = false;
				randomDir();
			}
			//If moving left and next move will hit the left border
			if(left && x-speed < 0){
				canMove = false;
				left = false;
				randomDir();
			}
			//If moving down and next move will hit the bottom border
			if(down && y+speed > HEIGHT-ANTSIZE) {
				canMove = false;
				down = false;
				randomDir();
			}
			//If moving up and next move will hit the top border
			if(up && y-speed < 0) {
				canMove = false;
				up = false;
				randomDir();
			}
			//If the ant can move
			if(canMove) {
				//If moving up state
				if(up && !left && !right){
					y -= speed; //Move up
				}
				//If moving right state
				if(right && !up && !down) {
					x+= speed; //Move right
				}
				//If moving down state
				if(down && !left && !right) {
					y+= speed; //Move down
				}
				//If moving left state
				if(left && !up && !down) {
					x-= speed; //Move left
				}
				//If moving up and right state
				if(up && right){
					y -= diagSpeed; //Move up
					x += diagSpeed; //Move right
				}
				//If moving down and right state
				if(down && right) {
					y += diagSpeed; //Move down
					x += diagSpeed; //Move right
				}
				//If moving down and left state
				if(down && left) {
					y += diagSpeed; //Move down
					x -= diagSpeed; //Move left
				}
				//If moving up and left state
				if(up && left) {
					y -= diagSpeed; //Move up
					x -= diagSpeed; //Move left
				}
			}
		timer++; //Increment timer
	}

	//goHome() method - A state where the ant is bringing food back to the home square
	public void goHome() {
		//If ant is to the left of the home square
		if(x < Ants.squares.home.x) {
			x += speed; //Move right
		}
		//If ant is to the right of the home square
		if(x > Ants.squares.home.x) {
			x -= speed; //Move left
		}
		//If ant is above the home square
		if(y < Ants.squares.home.y) {
			y += speed; //Move down
		}
		//If ant is below the home square
		if(y > Ants.squares.home.y) {
			y -= speed; //Move up
		}
	}

	//randomDir() method - Generates a random direction for the ant to move in
	public void randomDir() {
		dir = rand.nextInt(8-0) + 0; //Calculate random int, 8 possible numbers for 8 possible directions
		canMove = true; //Set canMove to true
		//Move up
		if(dir == 0) {
			up = true;
			right = false;
			down = false;
			left = false;
		}
		//Move right
		if(dir == 1) {
			right = true;
			up = false;
			down = false;
			left = false;
		}
		//Move down
		if(dir == 2) {
			down = true;
			up = false;
			right = false;
			left = false;
		}
		//Move left
		if(dir == 3) {
			left = true;
			up = false;
			right = false;
			down = false;
		}
		//Move up and right
		if(dir == 4) {
			up = true;
			right = true;
			down = false;
			left = false;
		}
		//Move down and right
		if(dir == 5) {
			down = true;
			right = true;
			up = false;
			left = false;
		}
		//Move down and left
		if(dir == 6) {
			down = true;
			left = true;
			up = false;
			right = false;
		}
		//Move up and left
		if(dir == 7) {
			up = true;
			left = true;
			right = false;
			down = false;
		}
	}
}