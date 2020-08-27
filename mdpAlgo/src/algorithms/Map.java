package algorithms;
import java.lang.*;

public class Map {
	
	private int numRows= 15; //CONSTANT
	private int numCols = 20; //CONSTANT
	public static Cell[][] grid = new Cell[15][20];
	
	public Map() {
		for (int i=0; i<15; i++) {
			for (int j=0; j<20; j++) {
				grid[i][j] = new Cell(i, j);
			}
		}
		// Initialize virtual walls for the outer border
		for (int i=0; i<15; i++) {
			grid[i][0].updateVirtual(true);
			grid[i][this.numCols-1].updateVirtual(true);
		}
		for (int j=0; j<20; j++) {
			grid[0][j].updateVirtual(true);
			grid[this.numRows-1][j].updateVirtual(true);
		}
	}
	
	public void printGrid() {
		for (int i=0; i<15; i++) {
			for (int j=0; j<20; j++) {
				System.out.print(this.grid[i][j].getCellID() +" ");
			}
			System.out.println();
		}
	}
	
	public void printVirtual() {
		for (int i=0; i<15; i++) {
			for (int j=0; j<20; j++) {
				if (grid[i][j].getIsVirtualWall()) {
					System.out.print(this.grid[i][j].getCellID() + " ");
				}
			}
		}
	}
	
	public boolean checkFullyExplored() {
		for (int i=0; i<15; i++) {
			for (int j=0; j<20; j++) {
				if (!grid[i][j].getIsExplored()) {
					return false;
				}
			}
		}
		return true;
	}
}

//A0 A1 A2 A3 A4 A5 A6 A7 A8 A9 A10 A11 A12 A13 A14 A15 A16 A17 A18 A19 
//B0 B1 B2 B3 B4 B5 B6 B7 B8 B9 B10 B11 B12 B13 B14 B15 B16 B17 B18 B19 
//C0 C1 C2 C3 C4 C5 C6 C7 C8 C9 C10 C11 C12 C13 C14 C15 C16 C17 C18 C19 
//D0 D1 D2 D3 D4 D5 D6 D7 D8 D9 D10 D11 D12 D13 D14 D15 D16 D17 D18 D19 
//E0 E1 E2 E3 E4 E5 E6 E7 E8 E9 E10 E11 E12 E13 E14 E15 E16 E17 E18 E19 
//F0 F1 F2 F3 F4 F5 F6 F7 F8 F9 F10 F11 F12 F13 F14 F15 F16 F17 F18 F19 
//G0 G1 G2 G3 G4 G5 G6 G7 G8 G9 G10 G11 G12 G13 G14 G15 G16 G17 G18 G19 
//H0 H1 H2 H3 H4 H5 H6 H7 H8 H9 H10 H11 H12 H13 H14 H15 H16 H17 H18 H19 
//I0 I1 I2 I3 I4 I5 I6 I7 I8 I9 I10 I11 I12 I13 I14 I15 I16 I17 I18 I19 
//J0 J1 J2 J3 J4 J5 J6 J7 J8 J9 J10 J11 J12 J13 J14 J15 J16 J17 J18 J19 
//K0 K1 K2 K3 K4 K5 K6 K7 K8 K9 K10 K11 K12 K13 K14 K15 K16 K17 K18 K19 
//L0 L1 L2 L3 L4 L5 L6 L7 L8 L9 L10 L11 L12 L13 L14 L15 L16 L17 L18 L19 
//M0 M1 M2 M3 M4 M5 M6 M7 M8 M9 M10 M11 M12 M13 M14 M15 M16 M17 M18 M19 
//N0 N1 N2 N3 N4 N5 N6 N7 N8 N9 N10 N11 N12 N13 N14 N15 N16 N17 N18 N19 
//O0 O1 O2 O3 O4 O5 O6 O7 O8 O9 O10 O11 O12 O13 O14 O15 O16 O17 O18 O19 