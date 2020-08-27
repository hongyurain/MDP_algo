package map;

public class Cell {
	private final int row;
	private final int col;
	private boolean isExplored;
	private boolean isVirtualWall;
	private boolean isObstacle;
	private String cellID;
	
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		this.isExplored = false;
		this.isVirtualWall = false;
		this.isObstacle = false;
		setID(row, col);
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
	
	public void updateExplored() {
		this.isExplored = true;
	}
	
	public boolean getIsExplored() {
		return this.isExplored;
	}
}
