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
		map.printVirtual();
		
	}
	
	// Algorithm to hug left wall (INCOMPLETE)
	public void exploreArea(Robot robot, Map map) {
		int curRow;
		int curCol;
		
		while (!map.checkFullyExplored()) {
			curRow = robot.getCurCell().getRow();
			curCol = robot.getCurCell().getCol();
			
		}		
	}

}
