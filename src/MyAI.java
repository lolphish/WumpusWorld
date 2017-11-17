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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
  	private Queue<Action> actions;
  	private boolean hasArrow;
  	private boolean wumpusAlive;
  	private boolean climbOut;
  	
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
      	currentPoint = Point.getStartingPoint();		// initialized to (1, 1)
      	currentNode = new Node(Node.Marker.EXPLORED);
      	cave = new Graph(currentNode);
      	
		hasArrow = true;
		wumpusAlive = true;
		climbOut = false;
		
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
			// moveForward() adds a new node
			// if a bump is perceived, a new node was added when it shouldn't have been
			// we can mark this node as a WALL, then update currentPoint to be the node that it came from
			// TODO: add right/top wall
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
//            return Action.SHOOT;
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
          	// TODO: go back to where you came from, and move to a non-dangerous node
			Stack<Point> backPath = cave.getPath(currentPoint, Point.getStartingPoint());
			action = moveForward();	// PLACEHOLDER
		}
        else {
        		action = moveForward();
        }
        
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
     * Pops the next Action from the actions queue, and returns it.
     */
    private Action dequeueAction() throws NoSuchElementException {
        Action nextAction = actions.remove();
        switch (nextAction) {
            case TURN_LEFT:
            nextAction = turnLeft();
            break;
        case TURN_RIGHT:
            nextAction = turnRight();
            break;
        case FORWARD:
            nextAction = moveForward();
        default:
            break;
        }
        return nextAction;
    }
  
  	/* Queues turn Actions until the Agent is facing targetDirection.
     * TODO: optimize
     */
    private void faceDirection(Direction currentDirection, Direction targetDirection) {
        if (currentDirection == targetDirection) {
            return;
        }
        switch (currentDirection) {
	        	case UP:
	            switch (targetDirection) {
	              	case DOWN:
	          			actions.add(Action.TURN_RIGHT);
	          			actions.add(Action.TURN_RIGHT);
	              		break;
	              	case LEFT:
	          			actions.add(Action.TURN_LEFT);
	              		break;
	              	case RIGHT:
	              		actions.add(Action.TURN_RIGHT);
	              		break;
	            }
	      		break;
	      	case DOWN:
	      		switch (targetDirection) {
	              	case UP:
	          			actions.add(Action.TURN_RIGHT);
	          			actions.add(Action.TURN_RIGHT);
	              		break;
	              	case LEFT:
	          			actions.add(Action.TURN_RIGHT);
	              		break;
	              	case RIGHT:
	              		actions.add(Action.TURN_LEFT);
	              		break;
	      		}
	      		break;
	      	case LEFT:
	      		switch (targetDirection) {
	              	case UP:
	          			actions.add(Action.TURN_RIGHT);
	              		break;
	              	case DOWN:
	          			actions.add(Action.TURN_LEFT);
	              		break;
	              	case RIGHT:
	              		actions.add(Action.TURN_RIGHT);
	              		actions.add(Action.TURN_RIGHT);
	              		break;
	            }
	      		break;
	      	case RIGHT:
	      		switch (targetDirection) {
	              	case UP:
	          			actions.add(Action.TURN_LEFT);
	              		break;
	              	case DOWN:
	          			actions.add(Action.TURN_RIGHT);
	              		break;
	              	case LEFT:
	              		actions.add(Action.TURN_RIGHT);
	              		actions.add(Action.TURN_RIGHT);
	              		break;
	            }
	      		break;
        }
    }
    
      /*
     * convert stack of point directions to actions and add them to the actions
     * queue
     */
    private void getActionsFromPoints(Stack<Point> points) {
    	Direction currentDirection = direction, nextDirection = null;
    	Point currentLocationPoint = currentPoint, nextPoint = null;
    	while(!points.isEmpty()) {
    		nextPoint = points.pop();
    		switch(currentLocationPoint.getX() - nextPoint.getX()){
                case 1:
                    nextDirection = Direction.RIGHT;
                    break;
                case -1:
                    nextDirection = Direction.LEFT;
                    break;
    		}
    		switch(currentLocationPoint.getY() - nextPoint.getY()){
                case 1:
                    nextDirection = Direction.UP;
                    break;
                case -1:
                    nextDirection = Direction.DOWN;
                    break;
    		}
			faceDirection(currentDirection, nextDirection);
			actions.add(Action.FORWARD);
			currentDirection = nextDirection;
			currentLocationPoint = nextPoint;
    	}
      	direction = currentDirection;
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
        Set<Point> neighbors = cave.getKnownAdjacentPoints(point);

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
  