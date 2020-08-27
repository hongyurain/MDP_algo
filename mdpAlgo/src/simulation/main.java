package algorithms;

public class main {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		Map map = new Map();
		
		// Robot's starting position = "B1"
		Robot wall_e = new Robot(1, 1); 

		// Prints the map
		map.printGrid();
		System.out.println();
		
		// Prints the virtual walls
		exploreArea(wall_e, map);
		
	}
	
	// Algorithm to hug left wall (INCOMPLETE)
	public static void exploreArea(Robot robot, Map map) throws InterruptedException {
		int curRow;
		int curCol;
		
		while (!map.checkFullyExplored()) {
			curRow = robot.getCurCell().getRow();
			curCol = robot.getCurCell().getCol();
			System.out.println("Exploring..." + robot.getCurCellID());
			if (!robot.getLeftClear()) {
				System.out.println("Left is clear");
				robot.rotateLeft();
				robot.moveForward();
			}
			else if (!robot.getFrontClear()) {
				System.out.println("Front is clear");
				robot.moveForward();
			}
			else if (!robot.getRightClear()) {
				System.out.println("Right is clear");
				robot.rotateRight();
			}
			else break;
			
			System.out.println("--------------");
			Thread.sleep(100);
			
		}	
		System.out.println("All explored!");
	}

}
