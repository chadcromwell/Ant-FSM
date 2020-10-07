/********************************************************************************************************************
Author: Chad Cromwell
Date: December 1st, 2017
Assignment: 2
Program: HandlerClass.java
Description: A class that handles the user's mouse input
********************************************************************************************************************/
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class HandlerClass implements MouseListener, MouseMotionListener {
	int x; //Holds x position of the mouse
	int y; //Holds y position of the mouse
	boolean click; //Whether or not the user has clicked
	
	@Override
	//What happens when the mouse is clicked
	public void mouseClicked(MouseEvent e) {
	}
	//What happens when the mouse is pressed
	public void mousePressed(MouseEvent e) {
		click = true; //The user is clicking
	}
	//What happens when the mouse is released
	public void mouseReleased(MouseEvent e) {
		click = false; //The user is no longer clicking
	}
	//What happens when the mouse enters the frame
	public void mouseEntered(MouseEvent e) {
	}
	//What happens when the mouse exits the frame
	public void mouseExited(MouseEvent e) {
	}
	//What happens when the mouse is moved within the frame
	public void mouseMoved(MouseEvent e) {
		x = e.getX(); //Capture x position of the mouse
		y = e.getY(); //Capture y position of the mouse
	}
	//What happens when the mouse is pressed and moved (dragging)
	public void mouseDragged(MouseEvent e) {
		x = e.getX(); //Capture x position of the mouse
		y = e.getY(); //Capture y position of the mouse
	}
}