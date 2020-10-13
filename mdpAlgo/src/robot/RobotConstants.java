package robot;

/**
 * Constants used in this package.
 *
 * @author Suyash Lakhotia
 */

public class RobotConstants {
    public static final int GOAL_ROW = 18;                          // row no. of goal cell
    public static final int GOAL_COL = 13;                          // col no. of goal cell
    public static final int START_ROW = 1;                          // row no. of start cell
    public static final int START_COL = 1;                          // col no. of start cell
    public static final int MOVE_COST = 10;                         // cost of FORWARD, BACKWARD movement
    public static final int TURN_COST = 20;                         // cost of RIGHT, LEFT movement
    public static final int SPEED = 100;                            // delay between movements (ms)
    public static final DIRECTION START_DIR = DIRECTION.RIGHT;      // start direction
    public static final int SENSOR_SHORT_RANGE_L = 1;               // range of short range sensor (cells)
    public static final int SENSOR_SHORT_RANGE_H = 2;               // range of short range sensor (cells)
    public static final int SENSOR_LONG_RANGE_L = 1;                // range of long range sensor (cells)
    public static final int SENSOR_LONG_RANGE_H = 4;                // range of long range sensor (cells)

    public static final int INFINITE_COST = 9999;

    public enum DIRECTION {
       UP, LEFT, DOWN, RIGHT;

        public static DIRECTION getNext(DIRECTION curDirection) {
            return values()[(curDirection.ordinal() + 1) % values().length];
        }

        public static DIRECTION getPrevious(DIRECTION curDirection) {
            return values()[(curDirection.ordinal() + values().length - 1) % values().length];
        }

        public static char print(DIRECTION d) {
            switch (d) {
                case LEFT:
                    return 'L';
                case UP:
                    return 'U';
                case RIGHT:
                    return 'R';
                case DOWN:
                    return 'D';
                default:
                    return 'X';
            }
        }
    }

    public enum MOVEMENT {
        FORWARD, BACKWARD, TURNR, TURNL, CALIBRATE, CALIBRATEL, ERROR;

        public static char print(MOVEMENT m) {
            switch (m) {
                case FORWARD:
                    return 'f';
                case BACKWARD:
                    return 'b';
                case TURNR:
                    return 'r';
                case TURNL:
                    return 'l';
                case CALIBRATE:
                    return 'c';
                case CALIBRATEL:
                	return 'p';
                case ERROR:
                default:
                    return 'e';
            }
        }
    }
}