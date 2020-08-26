package algorithms;
import java.util.concurrent.TimeUnit;

public class main {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Robot wall_e = new Robot("A13", "right"); 
		Map map = new Map();
		String destination = "O17";
		
		map.printGrid();
		
		//map.addWalls("A3");
		//map.printWalls();
		System.out.println("Current position: " + wall_e.getPos());
		while (!wall_e.getPos().equals(destination)) {
			System.out.println("-------------------------");
			try { wall_e.moveForward(); }
			catch(Exception e) { wall_e.rotateRight(); }
			System.out.println("Current position: " + wall_e.getPos());
			TimeUnit.SECONDS.sleep(1);
		}
		
		System.out.println("Target reached!");
		
	}

}
