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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


/* map: stores probabilities for each square;
 *      empty = 1, hazardous = 0
 */

public class MyAI extends Agent
{  
	static enum Direction
	{
		UP,
		RIGHT,
		DOWN,
		LEFT
	}
  
	private Direction direction;
	private Point currentPoint;
	private Point lastPoint;
	private Graph cave;
	private Queue<Action> actions;
	private boolean hasArrow;
	private boolean wumpusAlive;
	private boolean climbOut;
  	private boolean justShot;
	
	public MyAI ( )
	{
		// ======================================================================
		// YOUR CODE BEGINS
		// ======================================================================
		currentPoint = Point.getStartingPoint();		// initialized to (1, 1)
		lastPoint = null;
		cave = new Graph();
		
		hasArrow = true;
		wumpusAlive = true;
		climbOut = false;
      	justShot = false;
		
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
			cave.removeWumpusWarning();
		}
		
		if (glitter) {
			climbOut = true;
			return Action.GRAB;
		}
		
		if (climbOut) {
			if (currentPoint.atStart()) {
				return Action.CLIMB;
			}
			if (actions.isEmpty()) {
				navigate(currentPoint, Point.getStartingPoint());
			}
		}
		
		if (bump) {
			// moveForward() adds a new node
			// if a bump is perceived, a new node was added when it shouldn't have been
			// we can mark this node as a WALL, then update currentPoint to be the node that it came from
			// TODO: add right/top wall
			markOutOfBounds(currentPoint.getX(), currentPoint.getY());
			cave.getNode(currentPoint).addMarker(Node.Marker.WALL);
			currentPoint = new Point(lastPoint);
		}
      
		Set<Node.Marker> dangers = new HashSet<>();
		
		if (cave.getNode(currentPoint) != null) {
			cave.getNode(currentPoint).addMarker(Node.Marker.EXPLORED);
		}

		// if an upcoming action has been queued, prioritize it
		if (!actions.isEmpty()) {
			return dequeueAction();
		}
		
		if (stench && wumpusAlive && hasArrow) {
//			Point adjacentPoint = cave.getAdjacentPoint(currentPoint, direction);
//			Node adjacentNode = cave.getNode(adjacentPoint);
//			if (cave.containsPoint(adjacentPoint) && adjacentNode.containsMarker(Node.Marker.WALL)) {
//				// turn to unexplored adjacent node
//			}
			hasArrow = false;
			justShot = true;
			return Action.SHOOT;	
		}
		
		Action action;
		if (breeze || stench) {
			if (breeze) {
				if (currentPoint.atStart()) {
					// climb out?
					climbOut = true;
					return getAction(stench, breeze, glitter, bump, scream);
				}
				dangers.add(Node.Marker.PITWARNING);
			}
			if (stench && wumpusAlive) {
                dangers.add(Node.Marker.WUMPUSWARNING);
			}
//			action = addAndGoToClosestPoint(dangers);
//			currentNode = cave.addNode(currentNode, direction, currentPoint, dangers);
//			Point closestPoint = cave.getClosestUnexploredPoint(currentPoint);
//			navigate(currentPoint, closestPoint);
//			action = dequeueAction(dangers);
		}
		else {
//			action = addAndGoToClosestPoint(dangers);
//			currentNode = cave.addNode(currentNode, direction, currentPoint, dangers);
//			Point closestPoint = cave.getClosestUnexploredPoint(currentPoint);
//			navigate(currentPoint, closestPoint);
//			action = dequeueAction(dangers);
		}
		
		cave.addNode(currentPoint, direction, dangers);
		
//		// attempt to kill the Wumpus
//		if (stench && wumpusAlive && hasArrow && cave.isWumpusFound()) {
//			return killWumpus();
//		}
		
		if (justShot) {
			// shot, but missed
			// remove WUMPUSWARNING from the node directly in front of the agent
			Node currentNode = cave.getNode(currentPoint);
			Node neighbor = cave.getAdjacentNode(currentNode, direction);
			if (neighbor != null) {
				neighbor.removeMarker(Node.Marker.WUMPUSWARNING);
				Point adjacentPoint = cave.getAdjacentPoint(currentPoint, direction);
				if (!neighbor.isDangerous()) {
					cave.addToUnexplored(adjacentPoint);	
				}
			}
		}
		
		Point closestPoint = cave.getClosestUnexploredPoint(currentPoint, direction);
		if (closestPoint == null) {
			climbOut = true;
			actions.clear();
			return getAction(false, false, false, false, false);
		}
		navigate(currentPoint, closestPoint);
		action = dequeueAction();
      
		justShot = false;
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
		lastPoint = new Point(currentPoint);
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
		return Action.FORWARD;
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
	 * 
	 * TODO: test this method for walking through multiple Points
	 */
	private void getActionsFromPoints(Stack<Point> points) {
		Direction currentDirection = direction, nextDirection = null;
		Point currentLocationPoint = currentPoint, nextPoint = null;
		while (!points.isEmpty()) {
			nextPoint = points.pop();
			switch (currentLocationPoint.getX() - nextPoint.getX()){
				case 1:
					nextDirection = Direction.LEFT;
					break;
				case -1:
					nextDirection = Direction.RIGHT;
					break;
			}
			switch (currentLocationPoint.getY() - nextPoint.getY()){
				case 1:
					nextDirection = Direction.DOWN;
					break;
				case -1:
					nextDirection = Direction.UP;
					break;
			}
			faceDirection(currentDirection, nextDirection);
			actions.add(Action.FORWARD);
			currentDirection = nextDirection;
			currentLocationPoint = nextPoint;
		}
	}
	
	/* Marks the unexplored neighbors of the Node at 'point' as HAZARDOUS.
	 * Called on currentPoint when a stench/breeze is perceived. 
	 */
	
	/* Identifies a neighbor of 'point' that has been marked EXPLORED.
	 * Adds actions to the actions queue to navigate to that Point.
	 */
	private void goToExploredNeighborOf(Point point) {
		for (Point adjacentPoint : cave.getKnownAdjacentPoints(point)) {
			Node adjacentNode = cave.getNode(adjacentPoint);
			if (adjacentNode.containsMarker(Node.Marker.EXPLORED)) {
				navigate(point, adjacentPoint);
				break;
			}
		}
	}

	/* Navigates a path from origin to destination.
     * Adds all necessary actions to the actions queue.
	 */
	private void navigate(Point origin, Point destination) {
		Stack<Point> path = cave.getPath(origin, destination, direction);
		getActionsFromPoints(path);
	}

	/* Sets Point.maxX and Point.maxY to their respective arguments.
	 * Note that Point.maxX and Point.maxY are inclusive bounds.
	 * But, this method mandates that you pass currentPoint.getX(), .getY() directly.
	 * Deletes out-of-bounds nodes in the cave.
	 */
	private void markOutOfBounds(int x, int y) {
		switch (direction) {
			case RIGHT:
				Point.setMaxX(x - 1);
				break;
			case UP:
				Point.setMaxY(y - 1);
				break;
			default:
				break;
		}
		cave.deleteOutOfBounds();
	}
	
	/* Convenience method.
	 * Adds current point, finds the closest point, and returns the first necessary Action to take.
	 * If no closest point is found, begins navigating back to the start.
	 */
	private Action addAndGoToClosestPoint(Set<Node.Marker> dangers) {
		cave.addNode(currentPoint, direction, dangers);
		Point closestPoint = cave.getClosestUnexploredPoint(currentPoint, direction);
		if (closestPoint == null) {
			climbOut = true;
			actions.clear();
			return getAction(false, false, false, false, false);
		}
		navigate(currentPoint, closestPoint);
		return dequeueAction();
	}
	
//	private Action killWumpus() {
//		if (cave.getWumpusPoint() == null) {
//			return Action.SHOOT;
//		}
//		
//		int dX = currentPoint.getX() - cave.getWumpusPoint().getX();
//		int dY = currentPoint.getY() - cave.getWumpusPoint().getY();
//		
//		if (dX < 0) {
//			faceDirection(direction, Direction.RIGHT);
//		}
//		else if (dX > 0) {
//			faceDirection(direction, Direction.LEFT);
//		}
//		else if (dY < 0) {
//			faceDirection(direction, Direction.UP);
//		}
//		else if (dY > 0) {
//			faceDirection(direction, Direction.DOWN);
//		}
//		actions.add(Action.SHOOT);
//		return dequeueAction();
//	}
	
//	private boolean facingWumpus() {		
//		int dX = currentPoint.getX() - cave.getWumpusPoint().getX();
//		int dY = currentPoint.getY() - cave.getWumpusPoint().getY();
//		
//		return cave.getWumpusPoint() != null && 
//				dX < 0 && direction == Direction.RIGHT || dX > 0 && direction == Direction.LEFT || 
//				dY < 0 && direction == Direction.UP || dY > 0 && direction == Direction.RIGHT;
//	}

	// ======================================================================
	// YOUR CODE ENDS
	// ======================================================================
}

