package robot;

import map.Map;
import map.MapConstant;
import map.Cell;
import robot.RobotConstant.Direction;


public class Robot {
	private int row;
	private int col;
	
	// Cell information of the robot
	private Cell cell;
	
	public Direction curDir = Direction.RIGHT;
	
	
	private boolean leftClear;
	private boolean rightClear;
	private boolean frontClear;
	
	
	
	public Robot(int row, int col) {
		this.row = row;
		this.col = col;
		this.cell = new Cell(row, col);
		Map.grid[this.row][this.col].updateExplored();
		Map.grid[this.row][this.col].updateTraveled();
		updateSensors();
	}
	
	public String getCurCellID() {
		return this.cell.getCellID();
	}
	
	public Cell getCurCell() {
		return this.cell;
	}
	
	public Direction getCurDir() {
		return this.curDir;
	}
	
	public boolean getLeftClear() {
		return this.leftClear;
	}
	
	public boolean getRightClear() {
		return this.rightClear;
	}
	
	public boolean getFrontClear() {
		return this.frontClear;
	}
	
	public void moveForward() {
		switch(this.curDir) {
		case LEFT:
			this.col = this.col-1;
			System.out.println("Moved left");
			break;
		case UP:
			this.row = this.row-1;
			System.out.println("Moved up");
			break;
		case RIGHT:
			this.col = this.col+1;
			System.out.println("Moved right");
			break;
		case DOWN:
			this.row = this.row+1;
			System.out.println("Moved down");
			break;
		}
		this.cell = new Cell(this.row, this.col);
		updateSensors();
		this.discoverCellsSim();
	}
	
	/**
	 *  FOR THE SIMULATION ONLY!!!!
	 *  Below diagram shows how the sensors are named
	 *  
	 *  
	 *  
	 *  
	 *  			  (s3_1) (s2_1) (s1_1)
	 *  			  (s3_0) (s2_0) (s1_0)
	 *  (s4_1) (s4_0) (ROBO) (ROBO) (ROBO) (s6_0) (s6_1) (s6_2) (s6_3) (s6_4)
	 *                (ROBO) (ROBO) (ROBO)
	 *  (s5_1) (s5_0) (ROBO) (ROBO) (ROBO)
	 *  
	 */
	private void discoverCellsSim() {
		// Updates cell traveled and explored		
		//Map.grid[this.row-1][this.col-1].updateTraveled();
		Map.grid[this.row-1][this.col-1].updateExplored();	
		
		//Map.grid[this.row-1][this.col].updateTraveled();
		Map.grid[this.row-1][this.col].updateExplored();	
		
		//Map.grid[this.row-1][this.col+1].updateTraveled();
		Map.grid[this.row-1][this.col+1].updateExplored();	
	
		//Map.grid[this.row][this.col-1].updateTraveled();
		Map.grid[this.row][this.col-1].updateExplored();	
	
		Map.grid[this.row][this.col].updateTraveled();
		Map.grid[this.row][this.col].updateExplored();	
		
		//Map.grid[this.row][this.col+1].updateTraveled();
		Map.grid[this.row][this.col+1].updateExplored();	
		
		//Map.grid[this.row+1][this.col-1].updateTraveled();
		Map.grid[this.row+1][this.col-1].updateExplored();	
		
		//Map.grid[this.row+1][this.col].updateTraveled();
		Map.grid[this.row+1][this.col].updateExplored();	
		
		//Map.grid[this.row+1][this.col+1].updateTraveled();
		Map.grid[this.row+1][this.col+1].updateExplored();	
		
		
		
	}
	
	public void rotateRight() {
		switch(this.curDir) {
		case LEFT:
			this.curDir = Direction.UP;
			break;
		case UP:
			this.curDir = Direction.RIGHT;
			break;
		case RIGHT:
			this.curDir = Direction.DOWN;
			break;
		case DOWN:
			this.curDir = Direction.LEFT;
			break;
		}
		updateSensors();
	}
	
	public void rotateLeft() {
		switch(this.curDir) {
		case LEFT:
			this.curDir = Direction.DOWN;
			break;
		case UP:
			this.curDir = Direction.LEFT;
			break;
		case RIGHT:
			this.curDir = Direction.UP;
			break;
		case DOWN:
			this.curDir = Direction.RIGHT;
			break;
		}
		updateSensors();
	}
	
	public void updateSensors() {
		//add code to update the boolean sensors every time this function is called
		//this function will be called every time the bot moves e.g. moveForward()
		//as long as there's one grid detected to be an obstacle or explored, set the leftClear/rightClear/frontClear as false
		
		//TEMPORARY CODE THAT USES FAKE SENSORS BELOW
		this.leftClear = checkLeftSim();
		this.rightClear = checkRightSim();
		this.frontClear = checkFrontSim();
	}
	
	// START OF SIMULATION CODE -----------------------------------------------------------------------
	// FAKE SENSORS -----------------------------------------------------------------------------------
	
	public boolean checkLeftSim() {
		
		switch(this.curDir) {
		case DOWN:
			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
					Map.grid[this.row][this.col+1].getIsTraveled() ||
					Map.grid[this.row][this.col+1].getIsVirtualWall() 
					//Map.grid[this.row+1][this.col+1].getIsExplored() ||
					//Map.grid[this.row+1][this.col+1].getIsVirtualWall()
					);
		case RIGHT:
			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
					Map.grid[this.row-1][this.col].getIsTraveled() ||
					Map.grid[this.row-1][this.col].getIsVirtualWall() 
					//Map.grid[this.row-1][this.col-1].getIsExplored() ||
					//Map.grid[this.row-1][this.col-1].getIsVirtualWall()
					);
		case UP:
			return (//Map.grid[this.row-1][this.col-1].getIsExplored() ||
					//Map.grid[this.row-1][this.col-1].getIsVirtualWall() ||
					Map.grid[this.row][this.col-1].getIsTraveled() ||
					Map.grid[this.row][this.col-1].getIsVirtualWall() 
					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
					);
		case LEFT:
			return (//Map.grid[this.row+1][this.col+1].getIsExplored() ||
					//Map.grid[this.row+1][this.col+1].getIsVirtualWall() ||
					Map.grid[this.row+1][this.col].getIsTraveled() ||
					Map.grid[this.row+1][this.col].getIsVirtualWall() 
					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
					);
		default:
			return false;
		}
	}
	
	public boolean checkFrontSim() {
		switch(this.curDir) {
		case RIGHT:
			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
					Map.grid[this.row][this.col+1].getIsTraveled() ||
					Map.grid[this.row][this.col+1].getIsVirtualWall() 
					//Map.grid[this.row+1][this.col+1].getIsExplored() ||
					//Map.grid[this.row+1][this.col+1].getIsVirtualWall()
					);
		case UP:
			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
					Map.grid[this.row-1][this.col].getIsTraveled() ||
					Map.grid[this.row-1][this.col].getIsVirtualWall() 
					//Map.grid[this.row-1][this.col-1].getIsExplored() || 
					//Map.grid[this.row-1][this.col-1].getIsVirtualWall()
					);
		case LEFT:
			return (//Map.grid[this.row-1][this.col-1].getIsExplored() ||
					//Map.grid[this.row-1][this.col-1].getIsVirtualWall() ||
					Map.grid[this.row][this.col-1].getIsTraveled() ||
					Map.grid[this.row][this.col-1].getIsVirtualWall() 
					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
					);
		case DOWN:
			return (//Map.grid[this.row+1][this.col+1].getIsExplored() ||
					//Map.grid[this.row+1][this.col+1].getIsVirtualWall() ||
					Map.grid[this.row+1][this.col].getIsTraveled() ||
					Map.grid[this.row+1][this.col].getIsVirtualWall() 
					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
					);
		default:
			return false;
		}
	}
	
	public boolean checkRightSim() {
		switch(this.curDir) {
		case UP:
			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
					Map.grid[this.row][this.col+1].getIsTraveled() ||
					Map.grid[this.row][this.col+1].getIsVirtualWall() 
					//Map.grid[this.row+1][this.col+1].getIsExplored() ||
					//Map.grid[this.row+1][this.col+1].getIsVirtualWall()
					);
		case LEFT:
			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
					Map.grid[this.row-1][this.col].getIsTraveled() ||
					Map.grid[this.row-1][this.col].getIsVirtualWall()
					//Map.grid[this.row-1][this.col-1].getIsExplored() ||
					//Map.grid[this.row-1][this.col-1].getIsVirtualWall()
					);
		case DOWN:
			return (//Map.grid[this.row-1][this.col-1].getIsExplored() ||
					//Map.grid[this.row-1][this.col-1].getIsVirtualWall() ||
					Map.grid[this.row][this.col-1].getIsTraveled() ||
					Map.grid[this.row][this.col-1].getIsVirtualWall() 
					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
					);
		case RIGHT:
			return (//Map.grid[this.row+1][this.col+1].getIsExplored() ||
					//Map.grid[this.row+1][this.col+1].getIsVirtualWall() ||
					Map.grid[this.row+1][this.col].getIsTraveled() ||
					Map.grid[this.row+1][this.col].getIsVirtualWall() 
					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
					);
		default:
			return false;
		}
	}
	// END OF SIMULATION CODE -----------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------
	
	
	
	// For copy pasting lmao
	/** switch(this.curDir) {
		case LEFT:
		case UP:
		case RIGHT:
		case DOWN:
		}
	*/
}
