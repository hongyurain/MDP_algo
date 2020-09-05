package map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Cell {
	private final int row;
	private final int col;
	private boolean isExplored;
	private boolean isVirtualWall;
	private boolean isObstacle;
	private boolean isTraveled;
	private String cellID;
	private boolean isRobotHead;
	
	private JButton button;
	
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		this.isExplored = false;
		this.isVirtualWall = false;
		this.isObstacle = false;
		setID(row, col);
		
		this.button = new JButton();
	}
	
	private void setID(int row, int col) {
		char letter = (char) (row + 65); 
		String no = Integer.toString(col);
		this.cellID = letter + no;
	}
	
	public String getCellID() {
		return this.cellID;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}
	
	public void updateObstacle(boolean val) {
		this.isObstacle = val;
		this.button.setBackground(Color.BLACK);
	}
	
	public boolean getIsObstacle() {
		return this.isObstacle;
	}
	
	public void updateVirtual(boolean val) {
		if (val) {
			this.isVirtualWall = true;
		}
		else {
			if (row != 0 && col != 0) {
				this.isVirtualWall = false;
			}
		}
	}
	
	public boolean getIsVirtualWall() {
		return this.isVirtualWall;
	}
	
	public void updateTraveled() {
		if (!this.isObstacle || this.isVirtualWall)
		this.isTraveled = true;
		this.button.setBackground(Color.red);
	}
	
	public boolean getIsTraveled() {
		return this.isTraveled;
	}
	
	public void updateExplored() {
		this.isExplored = true;
		if (!this.isObstacle && !this.isVirtualWall && !this.isTraveled) {
			this.button.setBackground(Color.BLUE);
		}
		else if (this.isVirtualWall) {
			this.button.setBackground(Color.DARK_GRAY);
		}
	}
	
	public boolean getIsExplored() {
		return this.isExplored;
	}
	
	public JButton getButton() {
		return this.button;
	}
	
	public void setRobotHead(boolean val) {
		this.isRobotHead = val;
	}
}
