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

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;


/* map: stores probabilities for each square;
 *      empty = 1, hazardous = 0
 */

public class MyAI extends Agent
{
  	private static final Random generator = new Random();
  	private static final double UNEXPLORED = -1;
  	private static final double EMPTY = 1;
  
  	private enum Direction
    {
  		UP,
      	RIGHT,
      	DOWN,
      	LEFT
    }
  
  	private Direction direction;
  	private Point currentPoint;
  	private LinkedList<Point> visited;
  	private Stack<Action> path;
  	private Queue<Action> actions;
  	private boolean hasArrow;
  	private boolean wumpusAlive;
  	
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
      	currentPoint = new Point();		// initialized to (1, 1)
      	visited = new LinkedList<>();
      	addToVisited(currentPoint);
      	
		hasArrow = true;
		wumpusAlive = true;
		
      	path = new Stack<>();
      	actions = new LinkedList<>();
      	direction = Direction.RIGHT;
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
		if (scream) {
			wumpusAlive = false;
		}
		if (bump) {
			visited.removeLast();
			currentPoint = visited.getLast();
			path.pop();
		}
		
        if (glitter) {
            return Action.GRAB;
        }
        if (currentPoint.atStart() && visited.size() > 1) {
        		return Action.CLIMB;
        }
      
        // if an upcoming action has been queued, prioritize it
		if (!actions.isEmpty()) {
			return getQueuedAction();
		}
		
		Action action;
//		// attempt to kill the Wumpus
//		if (stench && hasArrow) {
//            hasArrow = false;
//            return Action.SHOOT;		// return; don't add Action.SHOOT to the path stack
//        }
		if (breeze || (stench && wumpusAlive)) {
          	// 180, then pop from the stack
          	return backpedal();
        }
		else if (bump) {
			// turn 180 if you perceive a bump?
			// in the worst case, it will take at least 2 90-degree turns to recover from a bump
			// but, it would only take 1 180-degree turn
			action = getRandomTurn();
		}
        else {
            action = getRandomMove();
        }
        
		// track the steps that the Agent has taken,
		// so that it can safely (though possibly not efficiently) back-track to the start
        path.push(action);
        
		return action;
		// ======================================================================
		// YOUR CODE ENDS
		// ======================================================================
	}
	
	// ======================================================================
	// YOUR CODE BEGINS
	// ======================================================================  
	
    /* Helper function;
     * returns Action.TURN_LEFT, and updates the AI's direction.
     */
    private Action turnLeft() {
      
    		switch (direction) {
    			case UP:
    				direction = Direction.LEFT;
    				break;
    			case LEFT:
    				direction = Direction.DOWN;
    				break;
    			case DOWN:
    				direction = Direction.RIGHT;
    				break;
    			case RIGHT:
    				direction = Direction.UP;
    				break;
    		}
    		return Action.TURN_LEFT;
    }
    
    /* Helper function;
     * returns Action.TURN_RIGHT, and updates the AI's direction.
     */
    private Action turnRight() {
    		switch (direction) {
    			case UP:
    				direction = Direction.RIGHT;
    				break;
    			case RIGHT:
    				direction = Direction.DOWN;
    				break;
    			case DOWN:
    				direction = Direction.LEFT;
    				break;
    			case LEFT:
    				direction = Direction.UP;
    				break;
    		}
    		return Action.TURN_RIGHT;
    }
    
    private Action moveForward() {
    		switch (direction) {
    			case UP:
    				currentPoint.addY(1);
    				break;
    			case DOWN:
    				currentPoint.addY(-1);
    				break;
    			case RIGHT:
    				currentPoint.addX(1);
    				break;
    			case LEFT:
    				currentPoint.addX(-1);
    				break;
    		}
    		addToVisited(currentPoint);
    		return Action.FORWARD;
    }
  
    private Action getRandomMove() {
	    Action action;
      	int value = generator.nextInt(12);
        switch (value) {
        		case 0:
          		action = turnLeft();
          		break;
          	case 1:
          		action = turnRight();
          		break;
          	case 2:
          	default:
          		action = moveForward();
        }
      	return action;
    }
    
    private Action getRandomTurn() {
    		Action action;
    		int value = generator.nextInt(2);
    		switch (value) {
    			case 0:
    				action = turnLeft();
    				break;
    			default:
    				action = turnRight();
    		}
    		return action;
    }
    
    /* Helper method;
     * Must invoke Point's copy constructor - 
     * otherwise, they are passed by reference and Points inside visited
     * will be updated by currentPoint's method calls.
     */
    private void addToVisited(Point point) {
    		visited.add(new Point(point));
    }
  
    /* Clears all upcoming actions and begins backtracking (literally)
     * to the starting position, in the order that it came.
     * Returns the first action it needs to take to backpedal (usually the first turn in a 180).
     */
    private Action backpedal() {
    		// 180, then transfer stack to actions queue
      	actions.clear();
      	
      	// if the agent never made a move (perceived hazard at start),
      	// then no need to turn around
      	if (!path.empty()) {
      		actions.add(Action.TURN_RIGHT);
          	actions.add(Action.TURN_RIGHT);	
      	}
      	
        while (!path.empty()) {
            actions.add(path.pop());
        }
        
        actions.add(Action.CLIMB);
        return getQueuedAction();
    }
    
    /* Assumes actions is NOT empty.
     */
    private Action getQueuedAction() throws NoSuchElementException {
    		Action nextAction = actions.remove();
    		switch (nextAction) {
    			case TURN_LEFT:
				nextAction = turnRight();
				break;
			case TURN_RIGHT:
				nextAction = turnLeft();
				break;
			case FORWARD:
				nextAction = moveForward();
			default:
				break;
    		}
    		return nextAction;
    }

	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}
  