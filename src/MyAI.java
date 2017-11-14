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
import java.util.Set;
import java.util.Stack;


/* map: stores probabilities for each square;
 *      empty = 1, hazardous = 0
 */

public class MyAI extends Agent
{
  	private static final Random generator = new Random();
  
  	static enum Direction
    {
  		UP,
      	RIGHT,
      	DOWN,
      	LEFT
    }
  
  	private Direction direction;
  	private Point currentPoint;
  	private Node currentNode;
  	private Graph cave;
  	private Stack<Action> path;
  	private Queue<Action> actions;
  	private boolean hasArrow;
  	private boolean wumpusAlive;
  	private boolean climbOut;
  	
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
      	currentPoint = new Point(1, 1);
      	currentNode = new Node(Node.Marker.EXPLORED);
      	cave = new Graph(currentNode);
      	
		hasArrow = true;
		wumpusAlive = true;
		climbOut = false;
		
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
			// TODO:
			// moveForward() adds a new node
			// if a bump is perceived, a new node was added when it shouldn't have been
			// we can mark this node as a WALL, then update currentPoint to be the node that it came from
			cave.markNodeAtPoint(currentPoint, Node.Marker.WALL);
			currentPoint = getLocalOriginPoint(currentPoint);
		}
		
        if (glitter) {
        		climbOut = true;
            return Action.GRAB;
        }
        
        if (climbOut && currentPoint.atStart()) {
        		return Action.CLIMB;
        }
      
        // if an upcoming action has been queued, prioritize it
		if (!actions.isEmpty()) {
			return dequeueAction();
		}
		
		// attempt to kill the Wumpus
		if (stench && wumpusAlive && hasArrow) {
//            hasArrow = false;
//            return Action.SHOOT;		// return; don't add Action.SHOOT to the path stack
        }
		
		currentNode.setMarker(Node.Marker.EXPLORED);
		if (breeze) {
			currentNode.setMarker(Node.Marker.BREEZE);
		}
		if (stench) {
			currentNode.setMarker(Node.Marker.STENCH);
		}
		
		Action action;
		if (breeze || stench) {
			action = goBack();
			actions.add(Action.TURN_LEFT);
		}
		else if (bump) {
			// turn 180 if you perceive a bump?
			// in the worst case, it will take at least 2 90-degree turns to recover from a bump
			// but, it would only take 1 180-degree turn
			action = getRandomTurn();
			actions.add(action);
		}
        else {
        		action = moveForward();
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
    		currentNode = cave.addNode(currentNode, direction, currentPoint); 
    		return Action.FORWARD;
    }
  
    /* Returns a random Action out of {TURN_LEFT, TURN_RIGHT, FORWARD}.
     * The probability of a FORWARD move is greater than a turn.
     * Returns the Action.
     */
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
  
//    /* Clears all upcoming actions and begins backtracking (literally)
//     * to the starting position, in the order that it came.
//     * Returns the first action it needs to take to backpedal (usually the first turn in a 180).
//     */
//    private Action backpedal() {
//      	actions.clear();
//      	
//      	// 180, then transfer stack to actions queue
//      	// if the agent never made a move (perceived hazard at start),
//      	// then no need to turn around
//      	if (!path.empty()) {
//      		actions.add(Action.TURN_RIGHT);
//          	actions.add(Action.TURN_RIGHT);
//      	}
//      	
//        while (!path.empty()) {
//            actions.add(path.pop());
//        }
//        
//        actions.add(Action.CLIMB);
//        return dequeueAction();
//    }
    
    /* Readies the agent to go to the square behind him.
     * Returns the first turn.
     */
    private Action goBack() {
    		actions.add(Action.TURN_RIGHT);
    		actions.add(Action.TURN_RIGHT);
    		actions.add(Action.FORWARD);
    		actions.add(Action.TURN_RIGHT);
    		actions.add(Action.TURN_RIGHT);
    		return dequeueAction();
    }
    
    /* Assumes actions is NOT empty.
     * Pops the next Action from the actions queue, and reverses it if it's a turn - 
     * e.g., a left turn becomes a right turn, and vice versa, and moveForward updates currentPoint and cave.
     * Returns the next Action in the queue.
     */
    private Action dequeueAction() throws NoSuchElementException {
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
    
    /*
     * Helper function for restoring currentPoint after bumping into a wall.
     * After perceiving a bump, currentPoint has already been updated to out-of-bounds.
     * Returns the valid Point that it came from.
     * example: currentPoint = getLocalOriginPoint(currentPoint);
     */
    private Point getLocalOriginPoint(Point point) {
    		Node target = cave.getNode(point);
    		if (target == null) {
    			throw new WumpusWorldException("received null Node target; expected valid");
    		}
    		Set<Point> neighbors = cave.getAdjacentPoints(point);
    		
    		// a wall should only be connected to a valid node on ONE of its ends
    		if (neighbors.size() != 1) {
    			throw new WumpusWorldException("expected exactly 1 valid neighboring node");
    		}
    		return neighbors.iterator().next();
    }

	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}
  