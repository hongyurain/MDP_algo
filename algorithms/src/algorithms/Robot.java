package algorithms;


public class Robot {
	private Cell cell;
	private enum Direction {
			LEFT, UP, RIGHT, DOWN
	}
	private Direction curDir = Direction.RIGHT;
	
	public Robot(int row, int col) {
		this.cell = new Cell(row, col);
	}
	
	public String getCurCellID() {
		return this.cell.getCellID();
	}
	
	public Cell getCurCell() {
		return this.cell;
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
	
	
	
}
