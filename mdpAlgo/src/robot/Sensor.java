package robot;


import map.Map;
import map.VisitedCell;
import robot.RobotConstant.Direction;

public class Sensor {
	private final int lowerRange;
    private final int upperRange;
    private int sensorPosRow;
    private int sensorPosCol;
    private Direction sensorDir;
    private final String id;

    public Sensor(int lowerRange, int upperRange, int row, int col, Direction dir, String id) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
        this.id = id;
    }

    public void setSensor(int row, int col, Direction dir) {
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
    }
     
    
    
    
}
