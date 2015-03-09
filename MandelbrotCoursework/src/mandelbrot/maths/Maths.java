package mandelbrot.maths;

import java.awt.Point;

import mandelbrot.utilities.Pair;

public final class Maths
{

	private Maths()
	{
		// TODO Auto-generated constructor stub
	}


	public static double fastSquare(double number)
	{
		return number * number;
	}


	public static int fastSquare(int number)
	{
		return number * number;
	}


	public static Pair<Double, Double> calculateRealtoComplexRatio(int width, int height, Pair<Double, Double> xAxisComplex,
			Pair<Double, Double> yAxisComplex)
	{
		double xRatio = (xAxisComplex.getRight() - xAxisComplex.getLeft()) / (width);
		double yRatio = (yAxisComplex.getRight() - yAxisComplex.getLeft()) / (height);
		return new Pair<Double, Double>(xRatio, yRatio);
	}


	public static ComplexNumber convertCoordinateToComplexPlane(Point coordinate, Pair<Double, Double> conversionRatio, int width, int height, Pair<Double, Double> xAxisComplex, Pair<Double, Double> yAxisComplex)
	{
		double x = coordinate.getX() * conversionRatio.getLeft() + xAxisComplex.getLeft();
		double y = coordinate.getY() * conversionRatio.getRight() + yAxisComplex.getLeft();
		return new ComplexNumber(x, y);
	}

}
