package mandlebrot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import mandelbrot.maths.ComplexNumber;
import mandelbrot.maths.Maths;
import utilities.Pair;
import circularRing.CircularArrayRing;

public class GUI extends JFrame
{

	private static Rectangle screenBounds = GUI.getScreenResolution();
	private final static int DEFAULT_FRAME_WIDTH = (int) (screenBounds.getWidth() * 0.95);
	private final static int DEFAULT_FRAME_HEIGHT = (int) (screenBounds.getHeight() * 0.75);
	private final static Pair<Double, Double> DEFAULT_X_AXIS_COMPLEX = new Pair<Double, Double>(-2.0, 2.0);
	private final static Pair<Double, Double> DEFAULT_Y_AXIS_COMPLEX = new Pair<Double, Double>(-1.6, 1.6);
	private final static int DEFAULT_ITERATIONS = 100;
	private final int PAINT_TYPE = BufferedImage.TYPE_INT_ARGB;
	private final static File IMAGE_DIRECTORY = new File(System.getProperty("user.dir") + "/images/");

	private long lastDrawTime = 0;

	private GUI frame;
	private JPanel pnlOuter;
	private JPanel pnlFractal;
	private MandelbrotPanel pnlMandelbrot;
	private JuliaPanel pnlJulia;
	private InfoPanel pnlInfo;

	private Pair<Double, Double> xAxisComplex;


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


	private Pair<Double, Double> yAxisComplex;
	private int iterations;
	private ComplexNumber complexCoordinate;

	private boolean favouriteSelected = false;

	// private MandelBrotThread mandelbrotThread;
	private JuliaThread juliaThread;

	private boolean juliaNeedsRecalculate;

	private static final long serialVersionUID = -9167797785983558030L;


	public GUI()
	{
		super();
		juliaThread = new JuliaThread();
		setJuliaNeedsRecalculate(false);
	}


	public static void main(String[] args)
	{
		GUI frame = new GUI();
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
		setPnlMandelbrot(new MandelbrotPanel());
		setPnlJulia(new JuliaPanel());
		setPnlInfo(new InfoPanel());

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


	private void setComplexCoordinate(ComplexNumber complexCoordinate)
	{
		this.complexCoordinate = complexCoordinate;
	}


	private synchronized boolean isJuliaNeedsRecalculate()
	{
		return juliaNeedsRecalculate;
	}


	public synchronized void setJuliaNeedsRecalculate(boolean juliaNeedsRecalculate)
	{
		this.juliaNeedsRecalculate = juliaNeedsRecalculate;
	}


	private JPanel getPnlOuter()
	{
		return pnlOuter;
	}


	private void setPnlOuter(JPanel pnlOuter)
	{
		this.pnlOuter = pnlOuter;
	}


	private JPanel getPnlFractal()
	{
		return pnlFractal;
	}


	private void setPnlFractal(JPanel pnlFractal)
	{
		this.pnlFractal = pnlFractal;
	}


	private MandelbrotPanel getPnlMandelbrot()
	{
		return pnlMandelbrot;
	}


	private void setPnlMandelbrot(MandelbrotPanel pnlMandelbrot)
	{
		this.pnlMandelbrot = pnlMandelbrot;
	}


	private JuliaPanel getPnlJulia()
	{
		return pnlJulia;
	}


	private void setPnlJulia(JuliaPanel pnlJulia)
	{
		this.pnlJulia = pnlJulia;
	}


	private InfoPanel getPnlInfo()
	{
		return pnlInfo;
	}


	private void setPnlInfo(InfoPanel pnlInfo)
	{
		this.pnlInfo = pnlInfo;
	}


	private int getPAINT_TYPE()
	{
		return PAINT_TYPE;
	}


	class MandelbrotPanel extends JPanel implements MouseListener, ComponentListener, MouseMotionListener, KeyListener
	{

		private CircularArrayRing<Point> cursorLocationRing;
		private Point pressLocation;
		private Point releaseLocation;
		private BufferedImage mandelbrotImage;
		private Pair<Double, Double> conversionRatio;
		private Rectangle selection = null;

		int paintType;

		private static final long serialVersionUID = 1900295689838487856L;


		/**
		 * Sets the paint type of the mandelbrot image and adds various Event Listeners to the panel
		 */
		public MandelbrotPanel()
		{
			super();
			paintType = BufferedImage.TYPE_INT_ARGB;
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.addKeyListener(this);
		}


		/**
		 * Sets the preferred size of the panel and calculates the conversion ratio from it's resoultion to the complex
		 * plane
		 */
		public void init()
		{
			cursorLocationRing = new CircularArrayRing<Point>();
			getPnlMandelbrot().setBackground(Color.GRAY);
			getPnlMandelbrot().setPreferredSize(new Dimension((int) (getPnlFractal().getWidth() * (0.6)), (int) (getPnlFractal().getHeight())));
			setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));
			getPnlFractal().add(getPnlMandelbrot());
			setMandelbrotImage(new BufferedImage((int) (getPnlMandelbrot().getPreferredSize().getWidth()), (int) getPnlMandelbrot().getPreferredSize().getHeight(), paintType));
		}


		/**
		 * Draws a selection rectangle if the mouse is being dragged, otherwise calculates and draws the mandelbrot set
		 * 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			super.paintComponent(g2);

			setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));
			if (selection == null)
			{
				setMandelbrotImage(new BufferedImage((int) (getPnlMandelbrot().getPreferredSize().getWidth()), (int) getPnlMandelbrot().getPreferredSize().getHeight(), paintType));
				paintMandelbrotSet();
				g2.drawImage(getMandelbrotImage(), 0, 0, null);
			}
			else
			{
				g2.drawImage(getMandelbrotImage(), 0, 0, null);
				g2.draw(selection);
			}
			getPnlMandelbrot().requestFocusInWindow();

		}


		/**
		 * Iterates through all the pixels of the panel, converting each one to a complex coordinate and using that to
		 * generate a colour which will make up part of the mandelbrot image
		 */
		public void paintMandelbrotSet()
		{
			int width = getWidth();
			int height = getHeight();
			Pair<Double, Double> xAxisComplex = getxAxisComplex();
			Pair<Double, Double> yAxisComplex = getyAxisComplex();
			Pair<Double, Double> conversionRatio = getConversionRatio();

			ComplexNumber complexCoordinate;

			for (int x = 0; x < width; x++)
			{
				for (int y = 0; y < height; y++)
				{
					complexCoordinate = Maths.convertCoordinateToComplexPlane(new Point(x, y), conversionRatio, width, height, xAxisComplex,
							yAxisComplex);
					getMandelbrotImage().setRGB(x, y, generateColor(complexCoordinate));
				}
			}

		}


		/**
		 * @param complexCoordinate
		 *            The complex version of the current pixel of the mandelbrot panel
		 * @return <b>color</b/> The calculated colour of the pixel at the passed coordinate of the mandelbrot image
		 */
		public int generateColor(ComplexNumber complexCoordinate)
		{
			// Sets the default colour to black, if the number does not diverge, the pixel will be black
			int color = Color.BLACK.getRGB();
			float nsmooth = 0;
			ComplexNumber z = complexCoordinate;
			for (int i = 0; i < getIterations(); i++)
			{
				z = z.square().add(complexCoordinate);
				if (z.modulusSquared() > 4)
				{
					// A function to decide the colour of the pixel, based on how many iterations it took for the
					// complex number to diverge, if at all
					// Generates a float between [0, maxIterations]
					nsmooth = (float) (i + 1 - Math.log(Math.log(z.modulusSquared())) / Math.log(2));

					// Converts the float to a number between [0,1]
					nsmooth = nsmooth / getIterations();

					// Uses the float value as part of the value for the hue of the pixel, and converts it to an RGB
					// representation of the colour
					color = Color.HSBtoRGB(0.65f + 5 * nsmooth, 0.6f, 1.0f);
					break;
				}
			}
			return color;
		}


		/**
		 * Prints the complex version of the coordinate clicked to a label in the info panel
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e)
		{
			getPnlMandelbrot().requestFocusInWindow();
			System.out.println("Click");

			getCursorLocationRing().add(new Point(e.getX(), e.getY()));
			setComplexCoordinate(Maths.convertCoordinateToComplexPlane(getCursorLocationRing().get(0), getConversionRatio(), getWidth(),
					getHeight(), getxAxisComplex(), getyAxisComplex()));
			DecimalFormat df = new DecimalFormat("#.##");

			String connector;

			if (getComplexCoordinate().getImaginary() < 0)
			{
				connector = " - ";
				getComplexCoordinate().setImaginary(Math.abs(getComplexCoordinate().getImaginary()));
			}
			else
				connector = " + ";
			getPnlInfo().getLblSelectedComplexPoint().setText(
					("Selected point: " + "z = " + df.format(getComplexCoordinate().getReal()) + connector
							+ df.format(getComplexCoordinate().getImaginary()) + "i"));
		}


		/**
		 * Starts the drawing of a selection rectangle, used for zooming in on the mandelbrot set
		 *
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				System.out.println("Press");
				pressLocation = new Point(e.getX(), e.getY());
				selection = new Rectangle(pressLocation);
			}
		}


		/**
		 * Gets the coordinates of the edges of the selection rectangle, and uses them to calculate new complex axes for
		 * zooming in on the mandelbrot set
		 * 
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e)
		{
			int width = getWidth();
			int height = getHeight();

			if (e.getButton() == MouseEvent.BUTTON1)
			{	
				System.out.println("Release");
				releaseLocation = new Point(e.getX(), e.getY());

				int xLower = (int) selection.getMinX();
				int xUpper = (int) selection.getMaxX();

				int yLower = (int) selection.getMinY();
				int yUpper = (int) selection.getMaxY();

				ComplexNumber lowerComplex = Maths.convertCoordinateToComplexPlane(new Point(xLower, yLower), getConversionRatio(), width,
						height, getxAxisComplex(), getyAxisComplex());
				ComplexNumber upperComplex = Maths.convertCoordinateToComplexPlane(new Point(xUpper, yUpper), getConversionRatio(), width,
						height, getxAxisComplex(), getyAxisComplex());

				setxAxisComplex(new Pair<Double, Double>(lowerComplex.getReal(), upperComplex.getReal()));
				setyAxisComplex(new Pair<Double, Double>(lowerComplex.getImaginary(), upperComplex.getImaginary()));

				selection = null;
				getPnlMandelbrot().repaint();
			}
		}


		@Override
		public void mouseEntered(MouseEvent e)
		{
		}


		@Override
		public void mouseExited(MouseEvent e)
		{
		}


		@Override
		public void componentHidden(ComponentEvent arg0)
		{
		}


		@Override
		public void componentMoved(ComponentEvent e)
		{
		}


		/**
		 * Recalculates the conversion ratio for converting from panel coordinates to complex coordinates, whenever the
		 * panel is resized
		 * 
		 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
		 */
		@Override
		public void componentResized(ComponentEvent e)
		{
			Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex());
		}


		@Override
		public void componentShown(ComponentEvent e)
		{
		}


		/**
		 * Draws the selection rectangle from where the mouse was first pressed, to the current position of the cursor
		 * 
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseDragged(MouseEvent e)
		{
			//if (e.getButton() == MouseEvent.BUTTON1)
			//{
				System.out.println("Drag");
				int x = (int) Math.min(pressLocation.x, e.getX());
				int y = (int) Math.min(pressLocation.y, e.getY());
				int width = (int) Math.abs(pressLocation.getX() - e.getX());
				int height = (int) Math.abs(pressLocation.getY() - e.getY());

				selection.setBounds(x, y, width, height);
				repaint();
			//}
		}


		/**
		 * When the mouse is moved within Notifies the julia set calculation thread
		 * 
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseMoved(MouseEvent e)
		{
//			if ((System.currentTimeMillis() / 1000) - (lastDrawTime / 1000) > 1)
//			{
				System.out.println("Move");
				getCursorLocationRing().add(new Point(e.getX(), e.getY()));
				setJuliaNeedsRecalculate(true);
				lastDrawTime = System.currentTimeMillis();
//			}
		}


		@Override
		public void keyPressed(KeyEvent e)
		{
			System.out.println("Print");
			if (e.getKeyCode() == KeyEvent.VK_P)
			{
				getPnlJulia().saveJuliaImage();
			}

		}


		@Override
		public void keyReleased(KeyEvent e)
		{

		}


		@Override
		public void keyTyped(KeyEvent e)
		{

		}


		public CircularArrayRing<Point> getCursorLocationRing()
		{
			return cursorLocationRing;
		}


		public void setCursorLocationRing(CircularArrayRing<Point> cursorLocationRing)
		{
			this.cursorLocationRing = cursorLocationRing;
		}


		Pair<Double, Double> getConversionRatio()
		{
			return conversionRatio;
		}


		void setConversionRatio(Pair<Double, Double> conversionRatio)
		{
			this.conversionRatio = conversionRatio;
		}


		BufferedImage getMandelbrotImage()
		{
			return mandelbrotImage;
		}


		void setMandelbrotImage(BufferedImage mandelbrotImage)
		{
			this.mandelbrotImage = mandelbrotImage;
		}

	}

	class JuliaPanel extends JPanel implements MouseListener, ComponentListener
	{

		private Point clickLocation;
		private BufferedImage juliaImage;
		private BufferedImage juliaImageRing; // A ring of the 10 most recent Julia images, the most
																	// recent is at index 0
		private Pair<Double, Double> conversionRatio;

		private static final long serialVersionUID = 1900295689838487856L;


		public JuliaPanel()
		{
			super();
			this.addMouseListener(this);
		}


		/**
		 * Sets the preferred size of the panel, and calculates the conversion ratio from the panel bounds to the
		 * complex bounds
		 */
		public void init()
		{
//			juliaImageRing = new CircularArrayRing<BufferedImage>();

			getPnlJulia().setBackground(Color.GRAY);
			getPnlJulia().setPreferredSize(new Dimension((int) (getPnlFractal().getWidth() * (0.4)), (int) (getPnlFractal().getHeight())));

			setJuliaImage(new BufferedImage((int) (getPnlFractal().getWidth() * (0.4)), getPnlFractal().getHeight(), getPAINT_TYPE()));
			setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));

			getPnlFractal().add(getPnlJulia());
		}


		/**
		 * If one of the saved Julia images is selected, then the selected image is drawn in the julia panel, otherwise
		 * it draws the most recent calculated julia image
		 * 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));

			if (favouriteSelected)
			{
				super.paintComponent(g2);
				g2.drawImage(getJuliaImage(), 0, 0, null);
				favouriteSelected = false;
			}
			else
			{
				if ((getComplexCoordinate() != null) && (getJuliaImageRing() != null))
				{
					super.paintComponent(g2);
										
					//Fetches and draws the most recently calculated Julia image
					g2.drawImage(getJuliaImageRing(), 0, 0, null);
					
					getPnlMandelbrot().requestFocusInWindow();
				}
			}

		}


		/**
		 * Iterates through each pixel in the Julia panel, converting each coordinate into a complex number and then
		 * determining the colour that pixel should be to draw a Julia set
		 */
		public BufferedImage paintJuliaSet()
		{
			int width = getWidth();
			int height = getHeight();
			BufferedImage tempImage;
			ComplexNumber iteratingCoordinate;

			tempImage = new BufferedImage(getWidth(), getHeight(), PAINT_TYPE);
			
			for (int x = 0; x < getPnlJulia().getWidth(); x++)
			{
				for (int y = 0; y < getPnlJulia().getHeight(); y++)
				{
					iteratingCoordinate = Maths.convertCoordinateToComplexPlane(new Point(x, y), getConversionRatio(), width, height,
							DEFAULT_X_AXIS_COMPLEX, DEFAULT_Y_AXIS_COMPLEX);

					tempImage.setRGB(x, y, generateColor(iteratingCoordinate));
				}
			}
			
			return tempImage;

		}


		/**
		 * Determines the colour for a given pixel in the julia set by adding a negative exponential number to a float
		 * value each time an iteration is run, until the complex number diverges
		 * 
		 * @param z
		 *            The current complex coordinate for drawing the Julia set
		 * @return <b>color</b> THE RGB value that the current pixel should be set to
		 */
		private int generateColor(ComplexNumber z)
		{
			double smoothColor;
			int color;
			int maxIterations = getIterations();
			color = Color.BLACK.getRGB();

			smoothColor = Math.exp(-(z.modulusSquared())); // e ^ -(abs(z))

			for (int i = 0; i < maxIterations; i++)
			{
				z = z.square().add(complexCoordinate);

				// Increments the colour value by this amount for each iteration until the number diverges
				smoothColor += Math.exp(-z.modulusSquared());

				if (z.modulusSquared() > 4)
				{
					// Changes the range of smoothColor from [0, maxIterations] to [0, 1]
					smoothColor = smoothColor / maxIterations;

					// Uses the float value as part of the value for the hue of the pixel, and converts it to an RGB
					// representation of the colour
					color = Color.HSBtoRGB((float) (0.55f + 10 * smoothColor), 0.6f, 1.0f);

					break;
				}
			}
			return color;
		}


		public void saveJuliaImage()
		{

			try
			{
				if (!IMAGE_DIRECTORY.exists())
				{
					IMAGE_DIRECTORY.mkdir();
				}
				int noOfFiles;
				if (IMAGE_DIRECTORY.list() != null)
					noOfFiles = IMAGE_DIRECTORY.list().length;
				else
					noOfFiles = 0;
				File outputFile = new File(IMAGE_DIRECTORY + "/julia" + (noOfFiles + 1) + ".png");
				ImageIO.write(getJuliaImage(), "png", outputFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}


		@Override
		public void componentResized(ComponentEvent e)
		{
			// TODO Auto-generated method stub

		}


		@Override
		public void componentMoved(ComponentEvent e)
		{
			// TODO Auto-generated method stub

		}


		@Override
		public void componentShown(ComponentEvent e)
		{
			// TODO Auto-generated method stub

		}


		@Override
		public void componentHidden(ComponentEvent e)
		{
			// TODO Auto-generated method stub

		}


		@Override
		public void mouseClicked(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}


		@Override
		public void mousePressed(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}


		@Override
		public void mouseReleased(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}


		@Override
		public void mouseEntered(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}


		@Override
		public void mouseExited(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}


		BufferedImage getJuliaImage()
		{
			return juliaImage;
		}


		void setJuliaImage(BufferedImage juliaImage)
		{
			this.juliaImage = juliaImage;
		}


		public Pair<Double, Double> getConversionRatio()
		{
			return conversionRatio;
		}


		public void setConversionRatio(Pair<Double, Double> conversionRatio)
		{
			this.conversionRatio = conversionRatio;
		}


		private BufferedImage getJuliaImageRing()
		{
			return juliaImageRing;
		}

	}

	class InfoPanel extends JPanel implements ActionListener
	{

		private static final long serialVersionUID = -5157980355510237660L;

		private JFormattedTextField txtRealLower, txtImaginaryLower;
		private JFormattedTextField txtRealUpper, txtImaginaryUpper;
		private JButton btnChangeAxis;
		private JLabel lblRealBounds;
		private JLabel lblImaginaryBounds;
		private JLabel lblIterations;
		private JFormattedTextField txtIterations;
		private JButton btnSubmitIterations;
		private JLabel lblSelectedComplexPoint;
		private JComboBox<String> cmbJuliaFavourites;


		public void init()
		{
			getPnlInfo().setLayout(new BoxLayout(getPnlInfo(), BoxLayout.LINE_AXIS));

			lblRealBounds = new JLabel("Real Pair (x):     ");
			lblImaginaryBounds = new JLabel("Imaginary Pair (y):     ");
			lblIterations = new JLabel("Iterations:     ");
			lblSelectedComplexPoint = new JLabel("Selected point: ");

			/*
			 * try { txtRealLower = new JFormattedTextField(new MaskFormatter("##.##")); txtImaginaryLower = new
			 * JFormattedTextField(new MaskFormatter("##.##")); txtRealUpper = new JFormattedTextField(new
			 * MaskFormatter("##.##")); txtImaginaryUpper = new JFormattedTextField(new MaskFormatter("##.##"));
			 * 
			 * txtRealLower.setValue(new Double(-2)); txtImaginaryLower.setValue(new Double(-1.6));
			 * txtRealUpper.setValue(new Double(2)); txtImaginaryUpper.setValue(new Double(1.6)); } catch
			 * (ParseException e) { System.err.println(e.getMessage()); e.printStackTrace(); }
			 */

			txtRealLower = new JFormattedTextField(new Double(DEFAULT_X_AXIS_COMPLEX.getLeft()));
			txtRealUpper = new JFormattedTextField(new Double(DEFAULT_X_AXIS_COMPLEX.getRight()));
			txtImaginaryLower = new JFormattedTextField(new Double(DEFAULT_Y_AXIS_COMPLEX.getLeft()));
			txtImaginaryUpper = new JFormattedTextField(new Double(DEFAULT_Y_AXIS_COMPLEX.getRight()));
			btnChangeAxis = new JButton("Submit New Axis");
			txtIterations = new JFormattedTextField(Integer.valueOf(DEFAULT_ITERATIONS));
			btnSubmitIterations = new JButton("Submit Iteration Amount");

			if (IMAGE_DIRECTORY.list() != null)
			{
				String[] imageList = IMAGE_DIRECTORY.list();
				cmbJuliaFavourites = new JComboBox<String>(imageList);
			}
			else
				cmbJuliaFavourites = new JComboBox<String>();

			cmbJuliaFavourites.setSelectedItem(null);
			cmbJuliaFavourites.addActionListener(new ComboBoxListener());

			txtRealLower.setMargin(new Insets(5, 5, 5, 5));
			txtRealUpper.setMargin(new Insets(5, 5, 5, 5));
			txtImaginaryLower.setMargin(new Insets(5, 5, 5, 5));
			txtImaginaryUpper.setMargin(new Insets(5, 5, 5, 5));
			txtIterations.setMargin(new Insets(5, 5, 5, 5));

			txtRealLower.setPreferredSize(new Dimension(50, 25));
			txtRealUpper.setPreferredSize(new Dimension(50, 25));
			txtImaginaryLower.setPreferredSize(new Dimension(50, 25));
			txtImaginaryUpper.setPreferredSize(new Dimension(50, 25));
			btnChangeAxis.setPreferredSize(new Dimension(150, 25));
			txtIterations.setPreferredSize(new Dimension(50, 25));
			btnSubmitIterations.setPreferredSize(new Dimension(200, 25));

			txtRealLower.setMaximumSize(new Dimension(100, 25));
			txtRealUpper.setMaximumSize(new Dimension(100, 25));
			txtImaginaryLower.setMaximumSize(new Dimension(100, 25));
			txtImaginaryUpper.setMaximumSize(new Dimension(100, 25));
			btnChangeAxis.setMaximumSize(new Dimension(200, 25));
			txtIterations.setMaximumSize(new Dimension(100, 25));
			btnSubmitIterations.setMaximumSize(new Dimension(200, 25));

			getPnlInfo().setBackground(Color.lightGray);
			getPnlInfo().setPreferredSize(new Dimension(DEFAULT_FRAME_WIDTH, (int) (DEFAULT_FRAME_HEIGHT * 0.125)));
			getPnlInfo().add(Box.createHorizontalGlue());
			getPnlInfo().add(lblRealBounds);
			getPnlInfo().add(txtRealLower);
			getPnlInfo().add(new JLabel("  -  "));
			getPnlInfo().add(txtRealUpper);
			getPnlInfo().add(Box.createHorizontalGlue());
			getPnlInfo().add(lblImaginaryBounds);
			getPnlInfo().add(txtImaginaryLower);
			getPnlInfo().add(new JLabel("  -  "));
			getPnlInfo().add(txtImaginaryUpper);
			getPnlInfo().add(Box.createHorizontalGlue());
			getPnlInfo().add(btnChangeAxis);
			getPnlInfo().add(Box.createHorizontalGlue());
			getPnlInfo().add(lblIterations);
			getPnlInfo().add(txtIterations);
			getPnlInfo().add(Box.createHorizontalGlue());
			getPnlInfo().add(btnSubmitIterations);
			getPnlInfo().add(Box.createHorizontalGlue());
			getPnlInfo().add(lblSelectedComplexPoint);
			getPnlInfo().add(Box.createHorizontalGlue());
			getPnlInfo().add(cmbJuliaFavourites);
			getPnlInfo().add(Box.createHorizontalGlue());
			getPnlInfo().add(new JLabel("Press P to save a Julia Image"));
			getPnlInfo().add(Box.createHorizontalGlue());

			btnChangeAxis.addActionListener(this);
			btnSubmitIterations.addActionListener(this);
			getPnlOuter().add(getPnlInfo());
		}


		@Override
		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			super.paintComponent(g);
		}


		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == btnChangeAxis)
			{
				setxAxisComplex(new Pair<Double, Double>(Double.parseDouble(txtRealLower.getText()), Double.parseDouble(txtRealUpper.getText())));
				setyAxisComplex(new Pair<Double, Double>(Double.parseDouble(txtImaginaryLower.getText()), Double.parseDouble(txtImaginaryUpper
						.getText())));
			}
			else if (e.getSource() == btnSubmitIterations)
			{
				setIterations(Integer.parseInt(txtIterations.getText()));
			}
			getPnlMandelbrot().repaint();
		}


		public JLabel getLblSelectedComplexPoint()
		{
			return lblSelectedComplexPoint;
		}


		public void setLblSelectedComplexPoint(JLabel lblSelectedComplexPoint)
		{
			this.lblSelectedComplexPoint = lblSelectedComplexPoint;
		}


		class ComboBoxListener implements ActionListener
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					File fileSelected = new File(IMAGE_DIRECTORY + "/" + cmbJuliaFavourites.getItemAt(cmbJuliaFavourites.getSelectedIndex()));
					getPnlJulia().setJuliaImage(ImageIO.read(fileSelected));
					favouriteSelected = true;
					getPnlJulia().repaint();
				}
				catch (IOException exc)
				{
					exc.printStackTrace();
				}

			}
		}

	}

	public class JuliaThread extends Thread
	{

		public JuliaThread()
		{
			this.setPriority(Thread.MAX_PRIORITY);
		}
		
		/**
		 * Constantly iterates, checking if the flag that the Julia set needs a recalculate is set to true, if true,
		 * then recalculate the Julia set
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run()
		{
			while (true)
			{
				if (isJuliaNeedsRecalculate())
				{
					// Sets a flag indicating that the Julia calculation is over
					setJuliaNeedsRecalculate(false);
					
					// Sets the complex coordinate used for Julia set calculations to the last position of the cursor in
					// the Mandelbrot panel
					setComplexCoordinate(Maths.convertCoordinateToComplexPlane(getPnlMandelbrot().getCursorLocationRing().get(0), getPnlJulia()
							.getConversionRatio(), getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));

					//getPnlJulia().setJuliaImage(new BufferedImage(getWidth(), getHeight(), getPAINT_TYPE()));

					// Generates a Julia set image with the complex coordinate that was set
					BufferedImage tempImage = getPnlJulia().paintJuliaSet();

					// Adds the Julia image just created to a ring, where the most recent image is at index is 0
					getPnlJulia().juliaImageRing = tempImage;
					
					//Tells the Swing thread to repaint the Julia panel
					getPnlJulia().repaint();
				}
			}
		}

	}

}