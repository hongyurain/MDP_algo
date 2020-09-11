package algorithms;

import map.Cell;
import map.Map;
import map.MapConstants;
import robot.Robot;
import robot.RobotConstants;
import robot.RobotConstants.DIRECTION;
import robot.RobotConstants.MOVEMENT;
import utils.CommMgr;
import algorithms.FastestPathAlgo;
import utils.MapDescriptor;
import map.Visited;

/**
 * Exploration algorithm for the robot.
 *
 * @author Priyanshu Singh
 * @author Suyash Lakhotia
 */

public class ExplorationAlgo {
    private final Map exploredMap;
    private final Map realMap;
    private final Robot bot;
    private final int coverageLimit;
    private final int timeLimit;
    private int areaExplored;
    private long startTime;
    private long endTime;
    private int lastCalibrate;
    private boolean calibrationMode;

    public ExplorationAlgo(Map exploredMap, Map realMap, Robot bot, int coverageLimit, int timeLimit) {
        this.exploredMap = exploredMap;
        this.realMap = realMap;
        this.bot = bot;
        this.coverageLimit = coverageLimit;
        this.timeLimit = timeLimit;
    }

    /**
     * Main method that is called to start the exploration.
     */
    public void runExploration() {
        if (bot.getRealBot()) {
            System.out.println("Starting calibration...");

            //CommMgr.getCommMgr().recvMsg();
            if (bot.getRealBot()) {
                bot.move(MOVEMENT.TURNR, false);
                CommMgr.getCommMgr().recvMsg();
                bot.move(MOVEMENT.CALIBRATE, false);
                CommMgr.getCommMgr().recvMsg();
                bot.move(MOVEMENT.TURNR, false);
                CommMgr.getCommMgr().recvMsg();
                bot.move(MOVEMENT.CALIBRATE, false);
                CommMgr.getCommMgr().recvMsg();
                bot.move(MOVEMENT.TURNL, false);
                CommMgr.getCommMgr().recvMsg();
                bot.move(MOVEMENT.CALIBRATE, false);
                CommMgr.getCommMgr().recvMsg();
                bot.move(MOVEMENT.TURNL, false);
            }

            while (true) {
                System.out.println("Waiting for EX_START...");
                String msg = CommMgr.getCommMgr().recvMsg();
                //String[] msgArr = msg.split(";");
                if (msg.equals(CommMgr.EX_START)) break;
            }
        }

        System.out.println("Starting exploration...");

        startTime = System.currentTimeMillis();
        endTime = startTime + (timeLimit * 1000);

        if (bot.getRealBot()) {
            CommMgr.getCommMgr().sendMsg(null, CommMgr.BOT_START);
        }
        // senseAndRepaint();
        bot.setSensors();
        // bot.sense(exploredMap, realMap);
        exploredMap.repaint();


        areaExplored = calculateAreaExplored();
        System.out.println("Explored Area: " + areaExplored);

        explorationLoop(bot.getRow(), bot.getCol());

        exploredMap.repaint();
        String[] mapStrings = MapDescriptor.generateMapDescriptor(exploredMap);
        CommMgr.getCommMgr().sendMsg("md"+mapStrings[0] + " " + mapStrings[1] + " " + bot.getRow() + " " + bot.getCol() + " " + DIRECTION.print(bot.getrobotDir()), CommMgr.MAP_STRINGS);
        CommMgr.getCommMgr().sendMsg("END", CommMgr.BOT_POS);
    }


    private void updateVisited(int row, int col){
        Visited.visitedArr[row][col]=1;
        Visited.visitedArr[row-1][col-1]=1;
        Visited.visitedArr[row-1][col]=1;
        Visited.visitedArr[row-1][col+1]=1;
        Visited.visitedArr[row][col-1]=1;
        Visited.visitedArr[row][col+1]=1;
        Visited.visitedArr[row+1][col-1]=1;
        Visited.visitedArr[row+1][col]=1;
        Visited.visitedArr[row+1][col+1]=1;
    }


    /**
     * Loops through robot movements until one (or more) of the following conditions is met:
     * 1. Robot is back at (r, c)
     * 2. areaExplored > coverageLimit
     * 3. System.currentTimeMillis() > endTime
     */
    private void explorationLoop(int r, int c) {
        do {
            updateVisited(bot.getRow(), bot.getCol());
            nextMove();
            areaExplored = calculateAreaExplored();
            System.out.println("Area explored: " + areaExplored);

            if (bot.getRow() == r && bot.getCol() == c) {
                if (areaExplored >= 100) {
                    break;
                }
            }
        } while (areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime);
        
        /*int countNoOfRevisits = 0;
        while (areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime && countNoOfRevisits < 4){
            countNoOfRevisits++;
            
            //calcualate the rectangle
            
            FastestPathAlgo fp;
            fp = new FastestPathAlgo(exploredMap, bot);

        }*/

        goHome();
    }

    /**
     * Determines the next move for the robot and executes it accordingly.
     */
    private void nextMove() {
        if (lookLeft()) {
            moveBot(MOVEMENT.TURNL);
            if (lookForward()) moveBot(MOVEMENT.FORWARD);
        } else if (lookForward()) {
            moveBot(MOVEMENT.FORWARD);
        } else if (lookRight()) {
            moveBot(MOVEMENT.TURNR);
            if (lookForward()) moveBot(MOVEMENT.FORWARD);
        } else {
            moveBot(MOVEMENT.TURNR);
            moveBot(MOVEMENT.TURNR);
        }
    }

    /**
     * Returns true if the right side of the robot is free to move into.
     */
    private boolean lookRight() {
        switch (bot.getrobotDir()) {
            case UP:
                return RIGHTFree();
            case RIGHT:
                return DOWNFree();
            case DOWN:
                return LEFTFree();
            case LEFT:
                return UPFree();
        }
        return false;
    }

    /**
     * Returns true if the robot is free to move forward.
     */
    private boolean lookForward() {
        switch (bot.getrobotDir()) {
            case UP:
                return UPFree();
            case RIGHT:
                return RIGHTFree();
            case DOWN:
                return DOWNFree();
            case LEFT:
                return LEFTFree();
        }
        return false;
    }

    /**
     * * Returns true if the left side of the robot is free to move into.
     */
    private boolean lookLeft() {
        switch (bot.getrobotDir()) {
            case UP:
                return LEFTFree();
            case RIGHT:
                return UPFree();
            case DOWN:
                return RIGHTFree();
            case LEFT:
                return DOWNFree();
        }
        return false;
    }

    /**
     * Returns true if the robot can move to the UP cell.
     */
    private boolean UPFree() {
        int botRow = bot.getRow();
        int botCol = bot.getCol();
        return (isExploredNotObstacle(botRow + 1, botCol - 1) && isExploredAndFree(botRow + 1, botCol) && isExploredNotObstacle(botRow + 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the RIGHT cell.
     */
    private boolean RIGHTFree() {
        int botRow = bot.getRow();
        int botCol = bot.getCol();
        return (isExploredNotObstacle(botRow - 1, botCol + 1) && isExploredAndFree(botRow, botCol + 1) && isExploredNotObstacle(botRow + 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the DOWN cell.
     */
    private boolean DOWNFree() {
        int botRow = bot.getRow();
        int botCol = bot.getCol();
        return (isExploredNotObstacle(botRow - 1, botCol - 1) && isExploredAndFree(botRow - 1, botCol) && isExploredNotObstacle(botRow - 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the LEFT cell.
     */
    private boolean LEFTFree() {
        int botRow = bot.getRow();
        int botCol = bot.getCol();
        return (isExploredNotObstacle(botRow - 1, botCol - 1) && isExploredAndFree(botRow, botCol - 1) && isExploredNotObstacle(botRow + 1, botCol - 1));
    }

    /**
     * Returns the robot to START after exploration and points the bot UPwards.
     */
    private void goHome() {
        if (!bot.getTouchedGoal() && coverageLimit == 300 && timeLimit == 3600) {
            FastestPathAlgo goToGoal = new FastestPathAlgo(exploredMap, bot, realMap);
            goToGoal.runFastestPath(RobotConstants.GOAL_ROW, RobotConstants.GOAL_COL);
        }

        FastestPathAlgo returnToStart = new FastestPathAlgo(exploredMap, bot, realMap);
        returnToStart.runFastestPath(RobotConstants.START_ROW, RobotConstants.START_COL);

        System.out.println("Exploration complete!");
        areaExplored = calculateAreaExplored();
        System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
        System.out.println(", " + areaExplored + " Cells");
        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");

        if (bot.getRealBot()) {
            turnBotDirection(DIRECTION.LEFT);
            moveBot(MOVEMENT.CALIBRATE);
            moveBot(MOVEMENT.CALIBRATE);
            turnBotDirection(DIRECTION.DOWN);
            moveBot(MOVEMENT.CALIBRATE);
            moveBot(MOVEMENT.CALIBRATE);
            turnBotDirection(DIRECTION.LEFT);
            moveBot(MOVEMENT.CALIBRATE);
            moveBot(MOVEMENT.CALIBRATE);
        }
        turnBotDirection(DIRECTION.UP);
    }

    /**
     * Returns true for cells that are explored and not obstacles.
     */
    private boolean isExploredNotObstacle(int r, int c) {
        if (exploredMap.checkValidCoordinates(r, c)) {
            Cell tmp = exploredMap.getCell(r, c);
            return (tmp.getIsExplored() && !tmp.getIsObstacle());
        }
        return false;
    }

    /**
     * Returns true for cells that are explored, not virtual walls and not obstacles.
     */
    private boolean isExploredAndFree(int r, int c) {
        if (exploredMap.checkValidCoordinates(r, c)) {
            Cell b = exploredMap.getCell(r, c);
            return (b.getIsExplored() && !b.getIsVirtualWall() && !b.getIsObstacle());
        }
        return false;
    }

    /**
     * Returns the number of cells explored in the grid.
     */
    private int calculateAreaExplored() {
        int result = 0;
        for (int r = 0; r < MapConstants.MAP_ROWS; r++) {
            for (int c = 0; c < MapConstants.MAP_COLS; c++) {
                if (exploredMap.getCell(r, c).getIsExplored()) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * Moves the bot, repaints the map and calls senseAndRepaint().
     */
    private void moveBot(MOVEMENT m) {
        bot.move(m);
        exploredMap.repaint();
        if (m != MOVEMENT.CALIBRATE) {
            senseAndRepaint();
        } else {
            CommMgr commMgr = CommMgr.getCommMgr();
            commMgr.recvMsg();
        }

        if (bot.getRealBot() && !calibrationMode) {
            calibrationMode = true;

            if (canCalibrateOnTheSpot(bot.getrobotDir())) {
                lastCalibrate = 0;
                moveBot(MOVEMENT.CALIBRATE);
            } else {
                lastCalibrate++;
                if (lastCalibrate >= 5) {
                    DIRECTION targetDir = getCalibrationDirection();
                    if (targetDir != null) {
                        lastCalibrate = 0;
                        calibrateBot(targetDir);
                    }
                }
            }

            calibrationMode = false;
        }
    }

    /**
     * Sets the bot's sensors, processes the sensor data and repaints the map.
     */
    private void senseAndRepaint() {
        bot.setSensors();
        bot.sense(exploredMap, realMap);
        exploredMap.repaint();
    }

    /**
     * Checks if the robot can calibrate at its current position given a direction.
     */
    private boolean canCalibrateOnTheSpot(DIRECTION botDir) {
        int row = bot.getRow();
        int col = bot.getCol();

        switch (botDir) {
            case UP:
                return exploredMap.getIsObstacleOrWall(row + 2, col - 1) && exploredMap.getIsObstacleOrWall(row + 2, col) && exploredMap.getIsObstacleOrWall(row + 2, col + 1);
            case RIGHT:
                return exploredMap.getIsObstacleOrWall(row + 1, col + 2) && exploredMap.getIsObstacleOrWall(row, col + 2) && exploredMap.getIsObstacleOrWall(row - 1, col + 2);
            case DOWN:
                return exploredMap.getIsObstacleOrWall(row - 2, col - 1) && exploredMap.getIsObstacleOrWall(row - 2, col) && exploredMap.getIsObstacleOrWall(row - 2, col + 1);
            case LEFT:
                return exploredMap.getIsObstacleOrWall(row + 1, col - 2) && exploredMap.getIsObstacleOrWall(row, col - 2) && exploredMap.getIsObstacleOrWall(row - 1, col - 2);
        }

        return false;
    }

    /**
     * Returns a possible direction for robot calibration or null, otherwise.
     */
    private DIRECTION getCalibrationDirection() {
        DIRECTION origDir = bot.getrobotDir();
        DIRECTION dirToCheck;

        dirToCheck = DIRECTION.getNext(origDir);                    // left turn
        if (canCalibrateOnTheSpot(dirToCheck)) return dirToCheck;

        dirToCheck = DIRECTION.getPrevious(origDir);                // right turn
        if (canCalibrateOnTheSpot(dirToCheck)) return dirToCheck;

        dirToCheck = DIRECTION.getPrevious(dirToCheck);             // u turn
        if (canCalibrateOnTheSpot(dirToCheck)) return dirToCheck;

        return null;
    }

    /**
     * Turns the bot in the needed direction and sends the CALIBRATE movement. Once calibrated, the bot is turned back
     * to its original direction.
     */
    private void calibrateBot(DIRECTION targetDir) {
        DIRECTION origDir = bot.getrobotDir();

        turnBotDirection(targetDir);
        moveBot(MOVEMENT.CALIBRATE);
        turnBotDirection(origDir);
    }

    /**
     * Turns the robot to the required direction.
     */
    private void turnBotDirection(DIRECTION targetDir) {
        int numOfTurn = Math.abs(bot.getrobotDir().ordinal() - targetDir.ordinal());
        if (numOfTurn > 2) numOfTurn = numOfTurn % 2;

        if (numOfTurn == 1) {
            if (DIRECTION.getNext(bot.getrobotDir()) == targetDir) {
                moveBot(MOVEMENT.TURNL);
            } else {
                moveBot(MOVEMENT.TURNR);
            }
        } else if (numOfTurn == 2) {
            moveBot(MOVEMENT.TURNR);
            moveBot(MOVEMENT.TURNR);
        }
    }
}
