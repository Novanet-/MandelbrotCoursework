package mandlebrot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import mandelbrot.maths.ComplexNumber;
import mandelbrot.maths.Maths;
import mandelbrot.utilities.Pair;

class MandelbrotPanel extends JPanel implements MouseListener, ComponentListener, MouseMotionListener, KeyListener
{

	/**
	 * 
	 */
	private GUI gui;
	private Point cursorLocation;
	private Point pressLocation;
	private BufferedImage mandelbrotImage;
	private Pair<Double, Double> conversionRatio;
	private Rectangle selection = null;

	int paintType;

	private static final long serialVersionUID = 1900295689838487856L;


	/**
	 * Sets the paint type of the mandelbrot image and adds various Event Listeners to the panel
	 * 
	 * @param gui
	 *            TODO
	 */
	public MandelbrotPanel(GUI gui)
	{
		super();
		this.gui = gui;
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
		gui.getPnlMandelbrot().setBackground(Color.GRAY);
		gui.getPnlMandelbrot().setPreferredSize(
				new Dimension((int) (gui.getPnlFractal().getWidth() * (0.6)), (int) (gui.getPnlFractal().getHeight())));
		setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), gui.getxAxisComplex(), gui.getyAxisComplex()));
		gui.getPnlFractal().add(gui.getPnlMandelbrot());
		setMandelbrotImage(new BufferedImage((int) (gui.getPnlMandelbrot().getPreferredSize().getWidth()), (int) gui.getPnlMandelbrot()
				.getPreferredSize().getHeight(), paintType));
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

		setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), gui.getxAxisComplex(), gui.getyAxisComplex()));
		if (selection == null)
		{
			gui.setMandelbrotNeedsRecalculate(true);
			// paintMandelbrotSet();
			g2.drawImage(getMandelbrotImage(), 0, 0, null);
		}
		else
		{
			g2.drawImage(getMandelbrotImage(), 0, 0, null);
			g2.draw(selection);
		}
		gui.getPnlMandelbrot().requestFocusInWindow();

	}


	/**
	 * Iterates through all the pixels of the panel, converting each one to a complex coordinate and using that to
	 * generate a colour which will make up part of the mandelbrot image
	 */
	public BufferedImage paintMandelbrotSet()
	{
		int width = getWidth();
		int height = getHeight();
		Pair<Double, Double> xAxisComplex = gui.getxAxisComplex();
		Pair<Double, Double> yAxisComplex = gui.getyAxisComplex();
		Pair<Double, Double> conversionRatio = getConversionRatio();
		BufferedImage tempImage;

		ComplexNumber complexCoordinate;

		tempImage = new BufferedImage(width, height, gui.PAINT_TYPE);

		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				complexCoordinate = Maths.convertCoordinateToComplexPlane(new Point(x, y), conversionRatio, width, height, xAxisComplex,
						yAxisComplex);
				tempImage.setRGB(x, y, generateColor(complexCoordinate));
			}
		}

		return tempImage;

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
		for (int i = 0; i < gui.getIterations(); i++)
		{
			z = z.square().add(complexCoordinate);
			if (z.modulusSquared() > 4)
			{
				// A function to decide the colour of the pixel, based on how many iterations it took for the
				// complex number to diverge, if at all
				// Generates a float between [0, maxIterations]
				nsmooth = (float) (i + 1 - Math.log(Math.log(z.modulusSquared())) / Math.log(2));

				// Converts the float to a number between [0,1]
				nsmooth = nsmooth / gui.getIterations();

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
		gui.getPnlMandelbrot().requestFocusInWindow();
		System.out.println("Click");

		setCursorLocation(new Point(e.getX(), e.getY()));
		gui.setComplexCoordinate(Maths.convertCoordinateToComplexPlane(getCursorLocation(), getConversionRatio(), getWidth(), getHeight(),
				gui.getxAxisComplex(), gui.getyAxisComplex()));
		DecimalFormat df = new DecimalFormat("#.##");

		String connector;

		if (gui.getComplexCoordinate().getImaginary() < 0)
		{
			connector = " - ";
			gui.getComplexCoordinate().setImaginary(Math.abs(gui.getComplexCoordinate().getImaginary()));
		}
		else
			connector = " + ";
		gui.getPnlInfo()
				.getLblSelectedComplexPoint()
				.setText(
						("Selected point: " + "z = " + df.format(gui.getComplexCoordinate().getReal()) + connector
								+ df.format(gui.getComplexCoordinate().getImaginary()) + "i"));
	}


	/**
	 * Starts the drawing of a selection rectangle, used for zooming in on the Mandelbrot set
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

			int xLower = (int) selection.getMinX();
			int xUpper = (int) selection.getMaxX();

			int yLower = (int) selection.getMinY();
			int yUpper = (int) selection.getMaxY();

			ComplexNumber lowerComplex = Maths.convertCoordinateToComplexPlane(new Point(xLower, yLower), getConversionRatio(), width, height,
					gui.getxAxisComplex(), gui.getyAxisComplex());
			ComplexNumber upperComplex = Maths.convertCoordinateToComplexPlane(new Point(xUpper, yUpper), getConversionRatio(), width, height,
					gui.getxAxisComplex(), gui.getyAxisComplex());

			gui.setxAxisComplex(new Pair<Double, Double>(lowerComplex.getReal(), upperComplex.getReal()));
			gui.setyAxisComplex(new Pair<Double, Double>(lowerComplex.getImaginary(), upperComplex.getImaginary()));

			selection = null;
			gui.getPnlMandelbrot().repaint();
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
		Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), gui.getxAxisComplex(), gui.getyAxisComplex());
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
		if (SwingUtilities.isLeftMouseButton(e))
		{
			System.out.println("Drag");
			int x = (int) Math.min(pressLocation.x, e.getX());
			int y = (int) Math.min(pressLocation.y, e.getY());
			int width = (int) Math.abs(pressLocation.getX() - e.getX());
			int height = (int) Math.abs(pressLocation.getY() - e.getY());

			selection.setBounds(x, y, width, height);
			repaint();
		}
	}


	/**
	 * When the mouse is moved within Notifies the julia set calculation thread
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		System.out.println("Move");
		setCursorLocation(new Point(e.getX(), e.getY()));
		gui.setJuliaNeedsRecalculate(true);
	}


	@Override
	public void keyPressed(KeyEvent e)
	{
		System.out.println("Print");
		if (e.getKeyCode() == KeyEvent.VK_P)
		{
			gui.getPnlJulia().saveJuliaImage();
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


	Point getCursorLocation()
	{
		return cursorLocation;
	}


	private void setCursorLocation(Point cursorLocation)
	{
		this.cursorLocation = cursorLocation;
	}

}