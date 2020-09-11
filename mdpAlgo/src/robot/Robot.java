package robot;

import map.Map;
import map.MapConstants;
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
	
    private DIRECTION robotDir;
    
    private int speed;
    
    
	/**
	 * 
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
    private final Sensor SRFrontLeft;       // north-facing front-left SR
    private final Sensor SRFrontCenter;     // north-facing front-center SR
    private final Sensor SRFrontRight;      // north-facing front-right SR
    private final Sensor SRLeftFront;            // west-facing left SR
    private final Sensor SRLeftBack;           // east-facing right SR
    private final Sensor LRRight;             // west-facing left LR
    private boolean touchedGoal;
    private final boolean realBot;
	
    public Robot(int row, int col, boolean realBot) {
        this.row = row;
        this.col = col;
        robotDir = RobotConstants.START_DIR;
        speed = RobotConstants.SPEED;
        
        this.realBot = realBot;
        
        SRFrontLeft = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col - 1, this.robotDir, "SRFL");
        SRFrontCenter = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col, this.robotDir, "SRFC");
        SRFrontRight = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col + 1, this.robotDir, "SRFR");
        SRLeftFront = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row + 1, this.col - 1, findNewDirection(MOVEMENT.TURNL), "SRL");
        SRLeftBack = new Sensor(RobotConstants.SENSOR_SHORT_RANGE_L, RobotConstants.SENSOR_SHORT_RANGE_H, this.row - 1, this.col - 1, findNewDirection(MOVEMENT.TURNL), "SRR");
        LRRight = new Sensor(RobotConstants.SENSOR_LONG_RANGE_L, RobotConstants.SENSOR_LONG_RANGE_H, this.row, this.col + 1, findNewDirection(MOVEMENT.TURNR), "LRL");
    }
	
	
	public void setRobotPos(int row, int col) {
        this.row = row;
        this.col = col;
    }
	
	public DIRECTION getrobotDir() {
		return this.robotDir;
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

	private void updateTouchedGoal() {
        if (this.getRow() == MapConstants.GOAL_ROW && this.getCol() == MapConstants.GOAL_COL)
            this.touchedGoal = true;
    }

    public boolean getTouchedGoal() {
        return this.touchedGoal;
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
                switch (this.robotDir) {
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
                switch (this.robotDir) {
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
            case TURNL:
            case TURNR:
                robotDir = findNewDirection(m);
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
    
    /**
     * Sends a number instead of 'F' for multiple continuous forward movements.
     */
    public void moveForwardMultiple(int count) {
        if (count == 1) {
            move(MOVEMENT.FORWARD);
        } else {
            CommMgr comm = CommMgr.getCommMgr();
            if (count == 10) {
                comm.sendMsg("0", CommMgr.INSTRUCTIONS);
            } else if (count < 10) {
                comm.sendMsg(Integer.toString(count), CommMgr.INSTRUCTIONS);
            }

            switch (robotDir) {
                case UP:
                    row += count;
                    break;
                case RIGHT:
                    col += count;
                    break;
                case DOWN:
                    row += count;
                    break;
                case LEFT:
                    col += count;
                    break;
            }

            comm.sendMsg(this.getRow() + "," + this.getCol() + "," + DIRECTION.print(this.getrobotDir()), CommMgr.BOT_POS);
        }
    }
    
    private void sendMovement(MOVEMENT m, boolean sendMoveToAndroid) {
        CommMgr comm = CommMgr.getCommMgr();
        comm.sendMsg(MOVEMENT.print(m) + "", CommMgr.INSTRUCTIONS);
        System.out.println("Bot Current Position: "+this.row + "," + this.col);
        if (m != MOVEMENT.CALIBRATE && sendMoveToAndroid) {
            comm.sendMsg(this.row + "," + this.col + "," + DIRECTION.print(this.robotDir), CommMgr.BOT_POS);
        }
    }
    
    public void setSensors() {
        switch (robotDir) {
            case UP:
                SRFrontLeft.setSensor(this.row + 1, this.col - 1, this.robotDir);
                SRFrontCenter.setSensor(this.row + 1, this.col, this.robotDir);
                SRFrontRight.setSensor(this.row + 1, this.col + 1, this.robotDir);
                SRLeftFront.setSensor(this.row + 1, this.col - 1, findNewDirection(MOVEMENT.TURNL));
                SRLeftBack.setSensor(this.row - 1, this.col - 1, findNewDirection(MOVEMENT.TURNL));
                LRRight.setSensor(this.row, this.col + 1, findNewDirection(MOVEMENT.TURNR));
                break;
            case RIGHT:
                SRFrontLeft.setSensor(this.row + 1, this.col + 1, this.robotDir);
                SRFrontCenter.setSensor(this.row, this.col + 1, this.robotDir);
                SRFrontRight.setSensor(this.row - 1, this.col + 1, this.robotDir);
                SRLeftFront.setSensor(this.row + 1, this.col + 1, findNewDirection(MOVEMENT.TURNL));
                SRLeftBack.setSensor(this.row + 1, this.col - 1, findNewDirection(MOVEMENT.TURNL));
                LRRight.setSensor(this.row - 1, this.col, findNewDirection(MOVEMENT.TURNR));
                break;
            case DOWN:
                SRFrontLeft.setSensor(this.row - 1, this.col + 1, this.robotDir);
                SRFrontCenter.setSensor(this.row - 1, this.col, this.robotDir);
                SRFrontRight.setSensor(this.row - 1, this.col - 1, this.robotDir);
                SRLeftFront.setSensor(this.row - 1, this.col + 1, findNewDirection(MOVEMENT.TURNL));
                SRLeftBack.setSensor(this.row + 1, this.col + 1, findNewDirection(MOVEMENT.TURNL));
                LRRight.setSensor(this.row, this.col - 1, findNewDirection(MOVEMENT.TURNR));
                break;
            case LEFT:
                SRFrontLeft.setSensor(this.row - 1, this.col - 1, this.robotDir);
                SRFrontCenter.setSensor(this.row, this.col - 1, this.robotDir);
                SRFrontRight.setSensor(this.row + 1, this.col - 1, this.robotDir);
                SRLeftFront.setSensor(this.row - 1, this.col - 1, findNewDirection(MOVEMENT.TURNL));
                SRLeftBack.setSensor(this.row - 1, this.col + 1, findNewDirection(MOVEMENT.TURNL));
                LRRight.setSensor(this.row + 1, this.col, findNewDirection(MOVEMENT.TURNR));
                break;
        }

    }
    
    private DIRECTION findNewDirection(MOVEMENT m) {
        if (m == MOVEMENT.TURNR) {
            return DIRECTION.getNext(robotDir);
        } else {
            return DIRECTION.getPrevious(robotDir);
        }
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
            comm.sendMsg("md"+mapStrings[0] + " " + mapStrings[1] + " " + this.row + " " + this.col + " " + DIRECTION.print(this.robotDir), CommMgr.MAP_STRINGS);
        }

        return result;
    }
}