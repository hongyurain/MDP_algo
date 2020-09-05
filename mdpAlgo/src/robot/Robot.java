package robot;

import map.Map;
import map.MapConstant;
import map.Cell;
import robot.RobotConstants.DIRECTION;
import robot.RobotConstants.MOVEMENT;
import utils.*;

import java.util.concurrent.TimeUnit;

public class Robot {
	// Center Cell
	private int row;
	private int col; 
	
	/**
	 * Cell information of the robot
	 * Deciding if we really need this
	 */
	private Cell cell;
	
	public DIRECTION curDir;
	
    private DIRECTION robotDir;
    
    private int speed;
    
    private final Sensor SRFrontLeft;       // north-facing front-left SR
    private final Sensor SRFrontCenter;     // north-facing front-center SR
    private final Sensor SRFrontRight;      // north-facing front-right SR
    private final Sensor SRLeftFront;            // west-facing left SR
    private final Sensor SRLeftBack;           // east-facing right SR
    private final Sensor LRRight;            // west-facing left LR
    private boolean reachedGoal;
    private final boolean realBot;
	
    private boolean leftClear;
    private boolean frontClear;
    private boolean rightClear;
	
    public Robot(int row, int col, boolean realBot) {
        this.row = row;
        this.col = col;
        robotDir = RobotConstants.START_DIR;
        speed = RobotConstants.SPEED;

        this.realBot = realBot;
        
        SRFrontLeft = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col - 1, this.robotDir, "SRFL");
        SRFrontCenter = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col, this.robotDir, "SRFC");
        SRFrontRight = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col + 1, this.robotDir, "SRFR");
        SRLeftFront = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col - 1, findNewDirection(MOVEMENT.LEFT), "SRL");
        SRLeftBack = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col + 1, findNewDirection(MOVEMENT.RIGHT), "SRR");
        LRRight = new Sensor(RobotConstants.SENSOR_LONG_RANGE_L, RobotConstants.SENSOR_LONG_RANGE_H, this.row, this.col - 1, findNewDirection(MOVEMENT.LEFT), "LRL");
    }
	
	public String getCurCellID() {
		return this.cell.getCellID();
	}
	
	public Cell getCurCell() {
		return this.cell;
	}
	
	public DIRECTION getCurDir() {
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
	
	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}
	
	public boolean getRealBot() {
        return realBot;
    }
	
	public void setRobotDir(DIRECTION dir) {
        robotDir = dir;
    }
	
	public void setSpeed(int speed) {
        this.speed = speed;
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
//		updateSensors();
//		this.discoverCellsSim();
	}
	
	/**
     * Takes in a MOVEMENT and moves the robot accordingly by changing its position and direction. Sends the movement
     * if this.realBot is set.
     */
    public void move(MOVEMENT m, boolean sendMoveToAndroid) {
        if (!realBot) {
            // Emulate real movement by pausing execution.
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
        }

        switch (m) {
            case FORWARD:
                switch (this.curDir) {
                    case UP:
                        this.row++;
                        break;
                    case RIGHT:
                        this.col++;
                        break;
                    case DOWN:
                        this.row--;
                        break;
                    case LEFT:
                        this.col--;
                        break;
                }
                break;
            case BACKWARD:
                switch (this.curDir) {
                    case UP:
                    	row--;
                        break;
                    case RIGHT:
                    	col--;
                        break;
                    case DOWN:
                    	row++;
                        break;
                    case LEFT:
                    	col++;
                        break;
                }
                break;
            case RIGHT:
            case LEFT:
                curDir = findNewDirection(m);
                break;
            case CALIBRATE:
                break;
            default:
                System.out.println("Error in Robot.move()!");
                break;
        }

        if (realBot) sendMovement(m, false);
        else System.out.println("Move: " + MOVEMENT.print(m));

        updateTouchedGoal();
    }
    
    /**
     * Overloaded method that calls this.move(MOVEMENT m, boolean sendMoveToAndroid = true).
     */
    public void move(MOVEMENT m) {
        this.move(m, true);
    }
    
    private void sendMovement(MOVEMENT m, boolean sendMoveToAndroid) {
        CommMgr comm = CommMgr.getCommMgr();
        comm.sendMsg(MOVEMENT.print(m) + "", CommMgr.INSTRUCTIONS);
        System.out.println("Bot Current Position: "+this.row + "," + this.col);
        if (m != MOVEMENT.CALIBRATE && sendMoveToAndroid) {
            comm.sendMsg(this.row + "," + this.col + "," + DIRECTION.print(this.curDir), CommMgr.BOT_POS);
        }
    }
    
    public void setSensors() {
        switch (robotDir) {
            case UP:
                SRFrontLeft.setSensor(this.row + 1, this.col - 1, this.robotDir);
                SRFrontCenter.setSensor(this.row + 1, this.col, this.robotDir);
                SRFrontRight.setSensor(this.row + 1, this.col + 1, this.robotDir);
                SRLeftFront.setSensor(this.row + 1, this.col - 1, findNewDirection(MOVEMENT.LEFT));
                SRLeftBack.setSensor(this.row - 1, this.col - 1, findNewDirection(MOVEMENT.LEFT));
                LRRight.setSensor(this.row + 1, this.col + 1, findNewDirection(MOVEMENT.RIGHT));
                break;
            case RIGHT:
                SRFrontLeft.setSensor(this.row + 1, this.col + 1, this.robotDir);
                SRFrontCenter.setSensor(this.row, this.col + 1, this.robotDir);
                SRFrontRight.setSensor(this.row - 1, this.col + 1, this.robotDir);
                SRLeftFront.setSensor(this.row + 1, this.col + 1, findNewDirection(MOVEMENT.LEFT));
                SRLeftBack.setSensor(this.row + 1, this.col - 1, findNewDirection(MOVEMENT.LEFT));
                LRRight.setSensor(this.row - 1, this.col + 1, findNewDirection(MOVEMENT.RIGHT));
                break;
            case DOWN:
                SRFrontLeft.setSensor(this.row - 1, this.col + 1, this.robotDir);
                SRFrontCenter.setSensor(this.row - 1, this.col, this.robotDir);
                SRFrontRight.setSensor(this.row - 1, this.col - 1, this.robotDir);
                SRLeftFront.setSensor(this.row - 1, this.col + 1, findNewDirection(MOVEMENT.LEFT));
                SRLeftBack.setSensor(this.row + 1, this.col + 1, findNewDirection(MOVEMENT.LEFT));
                LRRight.setSensor(this.row - 1, this.col - 1, findNewDirection(MOVEMENT.RIGHT));
                break;
            case LEFT:
                SRFrontLeft.setSensor(this.row - 1, this.col - 1, this.robotDir);
                SRFrontCenter.setSensor(this.row, this.col - 1, this.robotDir);
                SRFrontRight.setSensor(this.row + 1, this.col - 1, this.robotDir);
                SRLeftFront.setSensor(this.row - 1, this.col - 1, findNewDirection(MOVEMENT.LEFT));
                SRLeftBack.setSensor(this.row - 1, this.col + 1, findNewDirection(MOVEMENT.LEFT));
                LRRight.setSensor(this.row + 1, this.col - 1, findNewDirection(MOVEMENT.RIGHT));
                break;
        }

    }
    
    private DIRECTION findNewDirection(MOVEMENT m) {
        if (m == MOVEMENT.RIGHT) {
            return DIRECTION.getNext(curDir);
        } else {
            return DIRECTION.getPrevious(curDir);
        }
    }
    
    // Need MapConstants file
    private void updateTouchedGoal() {
        if (this.row == MapConstant.GOAL_ROW && col == MapConstant.GOAL_COL)
            this.reachedGoal = true;
    }
    
    public int[] sense(Map explorationMap, Map realMap) {
        int[] result = new int[6];

        if (!realBot) {
            result[0] = SRFrontLeft.sense(explorationMap, realMap);
            result[1] = SRFrontCenter.sense(explorationMap, realMap);
            result[2] = SRFrontRight.sense(explorationMap, realMap);
            result[3] = SRLeftFront.sense(explorationMap, realMap);
            result[4] = SRLeftBack.sense(explorationMap, realMap);
            result[5] = LRRight.sense(explorationMap, realMap);
        } else {
            CommMgr comm = CommMgr.getCommMgr();
            String msg = comm.recvMsg();
            String[] msgArr = msg.split("\\|");
            result[0] = Integer.parseInt(msgArr[0].substring(msgArr[0].length()-1));
            result[1] = Integer.parseInt(msgArr[1]);
            result[2] = Integer.parseInt(msgArr[2]);
            result[3] = Integer.parseInt(msgArr[3]);
            result[4] = Integer.parseInt(msgArr[4]);
            result[5] = Integer.parseInt(msgArr[5]);
            //System.out.println("msgArr[6] is ...."+msgArr[6]);
            SRFrontLeft.senseReal(explorationMap, result[1]);
            SRFrontCenter.senseReal(explorationMap, result[2]);
            SRFrontRight.senseReal(explorationMap, result[3]);
            SRLeftFront.senseReal(explorationMap, result[4]);
            SRLeftBack.senseReal(explorationMap, result[5]);
            LRRight.senseReal(explorationMap, result[0]);

            String[] mapStrings = MapDescriptor.generateMapDescriptor(explorationMap);
            comm.sendMsg("md"+mapStrings[0] + " " + mapStrings[1] + " " + this.row + " " + this.col + " " + DIRECTION.print(this.curDir), CommMgr.MAP_STRINGS);
        }

        return result;
    }
    
    public boolean getReachedGoal() {
        return this.reachedGoal;
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
	
//    private void discoverCellsSim() {
//		// Updates cell traveled and explored		
//		//Map.grid[this.row-1][this.col-1].updateTraveled();
//		Map.grid[this.row-1][this.col-1].updateExplored();	
//		
//		//Map.grid[this.row-1][this.col].updateTraveled();
//		Map.grid[this.row-1][this.col].updateExplored();	
//		
//		//Map.grid[this.row-1][this.col+1].updateTraveled();
//		Map.grid[this.row-1][this.col+1].updateExplored();	
//	
//		//Map.grid[this.row][this.col-1].updateTraveled();
//		Map.grid[this.row][this.col-1].updateExplored();	
//	
//		Map.grid[this.row][this.col].updateTraveled();
//		Map.grid[this.row][this.col].updateExplored();	
//		
//		//Map.grid[this.row][this.col+1].updateTraveled();
//		Map.grid[this.row][this.col+1].updateExplored();	
//		
//		//Map.grid[this.row+1][this.col-1].updateTraveled();
//		Map.grid[this.row+1][this.col-1].updateExplored();	
//		
//		//Map.grid[this.row+1][this.col].updateTraveled();
//		Map.grid[this.row+1][this.col].updateExplored();	
//		
//		//Map.grid[this.row+1][this.col+1].updateTraveled();
//		Map.grid[this.row+1][this.col+1].updateExplored();			
//	}
	
	public void rotateRight() {
		switch(this.curDir) {
		case LEFT:
			this.curDir = DIRECTION.UP;
			break;
		case UP:
			this.curDir = DIRECTION.RIGHT;
			break;
		case RIGHT:
			this.curDir = DIRECTION.DOWN;
			break;
		case DOWN:
			this.curDir = DIRECTION.LEFT;
			break;
		}
//		updateSensors();
	}
	
	public void rotateLeft() {
		switch(this.curDir) {
		case LEFT:
			this.curDir = DIRECTION.DOWN;
			break;
		case UP:
			this.curDir = DIRECTION.LEFT;
			break;
		case RIGHT:
			this.curDir = DIRECTION.UP;
			break;
		case DOWN:
			this.curDir = DIRECTION.RIGHT;
			break;
		}
//		updateSensors();
	}
	
//	public void updateSensors() {
//		//add code to update the boolean sensors every time this function is called
//		//this function will be called every time the bot moves e.g. moveForward()
//		//as long as there's one grid detected to be an obstacle or explored, set the leftClear/rightClear/frontClear as false
//		
//		//TEMPORARY CODE THAT USES FAKE SENSORS BELOW
//		this.leftClear = checkLeftSim();
//		this.rightClear = checkRightSim();
//		this.frontClear = checkFrontSim();
//	}
	
	// START OF SIMULATION CODE -----------------------------------------------------------------------
	// FAKE SENSORS -----------------------------------------------------------------------------------
	
//	public boolean checkLeftSim() {
//		
//		switch(this.curDir) {
//		case DOWN:
//			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
//					Map.grid[this.row][this.col+1].getIsTraveled() ||
//					Map.grid[this.row][this.col+1].getIsVirtualWall() 
//					//Map.grid[this.row+1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col+1].getIsVirtualWall()
//					);
//		case RIGHT:
//			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
//					Map.grid[this.row-1][this.col].getIsTraveled() ||
//					Map.grid[this.row-1][this.col].getIsVirtualWall() 
//					//Map.grid[this.row-1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col-1].getIsVirtualWall()
//					);
//		case UP:
//			return (//Map.grid[this.row-1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col-1].getIsVirtualWall() ||
//					Map.grid[this.row][this.col-1].getIsTraveled() ||
//					Map.grid[this.row][this.col-1].getIsVirtualWall() 
//					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
//					);
//		case LEFT:
//			return (//Map.grid[this.row+1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col+1].getIsVirtualWall() ||
//					Map.grid[this.row+1][this.col].getIsTraveled() ||
//					Map.grid[this.row+1][this.col].getIsVirtualWall() 
//					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
//					);
//		default:
//			return false;
//		}
//	}
//	
//	public boolean checkFrontSim() {
//		switch(this.curDir) {
//		case RIGHT:
//			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
//					Map.grid[this.row][this.col+1].getIsTraveled() ||
//					Map.grid[this.row][this.col+1].getIsVirtualWall() 
//					//Map.grid[this.row+1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col+1].getIsVirtualWall()
//					);
//		case UP:
//			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
//					Map.grid[this.row-1][this.col].getIsTraveled() ||
//					Map.grid[this.row-1][this.col].getIsVirtualWall() 
//					//Map.grid[this.row-1][this.col-1].getIsExplored() || 
//					//Map.grid[this.row-1][this.col-1].getIsVirtualWall()
//					);
//		case LEFT:
//			return (//Map.grid[this.row-1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col-1].getIsVirtualWall() ||
//					Map.grid[this.row][this.col-1].getIsTraveled() ||
//					Map.grid[this.row][this.col-1].getIsVirtualWall() 
//					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
//					);
//		case DOWN:
//			return (//Map.grid[this.row+1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col+1].getIsVirtualWall() ||
//					Map.grid[this.row+1][this.col].getIsTraveled() ||
//					Map.grid[this.row+1][this.col].getIsVirtualWall() 
//					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
//					);
//		default:
//			return false;
//		}
//	}
//	
//	public boolean checkRightSim() {
//		switch(this.curDir) {
//		case UP:
//			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
//					Map.grid[this.row][this.col+1].getIsTraveled() ||
//					Map.grid[this.row][this.col+1].getIsVirtualWall() 
//					//Map.grid[this.row+1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col+1].getIsVirtualWall()
//					);
//		case LEFT:
//			return (//Map.grid[this.row-1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col+1].getIsVirtualWall() ||
//					Map.grid[this.row-1][this.col].getIsTraveled() ||
//					Map.grid[this.row-1][this.col].getIsVirtualWall()
//					//Map.grid[this.row-1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col-1].getIsVirtualWall()
//					);
//		case DOWN:
//			return (//Map.grid[this.row-1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row-1][this.col-1].getIsVirtualWall() ||
//					Map.grid[this.row][this.col-1].getIsTraveled() ||
//					Map.grid[this.row][this.col-1].getIsVirtualWall() 
//					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
//					);
//		case RIGHT:
//			return (//Map.grid[this.row+1][this.col+1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col+1].getIsVirtualWall() ||
//					Map.grid[this.row+1][this.col].getIsTraveled() ||
//					Map.grid[this.row+1][this.col].getIsVirtualWall() 
//					//Map.grid[this.row+1][this.col-1].getIsExplored() ||
//					//Map.grid[this.row+1][this.col-1].getIsVirtualWall()
//					);
//		default:
//			return false;
//		}
//	}
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
