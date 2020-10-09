package robot;

import map.Map;
import map.Visited;
import robot.RobotConstants.DIRECTION;

/**
 * Represents a sensor mounted on the robot.
 *
 * @author Yilin
 */

public class Sensor {
    private final int lowerRange;
    private final int upperRange;
    private int sensorPosRow;
    private int sensorPosCol;
    private DIRECTION sensorDir;
    private final String id;

    public Sensor(int lowerRange, int upperRange, int row, int col, DIRECTION dir, String id) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
        this.id = id;
    }

    public void setSensor(int row, int col, DIRECTION dir) {
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
    }

    /**
     * Returns the number of cells to the nearest detected obstacle or -1 if no obstacle is detected.
     */
    public int sense(Map exploredMap, Map realMap) {
    	if ((sensorDir==DIRECTION.UP)||(sensorDir==DIRECTION.DOWN)||(sensorDir==DIRECTION.RIGHT)||(sensorDir==DIRECTION.LEFT)) {
    		return getSensorVal(exploredMap, realMap, ((sensorDir==DIRECTION.UP)?1:0)-((sensorDir==DIRECTION.DOWN)?1:0), ((sensorDir==DIRECTION.RIGHT)?1:0)-((sensorDir==DIRECTION.LEFT)?1:0));
    	}
    	else {
    		return -1;
    	}
    }

    /**
     * Sets the appropriate obstacle cell in the map and returns the row or column value of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    private int getSensorVal(Map exploredMap, Map realMap, int rowInc, int colInc) {
        // Invalid starting point for sensors with lowerRange > 1 => return the offset
        if (lowerRange > 1) {
            for (int i = 1; i < this.lowerRange; i++) {
                int row = this.sensorPosRow + (rowInc * i);
                int col = this.sensorPosCol + (colInc * i);

                if ((!exploredMap.checkValidCoordinates(row, col))||(realMap.getCell(row, col).getIsObstacle())) return i;
            }
        }

        // Obstacle detected by the sensor => set obstable + return the offset
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.checkValidCoordinates(row, col)) return i;

            exploredMap.getCell(row, col).setIsExplored(true);

            if (realMap.getCell(row, col).getIsObstacle()) {
                exploredMap.setObstacleCell(row, col, true);
                return i;
            }
        }

        // Else, no obstable detected => return -1.
        return -1;
    }

    /**
     * Uses the sensor direction and given value from the actual sensor to update the map.
     */
    public void senseReal(Map exploredMap, int sensorVal) {
    	processSensorVal(exploredMap, sensorVal, ((sensorDir==DIRECTION.UP)?1:0)-((sensorDir==DIRECTION.DOWN)?1:0), ((sensorDir==DIRECTION.RIGHT)?1:0)-((sensorDir==DIRECTION.LEFT)?1:0));
    }

    /**
     * Sets the correct cells to explored and/or obstacle according to the actual sensor value.
     */
    private void processSensorVal(Map exploredMap, int sensorVal, int rowInc, int colInc) {
        // if (sensorVal == 0) return;  
    	// return value for LR sensor if obstacle before lowerRange
        // If above fails, check if starting point is valid for sensors with lowerRange > 1.
        for (int i = 1; i < this.lowerRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if ((!exploredMap.checkValidCoordinates(row, col))||(exploredMap.getCell(row, col).getIsObstacle())) return;
        }

        // Update map according to sensor's value
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.checkValidCoordinates(row, col)) continue;

            exploredMap.getCell(row, col).setIsExplored(true);

            if (sensorVal + 1 == i) {
                if (Visited.visitedArr[row][col]==0) exploredMap.setObstacleCell(row, col, true);
                break;
            }

            // Override previous obstacle value if front sensors detect no obstacle
            if (exploredMap.getCell(row, col).getIsObstacle()) {
                if (id.equals("SRFL") || id.equals("SRFC") || id.equals("SRFR")) {
                    exploredMap.setObstacleCell(row, col, false);
                } else {
                    break;
                }
            }
        }
    }
}
