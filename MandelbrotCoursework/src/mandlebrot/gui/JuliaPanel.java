package mandlebrot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import mandelbrot.maths.ComplexNumber;
import mandelbrot.maths.Maths;
import mandelbrot.utilities.Pair;

class JuliaPanel extends JPanel implements MouseListener, ComponentListener
{

	/**
	 * 
	 */
	private GUI gui;
	private BufferedImage juliaSavedImage;
	BufferedImage juliaImage; // A ring of the 10 most recent Julia images, the most
										// recent is at index 0
	private Pair<Double, Double> conversionRatio;

	private static final long serialVersionUID = 1900295689838487856L;


	public JuliaPanel(GUI gui)
	{
		super();
		this.gui = gui;
		this.addMouseListener(this);
	}


	/**
	 * Sets the preferred size of the panel, and calculates the conversion ratio from the panel bounds to the
	 * complex bounds
	 */
	public void init()
	{
		// juliaImageRing = new CircularArrayRing<BufferedImage>();

		gui.getPnlJulia().setBackground(Color.GRAY);
		gui.getPnlJulia().setPreferredSize(new Dimension((int) (gui.getPnlFractal().getWidth() * (0.4)), (int) (gui.getPnlFractal().getHeight())));

		setJuliaImage(new BufferedImage((int) (gui.getPnlFractal().getWidth() * (0.4)), gui.getPnlFractal().getHeight(), gui.getPAINT_TYPE()));
		setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), gui.getxAxisComplex(), gui.getyAxisComplex()));

		gui.getPnlFractal().add(gui.getPnlJulia());
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

		setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), gui.getxAxisComplex(), gui.getyAxisComplex()));

		if (gui.favouriteSelected)
		{
			super.paintComponent(g2);
			g2.drawImage(getJuliaImage(), 0, 0, null);
			gui.favouriteSelected = false;
		}
		else
		{
			if ((gui.getComplexCoordinate() != null) && (getJuliaImageRing() != null))
			{
				super.paintComponent(g2);

				// Fetches and draws the most recently calculated Julia image
				g2.drawImage(getJuliaImageRing(), 0, 0, null);

				gui.getPnlMandelbrot().requestFocusInWindow();
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

		tempImage = new BufferedImage(getWidth(), getHeight(), gui.PAINT_TYPE);

		for (int x = 0; x < gui.getPnlJulia().getWidth(); x++)
		{
			for (int y = 0; y < gui.getPnlJulia().getHeight(); y++)
			{
				iteratingCoordinate = Maths.convertCoordinateToComplexPlane(new Point(x, y), getConversionRatio(), width, height,
						GUI.DEFAULT_X_AXIS_COMPLEX, GUI.DEFAULT_Y_AXIS_COMPLEX);

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
		int maxIterations = gui.getIterations();
		color = Color.BLACK.getRGB();

		smoothColor = Math.exp(-(z.modulusSquared())); // e ^ -(abs(z))

		for (int i = 0; i < maxIterations; i++)
		{
			z = z.square().add(gui.complexCoordinate);

			// Increments the colour value by this amount for each iteration until the number diverges
			smoothColor += Math.exp(-z.modulusSquared());

			if (z.modulusSquared() > 4)
			{
				// Changes the range of smoothColor from [0, maxIterations] to [0, 1]
				smoothColor = smoothColor / maxIterations;

				// Uses the float value as part of the value for the hue of the pixel, and converts it to an RGB
				// representation of the colour
				color = Color.HSBtoRGB((float) (0.95f + 10 * smoothColor), 0.6f, 1.0f);

				break;
			}
		}
		return color;
	}


	public void saveJuliaImage()
	{

		try
		{
			if (!GUI.IMAGE_DIRECTORY.exists())
			{
				GUI.IMAGE_DIRECTORY.mkdir();
			}
			int noOfFiles;
			if (GUI.IMAGE_DIRECTORY.list() != null)
				noOfFiles = GUI.IMAGE_DIRECTORY.list().length;
			else
				noOfFiles = 0;
			File outputFile = new File(GUI.IMAGE_DIRECTORY + "/julia" + (noOfFiles + 1) + ".png");
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
		return juliaSavedImage;
	}


	void setJuliaImage(BufferedImage juliaImage)
	{
		this.juliaSavedImage = juliaImage;
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
		return juliaImage;
	}

}