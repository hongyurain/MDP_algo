package algorithms;


public class Robot {
	
	private String curPos;
	private String curDir;
	private Map map = new Map();
	
	public Robot(String position, String direction) {
		this.curPos = position;
		this.curDir = direction;
	}
	
	public void moveForward() {
		System.out.println("Moving forward!");
		
		int[] index = map.getIndex(this.curPos);
		switch(this.curDir) {
		case "up": this.curPos = map.grid[index[0]-1][index[1]]; break;
		case "right": this.curPos = map.grid[index[0]][index[1]+1]; break;
		case "down": this.curPos = map.grid[index[0]+1][index[1]]; break;
		case "left": this.curPos = map.grid[index[0]][index[1]-1]; break;	
		}
	}
	
	public void rotateRight() {
		System.out.println("Rotating right!");
		switch(this.curDir) {
		case "up": this.curDir = "right"; break;
		case "right": this.curDir = "down"; break;
		case "down": this.curDir = "left"; break;
		case "left": this.curDir = "up"; break;
		}
		System.out.println("New direction: " + this.curDir);
	}
	
	public String getPos() {
		return this.curPos;
	}
	
	public String getDir() {
		return this.curDir;
	}
	
}
