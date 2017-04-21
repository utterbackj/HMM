//By Joshua Utterback
//Methods for calculating states using a hidden Markov model (and matrix multiplication)

import java.util.ArrayList;

public class MarkovProblem {
	
	public ArrayList<double[]> forward;
	public ArrayList<double[]> backward;
	public ArrayList<double[]> smoothed;
	public double[][] transition;
	public double[][] transitionT;
	
	public void initialize(double[] initalState) {
		forward = new ArrayList<>();
		forward.add(initalState);
		backward = new ArrayList<>();
		backward.add(initalState);
		smoothed = new ArrayList<>();
	}
	
	public void calculateNextState(double[][] o) {
		double[] newState = multiplyVectorByMatrix(multiplyMatrix(o, transitionT), forward.get(forward.size()-1));
		normalizeVector(newState);
		forward.add(newState);
	}
	
	public void calculateBackState(double[][] o) {
		double[] newState = multiplyVectorByMatrix(multiplyMatrix(transition, o), backward.get(0));
		normalizeVector(newState);
		backward.add(0, newState);
	}
	
	public void calculateSmoothed() {
		for (int i = 0; i < forward.size(); i++) {
			double[] newState = vectorProduct(forward.get(i), backward.get(i));
			normalizeVector(newState);
			smoothed.add(newState);
		}
	}
	
	public void normalizeVector(double[] vector) {
		double sum = 0;
		for (int i = 0; i < vector.length; i++) {
			sum += vector[i];
		}
		if (sum == 0) {
			System.err.println("Zero vector result; impossible evidence given");
			return;
		}
		for (int i = 0; i < vector.length; i++) {
			vector[i] /= sum;
		}
	}
	
	private double[][] multiplyMatrix(double[][] a, double[][] b) {
		double[][] product = new double[a.length][b[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b[0].length; j++) {
				for (int k = 0; k < a[0].length; k++) {
					product[i][j] += a[i][k] * b[k][j]; //ith row of a . jth col of b
				}
			}
		}
		return product;
	}
	
	private double[] multiplyVectorByMatrix(double[][] a, double[] v) {
		double[] product = new double[v.length];
		for (int i = 0; i < a.length; i++) {
			for (int k = 0; k < v.length; k++) {
				product[i] += a[i][k] * v[k]; //ith row of a . v
			}
		}
		return product;
	}
	
	private double[] vectorProduct(double[] a, double[] b) {
		double[] product = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			product[i] = a[i] * b[i];
		}
		return product;
	}
}
