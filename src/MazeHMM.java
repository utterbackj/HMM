//By Joshua Utterback
//Implementation of the Maze Location problem in terms of a Markov model

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class MazeHMM extends MarkovProblem {
	
	private static String FILE_PATH = "/Users/Joshua/Documents/CS76/HMM/DefaultTestMaze.txt";
	
	public ArrayList<ArrayList<Integer>> maze;
	private int numColors;
	private double sensorCorrectRate;
	
	public MazeHMM (String filePath, int numColors, double sensorCorrectRate) {
		maze = createMaze(filePath);
		this.numColors = numColors;
		this.sensorCorrectRate = sensorCorrectRate;
		double[] initialState = calculateInitialState();
		calculateTransMatrix();
		initialize(initialState);
	}
	
	private double[] calculateInitialState() {
		double[] initial = new double[maze.size() * maze.get(0).size()];
		double emptySpaces = 0;
		for (int i = 0; i < maze.size(); i++) {
			for (int j = 0; j < maze.get(i).size(); j++) {
				if (maze.get(i).get(j) != -1) {
					emptySpaces++;
				}
			}
		}
		for (int i = 0; i < maze.size(); i++) {
			for (int j = 0; j < maze.get(i).size(); j++) {
				if (maze.get(i).get(j) != -1) {
					initial[i * maze.get(i).size() + j] = 1 / emptySpaces;
				} else {
					initial[i * maze.get(i).size() + j] = 0;
				}
			}
		}
		return initial;
	}
	
	private void calculateTransMatrix() {
		double[][] trans = new double[maze.size() * maze.get(0).size()][maze.size() * maze.get(0).size()];
		double[][] transT = new double[trans.length][maze.size() * maze.get(0).size()];
		for (int i = 0; i < maze.size(); i++) {
			for (int j = 0; j < maze.get(i).size(); j++) {
				int index = i * maze.get(i).size() + j;
				double stay = 0;
				//For each direction
				if (i > 0 && maze.get(i-1).get(j) != -1) {
					transT[(i-1) * maze.get(i).size() + j][index] = 0.25d;
					trans[index][(i-1) * maze.get(i).size() + j] = 0.25d;
				} else {
					stay++;
				}
				if (j > 0 && maze.get(i).get(j-1) != -1) {
					transT[i * maze.get(i).size() + j-1][index] = 0.25d;
					trans[index][i * maze.get(i).size() + j-1] = 0.25d;
				} else {
					stay++;
				}
				if (i < (maze.size()-1) && maze.get(i+1).get(j) != -1) {
					transT[(i+1) * maze.get(i).size() + j][index] = 0.25d;
					trans[index][(i+1) * maze.get(i).size() + j] = 0.25d;
				} else {
					stay++;
				}
				if (j < (maze.get(i).size()-1) && maze.get(i).get(j+1) != -1) {
					transT[i * maze.get(i).size() + j+1][index] = 0.25d;
					trans[index][i * maze.get(i).size() + j+1] = 0.25d;
				} else {
					stay++;
				}
				//For self
				trans[index][index] = stay / 4d;
				transT[index][index] = stay / 4d;
			}
		}
		transition = trans;
		transitionT = transT;
	}
	
	//Smooth enabled uses forward-backward algorithm
	public void setEvidence(ArrayList<Integer> evidence, boolean smooth) {
		for (int e = 0; e < evidence.size(); e++) {
			calculateNextState(calculateO(evidence.get(e)));
		}
		if (smooth) {
			for (int b = evidence.size() - 1; b >= 0; b--) {
				calculateBackState(calculateO(evidence.get(b)));
			}
			calculateSmoothed();
		}
	}
	
	private double[][] calculateO(int e) {
		double[][] o = new double[maze.size() * maze.get(0).size()][maze.size() * maze.get(0).size()];
		for (int i = 0; i < o.length; i++) {
			if (maze.get(i / maze.get(0).size()).get(i % maze.get(0).size()) == e) {
				o[i][i] = sensorCorrectRate;
			} else if (maze.get(i / maze.get(0).size()).get(i % maze.get(0).size()) != -1) {
				o[i][i] = (1 - sensorCorrectRate) / ((double) numColors - 1);
			} else {
				o[i][i] = 0;
			}
		}
		return o;
	}
	
	//Copied from Maze-world
	public ArrayList<ArrayList<Integer>> createMaze(String filePath) {
		ArrayList<ArrayList<Integer>> newMaze = new ArrayList<>();
		try {
			InputStream inputStream = new FileInputStream(new File(filePath));
			Reader reader = new InputStreamReader(inputStream);
			readMaze(reader, newMaze);
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			e.printStackTrace();
			System.exit(1);
		}
		return newMaze;
	}
	
	//From Maze-world, modified to fit colors
	private static void readMaze(Reader reader, ArrayList<ArrayList<Integer>> maze) {
		try {
			ArrayList<Integer> currentRow = new ArrayList<>();
			maze.add(currentRow);
			int i = reader.read();
			while (i != -1) {
				char c = (char) i;
				if (c == '#') {
					currentRow.add(-1);
				} else if (c == '\n') {
					currentRow = new ArrayList<>();
					maze.add(currentRow);
				} else if (c != 13) { //Don't want CR
					currentRow.add(c - 48);
				}
				i = reader.read();
			}
		} catch (IOException e) {
			System.err.println("File could not be read");
			e.printStackTrace();
			System.exit(1);
		}
		//Check maze formation
		if (maze.get(0) == null || maze.get(0).size() < 1) {
			System.err.println("No rows were created, check file");
			System.exit(1);
		}
		//Add walls to make maze rectangular - doesn't change fundamental structure
		int maxSize = 0;
		for (ArrayList<Integer> row : maze) {
			if (row.size() > maxSize) {
				maxSize = row.size();
			}
		}
		for (ArrayList<Integer> row : maze) {
			while (row.size() < maxSize) {
				row.add(-1);
			}
		}
	}
	
	public static void main(String[] args) {
		MazeHMM maze = new MazeHMM(FILE_PATH, 4, .88d);
		ArrayList<Integer> e = new ArrayList<>();
		e.add(0);
		e.add(1);
		e.add(2);
		e.add(3);
		e.add(2);
		e.add(3);
		e.add(2);
		e.add(1);
		e.add(0);
		e.add(2);
		e.add(3);
		e.add(2);
		e.add(0);
		maze.setEvidence(e, true);
		int timestep = maze.smoothed.size() - 1; //can change this value to see other timestep distributions
		for (int i = 0; i < maze.smoothed.get(0).length; i++) {
			System.out.println(i + ": " + maze.smoothed.get(timestep)[i] * 100 + "%");
		}
	}
}
