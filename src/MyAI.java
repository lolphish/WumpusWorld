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

public class MyAI extends Agent
{
  	private enum Direction
    {
  		NORTH,
      	EAST,
      	SOUTH,
      	WEST
    }
  
  	private Direction direction;
  	private ArrayList<ArrayList<Double>> map;
  
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
        direction = Direction.EAST;
      	ArrayList<ArrayList<Double>> map = new ArrayList<>();
		map.add(new ArrayList<Double>());
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
		
		return Action.CLIMB;
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	
	// ======================================================================
	// YOUR CODE BEGINS
	// ======================================================================


	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}