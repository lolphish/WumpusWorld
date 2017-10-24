// ======================================================================
// FILE:        MyAI.java
//
// AUTHOR:      Abdullah Younis
//
// DESCRIPTION: This file contains your agent class, which you will
//              implement. You are responsible for implementing the
//              'getAction' function and any helper methods you feel you
//              need.
//
// NOTES:       - If you are having trouble understanding how the shell
//                works, look at the other parts of the code, as well as
//                the documentation.
//
//              - You are only allowed to make changes to this portion of
//                the code. Any changes to other portions of the code will
//                be lost when the tournament runs your code.
// ======================================================================

import java.util.ArrayList;
import java.util.Stack;


/* map: stores probabilities for each square;
 *      empty = 1, hazardous = 0
 */

public class MyAI extends Agent
{
  	private static final double UNEXPLORED = -1;
  	private static final double EMPTY = 1;
  
  	private enum Direction
    {
  		NORTH,
      	EAST,
      	SOUTH,
      	WEST
    }
  
  	private Direction direction;
  	private Point currentPoint;
  	private ArrayList<ArrayList<Double>> map;
  	private Stack<Point> path;
  
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
      	direction = Direction.EAST;
      	currentPoint = new Point();		// initialized to (1, 1)
      	ArrayList<ArrayList<Double>> map = new ArrayList<>();
		map.add(new ArrayList<Double>());
      
      	path = new Stack<>();
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	
	public Action getAction
	(
		boolean stench,
		boolean breeze,
		boolean glitter,
		boolean bump,
		boolean scream
	)
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
        if (stench) {

        }
        if (breeze) {

        }
        if (glitter) {

        }
        if (bump) {

        }
        if (scream) {

        }
		
        step(Direction.EAST);
		return Action.FORWARD;
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	
	// ======================================================================
	// YOUR CODE BEGINS
	// ======================================================================
    private void initializeDirectionMap() {
      	map = new ArrayList<>();
      	map.add(new ArrayList<Double>());
    }
  
    private int getUpperBound() {
      	return map.get(0).size();
    }
  	
    private int getRightBound() {
          return map.size();
    }
  
    private void step(Direction direction) {
        switch (direction) {
          	case NORTH:
          		// if you move up, append to current ArrayList
          		if ((currentPoint.getY()+1) >= getUpperBound()) {
          			map.get(currentPoint.getY()).add(UNEXPLORED);
                }
          		currentPoint.addY(1);
          		break;
          		
          	case SOUTH:
          		currentPoint.addY(-1);
          		break;
          		
          	case EAST:
          		// append a new ArrayList to the outer array
          		if (currentPoint.getX()+1 >= getRightBound())
                {
                  	map.add(new ArrayList<Double>());
                  	for (int i = 0; i < getUpperBound(); ++i)
                    	map.get(currentPoint.getX()+1).add(UNEXPLORED);
                }
          		currentPoint.addX(1);
          		break;
          		
          	case WEST:
          		currentPoint.addX(-1);
          		break;
        }
      	map.get(currentPoint.getX()).set(currentPoint.getY(), EMPTY);
      	// save the (safe) point you just moved onto
      	path.push(currentPoint);
    }

	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}    