package mandlebrot.gui;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mandelbrot.maths.ComplexNumber;
import mandelbrot.utilities.Pair;

public class GUI extends JFrame
{

	private static Rectangle screenBounds = GUI.getScreenResolution();
	final static int DEFAULT_FRAME_WIDTH = (int) (screenBounds.getWidth() * 0.95);
	final static int DEFAULT_FRAME_HEIGHT = (int) (screenBounds.getHeight() * 0.75);
	final static Pair<Double, Double> DEFAULT_X_AXIS_COMPLEX = new Pair<Double, Double>(-2.0, 2.0);
	final static Pair<Double, Double> DEFAULT_Y_AXIS_COMPLEX = new Pair<Double, Double>(-1.6, 1.6);
	final static int DEFAULT_ITERATIONS = 100;
	final int PAINT_TYPE = BufferedImage.TYPE_INT_ARGB;
	final static File IMAGE_DIRECTORY = new File(System.getProperty("user.dir") + "/images/");

	long lastDrawTime = 0;

	@SuppressWarnings("unused")
	private GUI frame;
	private JPanel pnlOuter;
	private JPanel pnlFractal;
	private MandelbrotPanel pnlMandelbrot;
	private JuliaPanel pnlJulia;
	private InfoPanel pnlInfo;

	private Pair<Double, Double> xAxisComplex;
	private Pair<Double, Double> yAxisComplex;
	private int iterations;
	ComplexNumber complexCoordinate;

	boolean favouriteSelected = false;

	private MandelbrotThread mandelbrotThread;
	private JuliaThread juliaThread;

	private boolean mandelbrotNeedsRecalculate;
	private boolean juliaNeedsRecalculate;

	private static final long serialVersionUID = -9167797785983558030L;


	public GUI()
	{
		super();
		juliaThread = new JuliaThread(this);
		mandelbrotThread = new MandelbrotThread(this);
		setJuliaNeedsRecalculate(false);
	}


	public static void main(String[] args)
	{
		GUI frame = new GUI();
		frame.mandelbrotThread.start();
		frame.juliaThread.start();
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT));

		frame.setxAxisComplex(DEFAULT_X_AXIS_COMPLEX);
		frame.setyAxisComplex(DEFAULT_Y_AXIS_COMPLEX);
		frame.setIterations(DEFAULT_ITERATIONS);
		frame.setResizable(false);

		frame.init();
	}


	/**
	 * Initialises the various components contained in the frame by calling the child components initialisation methods
	 */
	public void init()
	{
		setTitle("Mandelbrot Visualiser");

		setPnlOuter(new JPanel());
		setPnlFractal(new JPanel());
		getPnlFractal().setLayout(new BoxLayout(getPnlFractal(), BoxLayout.LINE_AXIS));
		setPnlMandelbrot(new MandelbrotPanel(this));
		setPnlJulia(new JuliaPanel(this));
		setPnlInfo(new InfoPanel(this));

		getPnlFractal().setSize(new Dimension(DEFAULT_FRAME_WIDTH, (int) (DEFAULT_FRAME_HEIGHT * 0.875)));

		getPnlOuter().setPreferredSize(new Dimension(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT));
		setContentPane(getPnlOuter());
		getPnlOuter().setLayout(new BoxLayout(getPnlOuter(), BoxLayout.PAGE_AXIS));

		getPnlOuter().add(getPnlFractal());
		getPnlMandelbrot().init();
		getPnlJulia().init();
		getPnlInfo().init();

		setSize(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}


	/**
	 * Fetches the screen resolution of the primary monitor
	 * 
	 * @return virtualBounds A rectangle with bounds matching that of the primary monitor the program is running in
	 */
	public static Rectangle getScreenResolution()
	{
		Rectangle virtualBounds = new Rectangle();
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		virtualBounds.setBounds(0, 0, gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
		return virtualBounds;
	}


	public int getIterations()
	{
		return iterations;
	}


	public ComplexNumber getComplexCoordinate()
	{
		return complexCoordinate;
	}


	public void setIterations(int iterations)
	{
		this.iterations = iterations;
	}


	void setComplexCoordinate(ComplexNumber complexCoordinate)
	{
		this.complexCoordinate = complexCoordinate;
	}


	synchronized boolean isJuliaNeedsRecalculate()
	{
		return juliaNeedsRecalculate;
	}


	public synchronized void setJuliaNeedsRecalculate(boolean juliaNeedsRecalculate)
	{
		this.juliaNeedsRecalculate = juliaNeedsRecalculate;
	}


	
	synchronized boolean isMandelbrotNeedsRecalculate()
	{
		return mandelbrotNeedsRecalculate;
	}


	
	public synchronized void setMandelbrotNeedsRecalculate(boolean mandelbrotNeedsRecalculate)
	{
		this.mandelbrotNeedsRecalculate = mandelbrotNeedsRecalculate;
	}


	JPanel getPnlOuter()
	{
		return pnlOuter;
	}


	private void setPnlOuter(JPanel pnlOuter)
	{
		this.pnlOuter = pnlOuter;
	}


	JPanel getPnlFractal()
	{
		return pnlFractal;
	}


	private void setPnlFractal(JPanel pnlFractal)
	{
		this.pnlFractal = pnlFractal;
	}


	MandelbrotPanel getPnlMandelbrot()
	{
		return pnlMandelbrot;
	}


	private void setPnlMandelbrot(MandelbrotPanel pnlMandelbrot)
	{
		this.pnlMandelbrot = pnlMandelbrot;
	}


	JuliaPanel getPnlJulia()
	{
		return pnlJulia;
	}


	private void setPnlJulia(JuliaPanel pnlJulia)
	{
		this.pnlJulia = pnlJulia;
	}


	InfoPanel getPnlInfo()
	{
		return pnlInfo;
	}


	private void setPnlInfo(InfoPanel pnlInfo)
	{
		this.pnlInfo = pnlInfo;
	}


	int getPAINT_TYPE()
	{
		return PAINT_TYPE;
	}


	public Pair<Double, Double> getxAxisComplex()
	{
		return xAxisComplex;
	}


	public void setxAxisComplex(Pair<Double, Double> xAxisComplex)
	{
		this.xAxisComplex = xAxisComplex;
	}


	public Pair<Double, Double> getyAxisComplex()
	{
		return yAxisComplex;
	}


	public void setyAxisComplex(Pair<Double, Double> yAxisComplex)
	{
		this.yAxisComplex = yAxisComplex;
	}

}