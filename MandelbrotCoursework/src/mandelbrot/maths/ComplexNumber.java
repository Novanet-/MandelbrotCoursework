package mandelbrot.maths;

public class ComplexNumber
{

	private double real;
	private double imaginary;
	private final double isqrd = -1; // i^2 = -1


	/**
	 * Constructor for a complex number object z, where z = x + yi, x is the real part of the number and y is the
	 * imaginary part and i = sqrt(-1)
	 * 
	 * @param real
	 *            The real part of the number
	 * @param imaginary
	 *            The imaginary part of the number
	 */
	public ComplexNumber(double real, double imaginary)
	{
		super();
		setReal(real);
		setImaginary(imaginary);
	}


	/**
	 * Squares the complex number z, where z = x + yi z^2= (x+yi)(x+yi) = xx + xyi + yxi + yyi^2
	 * 
	 * mod(z) = sqrt(x^2 + y^2) mod(z)^2 = x^2 + y^2
	 * 
	 * @return ComplexNumber The square of the complex number
	 */
	public ComplexNumber square()
	{
		double real;
		double imaginary;

		// (a+bi)(c+di) = ac + adi + bci + bdi^2
		real = Maths.fastSquare(this.getReal()) + (isqrd * (Maths.fastSquare(this.getImaginary()))); // real part =
																										// xx + yy(i^2)
		imaginary = (this.getReal() * this.getImaginary()) + (this.getImaginary() * this.getReal()); // imaginary part =
																										// xy(i) + yx(i)
		return new ComplexNumber(real, imaginary);
	}


	/**
	 * Returns the square of the modulus of z, where z is the complex number in question z= x + yi
	 * 
	 * mod(z) = sqrt(x^2 + y^2) mod(z)^2 = x^2 + y^2
	 * 
	 * @return double The modulus of the complex number, squared
	 */
	public double modulusSquared()
	{
		return (Maths.fastSquare(getReal())) + (Maths.fastSquare(getImaginary()));
	}


	/**
	 * Returns the square of the modulus of z, where z is the complex number in question z= x + yi
	 * 
	 * mod(z) = sqrt(x^2 + y^2) mod(z)^2 = x^2 + y^2
	 * 
	 * @return double The modulus of the complex number, squared
	 */
	public ComplexNumber add(ComplexNumber d) // (a+bi) + (c+di) = (a+c) + (b+d)i
	{
		double real;
		double imaginary;

		real = this.getReal() + d.getReal();
		imaginary = this.getImaginary() + d.getImaginary();

		return new ComplexNumber(real, imaginary);
	}


	public double getReal()
	{
		return real;
	}


	public void setReal(double real)
	{
		this.real = real;
	}


	public double getImaginary()
	{
		return imaginary;
	}


	public void setImaginary(double imaginary)
	{
		this.imaginary = imaginary;
	}

}
