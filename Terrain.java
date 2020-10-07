/********************************************************************************************************************
Author: Chad Cromwell
Date: December 1st, 2017
Assignment: 2
Program: Terrain.java
Description: A class that represents a terrain square, it is like a Rectangle object but with more parameters, such as gCost, hCost, fCost, parent, and more.
			Essentially a Rectangle with the ability to be treated as a node for pathfinding
********************************************************************************************************************/
import java.awt.*;

public class Terrain {
	int x; //Holds x position
	int y; //Holds y position
	int width; //Holds width position
	int height; //Holds height position
	int cost; //Holds the cost to traverse this type of terrain
	int gCost; //Holds the g cost
	int fCost; //Golds the f cost
	int hCost; //Holds the h cost
	Terrain parent; //Terrain object that is the parent of this specific terrain node
	boolean isPath = false; //Whether or not this terrain node is in the optimal path
	boolean opened; //Whether or not this terrain node is opened
	boolean closed; //Whether or not this terrain node is closed
	boolean enabled; //Whether or not if this terrain node has been enabled
	String type = "open"; //Type of terrain, initialize as open terrain
	boolean highlighted = false; //Initialize as not highlighted

		//Default constructor
		public Terrain() {};

		//Terrain() constructor - Accepts a Rectangle object, stores x, y, width, and height of the rectangle. It is practically a Rectangle object but with extra parameters
		public Terrain(Rectangle r) {
			x = r.x; //Capture x position from rectangle object
			y = r.y; //Capture y position from rectangle object
			width = r.width; //Capture width from rectangle object
			height = r.height; //Capture height from rectangle object
		}

		//Overriding how comparing works with Terrain objects. When comparing, it will take into consideration it's position, not just the type of object it is.
		@Override
		public boolean equals(Object o) {
			if (o instanceof Terrain) {
				Terrain t = (Terrain) o;
				return t.x == this.x && t.y == this.y;
			}
			return false;
		}
	}