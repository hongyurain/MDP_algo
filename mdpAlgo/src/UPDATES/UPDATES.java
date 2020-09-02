package UPDATES; // TO BE DELETED AT THE END OF THE PROJECT

public class UPDATES {
	/**
	 * HI GUYS I KNOW THIS ISN'T A GOOD WAY TO KEEP YALL UPDATED ON WHAT I DO BUT IM LEGIT TOO LAZY TO MAKE A READ ME HAHA
	 * 
	 * KT 02/09/2020
	 * - Made a simulation(sort of) -> just run the main file! 
	 *      - Red = traveled block, Blue = explored, Gray = Virtual wall
	 *      - I set black for real walls too but since there isn't any, you won't see any black cells.
	 * - I made the simulated sensors such that it discovers the 4 neighboring cells (up down left right) of the robot cell.
	 *      - Unfortunately, I can't figure out a way to implement a 3x3 robot efficiently, so if yall have ideas pls feel free to add it in!
	 *      - If you guys do want to work on the 3x3 thing, please ensure the robot's main representation is at the middle front cell, and not the 
	 *        center. An example would be below, with H representing the 'heart'
	 *        
	 *        Facing up!           Facing Left!
	 *        (R) (H) (R)          (R) (R) (R)
	 *        (R) (R) (R)          (H) (R) (R)
	 *        (R) (R) (R)          (R) (R) (R)
	 *      - The reason why I kind of got stuck is because rotating the robot requires tinkering with the entire cell body in the Robot class, and 
	 *        apart from hard coding, I can't figure out an efficient way to do it.
	 * - What I think we should work on next is getting the 3x3, and the sensors for the simulation AND the actual robot. Also, we need to work on the
	 *   robot's separate map. 
	 * - IF Y'ALL NOTICED, THE TOP LEFT CELL ISN'T EXPLORED. That's because I havn't properly coded the sensors, so for now can just ignore. 
	 * 
	 */
}


