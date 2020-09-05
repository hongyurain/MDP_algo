package simulation;

import map.Map;
import robot.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class main {
	static Map map = new Map();
	static JFrame jframe;
	static JPanel simGrid;
	
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		

		// Robot's starting position = "B1"
		Robot wall_e = new Robot(1, 1); 
		
		System.out.println();
		
		displaySim();
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
			else {
				System.out.println("No more paths!");
				break;
			}
			
			System.out.println("--------------");
			Thread.sleep(50);
			simGrid.repaint();
		}	
		System.out.println("All explored!");
	}
	
	public static void displaySim() {
		jframe = new JFrame();
		jframe.setSize(1200,900);
		jframe.setTitle("Simulation");
		jframe.setResizable(false);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		jframe.setLocation(dim.width / 2 - jframe.getSize().width / 2, dim.height / 2 - jframe.getSize().height / 2);
		
        simGrid = new JPanel(new GridLayout(15,20));
        
        for (int row=0; row<15; row++) {
        	for (int col=0; col<20; col++) {
        		map.addButton(simGrid, row, col);
        	}
        }
        
		// Add card layout & grid to the main frame's content pane
        Container contentPane = jframe.getContentPane();
        contentPane.add(simGrid); 

        

        // Display the application
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	

}
