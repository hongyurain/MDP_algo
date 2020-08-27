package algorithms;


public class Robot {
	private Cell cell;
	private enum Direction {
			LEFT, UP, RIGHT, DOWN
	}
	private Direction curDir = Direction.RIGHT;
	private boolean leftClear;
	private boolean rightClear;
	private boolean frontClear;
	
	
	public Robot(int row, int col) {
		this.cell = new Cell(row, col);
		updateSensors();
	}
	
	public String getCurCellID() {
		return this.cell.getCellID();
	}
	
	public Cell getCurCell() {
		return this.cell;
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
	
	public void moveForward() {
		int row = this.cell.getRow();
		int col = this.cell.getCol();
		
		switch(this.curDir) {
		case LEFT:
			this.cell = new Cell(row-1, col);
		case UP:
			this.cell = new Cell(row, col-1);
		case RIGHT:
			this.cell = new Cell(row+1, col);
		case DOWN:
			this.cell = new Cell(row, col+1);
		}
		//updateSensors()
	}
	
	public void rotateRight() {
		switch(this.curDir) {
		case LEFT:
			this.curDir = Direction.UP;
		case UP:
			this.curDir = Direction.RIGHT;
		case RIGHT:
			this.curDir = Direction.DOWN;
		case DOWN:
			this.curDir = Direction.LEFT;
		}
	}
	
	public void rotateLeft() {
		switch(this.curDir) {
		case LEFT:
			this.curDir = Direction.DOWN;
		case UP:
			this.curDir = Direction.LEFT;
		case RIGHT:
			this.curDir = Direction.UP;
		case DOWN:
			this.curDir = Direction.RIGHT;
		}
	}
	
	public void updateSensors() {
		//add code to update the boolean sensors every time this function is called
		//this function will be called every time the bot moves e.g. moveForward()
		//----------
		
		
		//----------
	}
	
	
}
