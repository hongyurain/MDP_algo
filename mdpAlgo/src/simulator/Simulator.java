package simulator;

import algorithms.ExplorationAlgo;
import algorithms.FastestPathAlgo;
import map.Map;
import map.MapConstants;
import map.Visited;
import robot.Robot;
import robot.RobotConstants;
import robot.RobotConstants.DIRECTION;
import robot.RobotConstants.MOVEMENT;
import utils.CommMgr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static utils.MapDescriptor.generateMapDescriptor;
import static utils.MapDescriptor.loadMapFromDisk;

/**
 * Simulator for robot navigation in virtual arena.
 *
 * @author Suyash Lakhotia
 */

public class Simulator {
    private static JFrame _appFrame = null;         // application JFrame

    private static JPanel _mapCards = null;         // JPanel for map views
    private static JPanel _buttons = null;          // JPanel for buttons

    private static Robot bot;

    private static Map realMap = null;              // real map
    private static Map exploredMap = null;          // exploration map

    private static int speedLimit = robot.RobotConstants.SPEED;				// speed limit
    private static int timeLimit = 3600;            // time limit
    private static int coverageLimit = 300;         // coverage limit

    private static final CommMgr comm = CommMgr.getCommMgr();
    private static int fpRow;
    private static int fpCol;
    private static final boolean realRun = true;

    /**
     * Initialises the different maps and displays the application.
     */
    public static void main(String[] args) {
        if (realRun) {
            comm.openConnection();

            // Get waypoint from Android
            System.out.println("Waiting for waypoint from Android");
            String wpString = comm.recvMsg();
            if (wpString.substring(0,2).equals("wp")) {
            	String[] waypoints = wpString.substring(2).split(",");
            	fpRow = Integer.valueOf(waypoints[0]);
                fpCol = Integer.valueOf(waypoints[1]);
                System.out.println("fpRow: " + fpRow);
                System.out.println("fpCol: " + fpCol);
            }
        } 

        int start_row = RobotConstants.START_ROW;
        int start_col = RobotConstants.START_COL;
        //listen if got start coors
        //update start_row, start_col
        
        bot = new Robot(start_row, start_col, realRun);

        if (!realRun) {
        	fpRow = 8;
            fpCol = 12;
            realMap = new Map(bot);
            realMap.setAllUnexplored();
        }

        exploredMap = new Map(bot);
        exploredMap.setAllUnexplored();

        displayEverything();
    }


    /**
     * Initialises the different parts of the application.
     */
    private static void displayEverything() {
        // Initialise main frame for display
        _appFrame = new JFrame();
        _appFrame.setTitle("MDP Simulator");
        _appFrame.setSize(new Dimension(690, 700));
        _appFrame.setResizable(false);

        // Center the main frame in the middle of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        _appFrame.setLocation(dim.width / 2 - _appFrame.getSize().width / 2, dim.height / 2 - _appFrame.getSize().height / 2);

        // Create the CardLayout for storing the different maps
        _mapCards = new JPanel(new CardLayout());

        // Create the JPanel for the buttons
        _buttons = new JPanel();

        // Add _mapCards & _buttons to the main frame's content pane
        Container contentPane = _appFrame.getContentPane();
        contentPane.add(_mapCards, BorderLayout.CENTER);
        contentPane.add(_buttons, BorderLayout.PAGE_END);

        // Initialize the main map view
        initMainLayout();

        // Initialize the buttons
        initButtonsLayout();

        // Display the application
        _appFrame.setVisible(true);
        _appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
//        if (realRun) {
//        	int row, col;
//
//            row = RobotConstants.START_ROW;
//            col = RobotConstants.START_COL;
//
//            bot.setRobotPos(row, col);
//            exploredMap.repaint();
//
//            ExplorationAlgo exploration;
//            exploration = new ExplorationAlgo(exploredMap, realMap, bot, coverageLimit, timeLimit);
//            System.out.println("before running into runExploration");
//            exploration.runExploration();
//            generateMapDescriptor(exploredMap);
//            
//            /////////////////////////////////////////
//            //new FastestPath().execute();
//            
//            bot.setRobotPos(RobotConstants.START_ROW, RobotConstants.START_COL);
//            exploredMap.repaint();
//            
//            FastestPathAlgo fastestPathToWayPoint;
//            fastestPathToWayPoint = new FastestPathAlgo(exploredMap, bot);
//            String fp1 = fastestPathToWayPoint.runFastestPath(fpRow,fpCol);
//            bot.setRobotPos(fpRow,fpCol);
//
//            System.out.println("B4 goal: "+bot.getRow()+ ", "+bot.getCol());
//            FastestPathAlgo fastestPathToGoal;
//            fastestPathToGoal = new FastestPathAlgo(exploredMap, bot);
//            String fp2 = fastestPathToGoal.runFastestPath(RobotConstants.GOAL_ROW, RobotConstants.GOAL_COL);
//            String fpInstructions="";
//            if(Character.isUpperCase(fp1.charAt(fp1.length()-1)) && Character.isUpperCase(fp2.charAt(0))){
//                char c = (char)(fp1.charAt(fp1.length()-1)+fp2.charAt(0)-64);
//                fpInstructions = fpInstructions + fp1.substring(0,fp1.length()-1) + c + fp2.substring(1);
//            }   
//            else
//            {
//                fpInstructions= fpInstructions+fp1+fp2;
//            }
//            if (realRun) {
//                 while (true) {
//                     System.out.println("Waiting for FP_START...");
//                     String msg = comm.recvMsg();
//                     if (msg.equals(CommMgr.FP_START)) break;
//                 }
//                fpInstructions = "fpath" + fpInstructions + "z";
//            }
//            
//            CommMgr.getCommMgr().sendMsg(fpInstructions.toString(), CommMgr.INSTRUCTIONS);                
//            /////////////////////////
//            
//            
//            
//            
//        }
    }

    /**
     * Initialises the main map view by adding the different maps as cards in the CardLayout. Displays realMap
     * by default.
     */
    private static void initMainLayout() {
        if (!realRun) {
            _mapCards.add(realMap, "REAL_MAP");
        }
        _mapCards.add(exploredMap, "EXPLORATION");

        CardLayout cl = ((CardLayout) _mapCards.getLayout());
        if (!realRun) {
            cl.show(_mapCards, "REAL_MAP");
        } else {
            cl.show(_mapCards, "EXPLORATION");
        }
    }

    /**
     * Initialises the JPanel for the buttons.
     */
    private static void initButtonsLayout() {
        _buttons.setLayout(new GridLayout());
        addButtons();
    }

    /**
     * Helper method to set particular properties for all the JButtons.
     */
    private static void formatButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
    }

    /**
     * Initialises and adds the five main buttons. Also creates the relevant classes (for multithreading) and JDialogs
     * (for user input) for the different functions of the buttons.
     */
    private static void addButtons() {
        if (!realRun) {
            // Load Map Button
            JButton btn_LoadMap = new JButton("Load Map");
            formatButton(btn_LoadMap);
            btn_LoadMap.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    JDialog loadMapDialog = new JDialog(_appFrame, "Load Map", true);
                    loadMapDialog.setSize(400, 60);
                    loadMapDialog.setLayout(new FlowLayout());

                    final JTextField loadTF = new JTextField(15);
                    JButton loadMapButton = new JButton("Load");

                    loadMapButton.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            loadMapDialog.setVisible(false);
                            loadMapFromDisk(realMap, loadTF.getText());
                            CardLayout cl = ((CardLayout) _mapCards.getLayout());
                            cl.show(_mapCards, "REAL_MAP");
                            realMap.repaint();
                            System.out.println("Map printed!");
                        }
                    });

                    loadMapDialog.add(new JLabel("File Name: "));
                    loadMapDialog.add(loadTF);
                    loadMapDialog.add(loadMapButton);
                    loadMapDialog.setVisible(true);
                }
            });
            _buttons.add(btn_LoadMap);
        }

        // FastestPath Class for Multithreading
        class FastestPath extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                bot.setRobotPos(RobotConstants.START_ROW, RobotConstants.START_COL);
                exploredMap.repaint();
                
                FastestPathAlgo fastestPathToWayPoint;
                fastestPathToWayPoint = new FastestPathAlgo(exploredMap, bot);
                String fp1 = fastestPathToWayPoint.runFastestPath(fpRow,fpCol);
                bot.setRobotPos(fpRow,fpCol);

                System.out.println("B4 goal: "+bot.getRow()+ ", "+bot.getCol());
                FastestPathAlgo fastestPathToGoal;
                fastestPathToGoal = new FastestPathAlgo(exploredMap, bot);
                String fp2 = fastestPathToGoal.runFastestPath(RobotConstants.GOAL_ROW, RobotConstants.GOAL_COL);
                String fpInstructions="";
                if(Character.isUpperCase(fp1.charAt(fp1.length()-1)) && Character.isUpperCase(fp2.charAt(0))){
                    char c = (char)(fp1.charAt(fp1.length()-1)+fp2.charAt(0)-64);
                    fpInstructions = fpInstructions + fp1.substring(0,fp1.length()-1) + c + fp2.substring(1);
                }   
                else
                {
                    fpInstructions= fpInstructions+fp1+fp2;
                }
                System.out.println("Just finished finding fpInstructions:"+fpInstructions);
                if (realRun) {
                	System.out.println("GOING INTO THE while loop");
                     while (true) {
                         System.out.println("Waiting for FP_START...");
                         String msg = comm.recvMsg();
                         if (msg.equals(CommMgr.FP_START)) break;
                     }
                    fpInstructions = "fpath" + fpInstructions + "z";
                }
                //send fpinstructions to rpi
                CommMgr.getCommMgr().sendMsg(fpInstructions.toString(), CommMgr.INSTRUCTIONS);   
                
                ///////////////////////////////////////////////////////////////////////////////////////
                // bot (1,1)
                bot.setRobotPos(1,1);
                exploredMap.repaint();
                
                bot.setRobotDir(DIRECTION.UP);
                exploredMap.repaint();
                
                String movements = fp1 + fp2;
                
                for (int i=0;i<(movements.length());i++) {
                	
                	switch (movements.charAt(i)) {
                    	case 'f':
                    		switch (bot.getrobotDir()) {
	                            case UP:
		                            bot.setRobotPos(bot.getRow()+1,bot.getCol());
		                            break;
                             case RIGHT:
		                            bot.setRobotPos(bot.getRow(),bot.getCol()+1);
                                 break;
                             case DOWN:
		                            bot.setRobotPos(bot.getRow()-1,bot.getCol());
                                 break;
                             case LEFT:
		                            bot.setRobotPos(bot.getRow(),bot.getCol()-1);
                                 break;
                         }
	                         break;
	                    case 'r':
	                    	 bot.setRobotDir(DIRECTION.getPrevious(bot.getrobotDir()));
	                    	 break;
	                    case 'l':
	                    	 bot.setRobotDir(DIRECTION.getNext(bot.getrobotDir()));
	                         break;
	                    
	                    default:
	                         System.out.println("Error in Robot.move()!");
	                         break;
                 }
                	System.out.println("FP Move: " + movements.charAt(i));
                	
                	
                	
                	///////////////////////////
                	exploredMap.repaint();	
                }
//                /////////////////////////////////////////////////////////////////////////////////////
                
                return 222;
            }
        }

        // Exploration Class for Multithreading
        class Exploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                int row, col;

                row = RobotConstants.START_ROW;
                col = RobotConstants.START_COL;

                bot.setRobotPos(row, col);
                exploredMap.repaint();

                ExplorationAlgo exploration;
                exploration = new ExplorationAlgo(exploredMap, realMap, bot, coverageLimit, timeLimit);

//                if (realRun) {
//                    CommMgr.getCommMgr().sendMsg(null, CommMgr.BOT_START);
//                }

                exploration.runExploration();
            	System.out.println("Just finished exploration.runExploration(); WE ARE GONNA RUN generateMapDescriptor(exploredMap);");
                generateMapDescriptor(exploredMap);
                System.out.println("Just finished generateMapDescriptor(exploredMap);");
                
                // for(int i=0; i<20;i++){
                //     for(int j=0;j<15;j++){
                //         System.out.print(Visited.visitedArr[i][j]);
                //     }
                //     System.out.println();
                // }

                if (realRun) {
                	System.out.println("WE ARE GONNA RUN FastestPath().execute();");
                	DIRECTION direction = bot.getrobotDir();
                	if (direction == DIRECTION.LEFT) 
                    	bot.move(MOVEMENT.TURNR, true);
                		
                	else if (direction == DIRECTION.RIGHT)
                    	bot.move(MOVEMENT.TURNL, true);
                	else if (direction == DIRECTION.DOWN) 
                		{
                		bot.move(MOVEMENT.TURNR, true);
                    	bot.move(MOVEMENT.TURNR, true);
                    	}
                        		
                    new FastestPath().execute();
                }

                return 111;
            }
        }

        // Exploration Button
        JButton btn_Exploration = new JButton("Exploration");
        formatButton(btn_Exploration);
        btn_Exploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	
            	if (!realRun){
	            	JDialog speedExploDialog = new JDialog(_appFrame, "Set-Speed Exploration", true);
	            	speedExploDialog.setSize(400, 60);
	            	speedExploDialog.setLayout(new FlowLayout());
	                final JTextField speedTF = new JTextField(5);
	                JButton speedSaveButton = new JButton("Run");
	            	
	                speedSaveButton.addMouseListener(new MouseAdapter() {
	                    public void mousePressed(MouseEvent e) {
	                    	
		                    	speedExploDialog.setVisible(false);
		                        speedLimit = (int) (1000.0/(Integer.parseInt(speedTF.getText())));
		                        bot.setSpeed(speedLimit);
		                        System.out.println("Speed Limit:"+speedLimit);
	                    
	                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
	                        cl.show(_mapCards, "EXPLORATION");
	                        new Exploration().execute();
	                    }
	                });
	            	
	
	                speedExploDialog.add(new JLabel("Set Speed (grids per second): "));
	                speedExploDialog.add(speedTF);
	                speedExploDialog.add(speedSaveButton);
	                speedExploDialog.setVisible(true);
	            	
            	} else {
            		CardLayout cl = ((CardLayout) _mapCards.getLayout());
                    cl.show(_mapCards, "EXPLORATION");
                    new Exploration().execute();
            	}
            }
        });
        _buttons.add(btn_Exploration);

        
        
        
        
        
        
        
        
        
        // Fastest Path Button
        JButton btn_FastestPath = new JButton("Fastest Path");
        formatButton(btn_FastestPath);
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapCards.getLayout());
                cl.show(_mapCards, "EXPLORATION");
                new FastestPath().execute();
            }
        });
        _buttons.add(btn_FastestPath);


        // TimeExploration Class for Multithreading
        class TimeExploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                bot.setRobotPos(RobotConstants.START_ROW, RobotConstants.START_COL);
                exploredMap.repaint();

                ExplorationAlgo timeExplo = new ExplorationAlgo(exploredMap, realMap, bot, coverageLimit, timeLimit);
                timeExplo.runExploration();

                generateMapDescriptor(exploredMap);

                return 333;
            }
        }

        // Time-limited Exploration Button
        JButton btn_TimeExploration = new JButton("Time-Limited");
        formatButton(btn_TimeExploration);
        btn_TimeExploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog timeExploDialog = new JDialog(_appFrame, "Time-Limited Exploration", true);
                timeExploDialog.setSize(400, 60);
                timeExploDialog.setLayout(new FlowLayout());
                final JTextField timeTF = new JTextField(5);
                JButton timeSaveButton = new JButton("Run");

                timeSaveButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        timeExploDialog.setVisible(false);
                        String time = timeTF.getText();
                        String[] timeArr = time.split(":");
                        timeLimit = (Integer.parseInt(timeArr[0]) * 60) + Integer.parseInt(timeArr[1]);
                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
                        cl.show(_mapCards, "EXPLORATION");
                        new TimeExploration().execute();
                    }
                });

                timeExploDialog.add(new JLabel("Time Limit (in MM:SS): "));
                timeExploDialog.add(timeTF);
                timeExploDialog.add(timeSaveButton);
                timeExploDialog.setVisible(true);
            }
        });
        _buttons.add(btn_TimeExploration);


        // CoverageExploration Class for Multithreading
        class CoverageExploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                bot.setRobotPos(RobotConstants.START_ROW, RobotConstants.START_COL);
                exploredMap.repaint();

                ExplorationAlgo coverageExplo = new ExplorationAlgo(exploredMap, realMap, bot, coverageLimit, timeLimit);
                coverageExplo.runExploration();

                generateMapDescriptor(exploredMap);

                return 444;
            }
        }

        // Coverage-limited Exploration Button
        JButton btn_CoverageExploration = new JButton("Coverage-Limited");
        formatButton(btn_CoverageExploration);
        btn_CoverageExploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog coverageExploDialog = new JDialog(_appFrame, "Coverage-Limited Exploration", true);
                coverageExploDialog.setSize(400, 60);
                coverageExploDialog.setLayout(new FlowLayout());
                final JTextField coverageTF = new JTextField(5);
                JButton coverageSaveButton = new JButton("Run");

                coverageSaveButton.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        coverageExploDialog.setVisible(false);
                        coverageLimit = (int) ((Integer.parseInt(coverageTF.getText())) * MapConstants.MAP_SIZE / 100.0);
                        new CoverageExploration().execute();
                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
                        cl.show(_mapCards, "EXPLORATION");
                    }
                });

                coverageExploDialog.add(new JLabel("Coverage Limit (% of maze): "));
                coverageExploDialog.add(coverageTF);
                coverageExploDialog.add(coverageSaveButton);
                coverageExploDialog.setVisible(true);
            }
        });
        _buttons.add(btn_CoverageExploration);
    }
}