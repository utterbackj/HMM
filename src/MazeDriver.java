//By Joshua Utterback
//Driver for the Maze HMM Problem

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MazeDriver {
	
	private static String FILE_PATH = "./DefaultTestMaze.txt";
	private static double VISIBILITY_FACTOR = 1.75;
	private static boolean ENLARGE_TINY_CIRCLES = true;
	
	public int DRAW_SCALE = 50;
	
	private MazeHMM problem;
	private ArrayList<Color> colors;
	private int index;
	
	public void setProblem(MazeHMM hmmProblem, ArrayList<Color> colorList) {
		problem = hmmProblem;
		colors = colorList;
	}
	
	public void startDrawing() {
		index = 0;
		JFrame jFrame = new JFrame("Mobile Robot");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.getContentPane().add(new GraphicsPanel());
		jFrame.setLocationByPlatform(true);
		jFrame.setSize(750, 750);
		jFrame.setVisible(true);
		while (index < problem.smoothed.size() - 1) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			index++;
			jFrame.repaint();
		}
	}
	
	public class GraphicsPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		public void paintComponent(Graphics graphics) {
			drawFrame(graphics);
		}
		
		public void drawFrame(Graphics graphics) {
			Graphics2D g2d = (Graphics2D) graphics;
			for (int i = 0; i < problem.maze.size(); i++) {
				for (int j = 0; j < problem.maze.get(i).size(); j++) {
					if (problem.maze.get(i).get(j) == -1) {
						g2d.setColor(Color.black);
						g2d.fillRect(j * DRAW_SCALE, i * DRAW_SCALE, DRAW_SCALE, DRAW_SCALE);
					} else {
						g2d.setColor(colors.get(problem.maze.get(i).get(j)));
						double scale = VISIBILITY_FACTOR * 
								problem.smoothed.get(index)[i * problem.maze.get(i).size() + j];
						if (ENLARGE_TINY_CIRCLES) {
							scale = scale < 0.08 ? 0.08 : scale;
						}
						g2d.fillOval((int)(j * DRAW_SCALE + ((1 - scale) * DRAW_SCALE)/2),
								(int) (i * DRAW_SCALE + ((1 - scale) * DRAW_SCALE)/2),
								(int)(scale * DRAW_SCALE),(int) (scale * DRAW_SCALE));
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		MazeDriver mazeDriver = new MazeDriver();
		
		MazeHMM maze = new MazeHMM(FILE_PATH, 4, .88d);
		ArrayList<Integer> e = new ArrayList<>();
		e.add(0); //This is intended to be the square (3, 1) = 19
		e.add(1);
		e.add(2);
		e.add(2); //error input
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
		
		ArrayList<Color> colors = new ArrayList<>();
		colors.add(new Color(255, 0, 0)); // 0 = red
		colors.add(new Color(0, 255, 0)); // 1 = green
		colors.add(new Color(0, 0, 255)); // 2 = blue
		colors.add(new Color(255, 255, 0));//3 = yellow
		
		mazeDriver.setProblem(maze, colors);
		mazeDriver.startDrawing();
	}
}
