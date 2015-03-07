package mandlebrot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mandelbrot.maths.ComplexNumber;
import mandelbrot.maths.Maths;
import utilities.Pair;

public class GUI extends JFrame
{

	private static Rectangle screenBounds = GUI.getScreenResolution();
	private final static int DEFAULT_FRAME_WIDTH = (int) (screenBounds.getWidth() * 0.95);
	private final static int DEFAULT_FRAME_HEIGHT = (int) (screenBounds.getHeight() * 0.75);
	private final static Pair<Double, Double> DEFAULT_Y_AXIS_COMPLEX = new Pair<Double, Double>(-2.0, 2.0);
	private final static Pair<Double, Double> DEFAULT_X_AXIS_COMPLEX = new Pair<Double, Double>(-1.6, 1.6);
	private final static int DEFAULT_ITERATIONS = 100;
	private final int PAINT_TYPE = BufferedImage.TYPE_INT_ARGB;
	private final static File IMAGE_DIRECTORY = new File(System.getProperty("user.dir") + "/images/");

	GUI frame;
	private JPanel pnlOuter;
	private JPanel pnlFractal;
	private MandelbrotPanel pnlMandelbrot;
	private JuliaPanel pnlJulia;
	private InfoPanel pnlInfo;

	private Pair<Double, Double> xAxisComplex;
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
		juliaNeedsRecalculate = false;
	}


	public static void main(String[] args)
	{
		GUI frame = new GUI();
		frame.juliaThread.start();
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT));

		frame.setXAxisComplex(DEFAULT_Y_AXIS_COMPLEX);
		frame.setYAxisComplex(DEFAULT_X_AXIS_COMPLEX);
		frame.setIterations(DEFAULT_ITERATIONS);

		frame.init();
	}


	public void init()
	{
		setTitle("Mandelbrot Visualiser");

		pnlOuter = new JPanel();
		pnlFractal = new JPanel();
		pnlFractal.setLayout(new BoxLayout(pnlFractal, BoxLayout.LINE_AXIS));
		pnlMandelbrot = new MandelbrotPanel();
		pnlJulia = new JuliaPanel();
		pnlInfo = new InfoPanel();

		pnlFractal.setSize(new Dimension(DEFAULT_FRAME_WIDTH, (int) (DEFAULT_FRAME_HEIGHT * 0.875)));

		pnlOuter.setPreferredSize(new Dimension(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT));
		setContentPane(pnlOuter);
		pnlOuter.setLayout(new BoxLayout(pnlOuter, BoxLayout.PAGE_AXIS));

		pnlOuter.add(pnlFractal);
		pnlMandelbrot.init();
		pnlJulia.init();
		pnlInfo.init();

		setSize(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}


	public static Rectangle getScreenResolution()
	{
		Rectangle virtualBounds = new Rectangle();
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		virtualBounds.setBounds(0, 0, gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
		return virtualBounds;
	}


	public Pair<Double, Double> getXPairComplex()
	{
		return getxAxisComplex();
	}


	public Pair<Double, Double> getYPairComplex()
	{
		return getyAxisComplex();
	}


	public int getIterations()
	{
		return iterations;
	}


	public ComplexNumber getComplexCoordinate()
	{
		return complexCoordinate;
	}


	public void setXAxisComplex(Pair<Double, Double> xPairComplex)
	{
		this.setxAxisComplex(xPairComplex);
	}


	public void setYAxisComplex(Pair<Double, Double> yPairComplex)
	{
		this.setyAxisComplex(yPairComplex);
	}


	public void setIterations(int iterations)
	{
		this.iterations = iterations;
	}


	private Pair<Double, Double> getxAxisComplex()
	{
		return xAxisComplex;
	}


	private void setxAxisComplex(Pair<Double, Double> xAxisComplex)
	{
		this.xAxisComplex = xAxisComplex;
	}


	private Pair<Double, Double> getyAxisComplex()
	{
		return yAxisComplex;
	}


	private void setyAxisComplex(Pair<Double, Double> yAxisComplex)
	{
		this.yAxisComplex = yAxisComplex;
	}


	private void setComplexCoordinate(ComplexNumber complexCoordinate)
	{
		this.complexCoordinate = complexCoordinate;
	}


	public boolean isJuliaNeedsRedraw()
	{
		return juliaNeedsRecalculate;
	}


	public void setJuliaNeedsRedraw(boolean juliaNeedsRedraw)
	{
		this.juliaNeedsRecalculate = juliaNeedsRedraw;
	}


	class MandelbrotPanel extends JPanel implements MouseListener, ComponentListener, MouseMotionListener, KeyListener
	{

		private Point clickLocation;
		Point pressLocation;
		Point releaseLocation;
		private BufferedImage mandelbrotImage;
		private Pair<Double, Double> conversionRatio;
		Rectangle selection = null;

		int paintType;

		private static final long serialVersionUID = 1900295689838487856L;


		public MandelbrotPanel()
		{
			super();
			paintType = BufferedImage.TYPE_INT_ARGB;
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.addKeyListener(this);
		}


		public void init()
		{
			pnlMandelbrot.setBackground(Color.GRAY);
			pnlMandelbrot.setPreferredSize(new Dimension((int) (pnlFractal.getWidth() * (0.6)), (int) (pnlFractal.getHeight())));
			setMandelbrotImage(new BufferedImage((int) (pnlFractal.getWidth() * (0.6)), pnlFractal.getHeight(), paintType));
			setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));
			pnlFractal.add(pnlMandelbrot);
		}


		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			super.paintComponent(g2);

			setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));
			if (selection == null)
			{
				setMandelbrotImage(new BufferedImage(getWidth(), getHeight(), paintType));
				paintMandelbrotSet();
				g2.drawImage(getMandelbrotImage(), 0, 0, null);
			} else
			{
				g2.drawImage(getMandelbrotImage(), 0, 0, null);
				g2.draw(selection);
			}
			pnlMandelbrot.requestFocusInWindow();

		}


		public void paintMandelbrotSet()
		{
			int width = getWidth();
			int height = getHeight();
			Pair<Double, Double> xAxisComplex = getxAxisComplex();
			Pair<Double, Double> yAxisComplex = getyAxisComplex();
			Pair<Double, Double> conversionRatio = getConversionRatio();

			int i;
			int color;
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


		public int generateColor(ComplexNumber complexCoordinate)
		{
			int color = Color.BLACK.getRGB();
			float nsmooth = 0;
			ComplexNumber z = complexCoordinate;
			for (int i = 0; i < getIterations(); i++)
			{
				z = (z.square()).add(complexCoordinate);
				if (z.modulusSquared() > 4)
				{
					nsmooth = (float) (i + 1 - Math.log(Math.log(z.modulusSquared())) / Math.log(2));
					nsmooth = nsmooth / getIterations();
					color = Color.HSBtoRGB(0.95f + 10 * nsmooth, 0.6f, 1.0f);
					break;
				}
			}
			return color;
		}


		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				pnlMandelbrot.requestFocusInWindow();
				System.out.println("Click");

				setClickLocation(new Point(e.getX(), e.getY()));
				setComplexCoordinate(Maths.convertCoordinateToComplexPlane(getClickLocation(), getConversionRatio(), getWidth(), getHeight(),
						getxAxisComplex(), getyAxisComplex()));
				DecimalFormat df = new DecimalFormat("#.##");

				String connector;

				if (getComplexCoordinate().getImaginary() < 0)
				{
					connector = " - ";
					getComplexCoordinate().setImaginary(Math.abs(getComplexCoordinate().getImaginary()));
				} else
					connector = " + ";
				pnlInfo.getLblSelectedComplexPoint().setText(
						(("Selected point: " + "z = " + df.format(getComplexCoordinate().getReal()) + connector
								+ df.format(getComplexCoordinate().getImaginary()) + "i")));

				pnlJulia.repaint();
			}
		}


		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				pressLocation = new Point(e.getX(), e.getY());
				selection = new Rectangle(pressLocation);
			}
		}


		@Override
		public void mouseReleased(MouseEvent e)
		{
			int width = getWidth();
			int height = getHeight();

			if (e.getButton() == MouseEvent.BUTTON1)
			{
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
				pnlMandelbrot.repaint();
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


		@Override
		public void componentResized(ComponentEvent e)
		{
			Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex());
		}


		@Override
		public void componentShown(ComponentEvent e)
		{
		}


		@Override
		public void mouseDragged(MouseEvent e)
		{

			int x = (int) Math.min(pressLocation.x, e.getX());
			int y = (int) Math.min(pressLocation.y, e.getY());
			int width = (int) Math.abs(pressLocation.getX() - e.getX());
			int height = (int) Math.abs(pressLocation.getY() - e.getY());

			selection.setBounds(x, y, width, height);
			repaint();
		}


		@Override
		public void mouseMoved(MouseEvent e)
		{		
			setClickLocation(new Point(e.getX(), e.getY()));
			juliaNeedsRecalculate = true;
		}


		@Override
		public void keyPressed(KeyEvent e)
		{
			System.out.println("Print");
			if (e.getKeyCode() == KeyEvent.VK_P)
			{
				pnlJulia.saveJuliaImage();
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


		Point getClickLocation()
		{
			return clickLocation;
		}


		void setClickLocation(Point clickLocation)
		{
			this.clickLocation = clickLocation;
		}
	}

	class JuliaPanel extends JPanel implements MouseListener, ComponentListener
	{

		Point clickLocation;
		private BufferedImage juliaImage;
		private Pair<Double, Double> conversionRatio;

		private static final long serialVersionUID = 1900295689838487856L;


		public JuliaPanel()
		{
			super();
			this.addMouseListener(this);
		}


		public void init()
		{
			pnlJulia.setBackground(Color.GRAY);
			pnlJulia.setPreferredSize(new Dimension((int) (pnlFractal.getWidth() * (0.4)), (int) (pnlFractal.getHeight())));
			setJuliaImage(new BufferedImage((int) (pnlFractal.getWidth() * (0.4)), pnlFractal.getHeight(), PAINT_TYPE));
			setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));
			pnlFractal.add(pnlJulia);
		}


		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			super.paintComponent(g2);

			setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));

			if (favouriteSelected)
			{
				g2.drawImage(getJuliaImage(), 0, 0, null);
				favouriteSelected = false;
			} else
			{
				if (getComplexCoordinate() != null)
				{
					g2.drawImage(getJuliaImage(), 0, 0, null);
					pnlMandelbrot.requestFocusInWindow();
				}
			}

		}


		public void paintJuliaSet()
		{
			int width = getWidth();
			int height = getHeight();
			ComplexNumber iteratingCoordinate;

			for (int x = 0; x < pnlJulia.getJuliaImage().getWidth(); x++)
			{
				for (int y = 0; y < pnlJulia.getJuliaImage().getHeight(); y++)
				{
					iteratingCoordinate = Maths.convertCoordinateToComplexPlane(new Point(x, y), getConversionRatio(), width, height,
							DEFAULT_X_AXIS_COMPLEX, DEFAULT_Y_AXIS_COMPLEX);
					pnlJulia.getJuliaImage().setRGB(x, y, generateColor(iteratingCoordinate));
				}
			}

		}


		private int generateColor(ComplexNumber z)
		{
			double smoothColor;
			int color;
			int MAX_ITERATIONS = getIterations();
			ComplexNumber complexCoordinate;

			complexCoordinate = getComplexCoordinate();
			color = Color.BLACK.getRGB();
			smoothColor = Math.exp(-(z.modulusSquared()));
			for (int i = 0; i < MAX_ITERATIONS; i++)
			{
				z = (z.square()).add(complexCoordinate);
				smoothColor += Math.exp(-z.modulusSquared());
				if (z.modulusSquared() > 4)
				{
					smoothColor = smoothColor / MAX_ITERATIONS;
					color = Color.HSBtoRGB((float) (0.55f + 10 * smoothColor), 0.6f, 1.0f);
					color = new Color(color).getRGB();
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
			} catch (IOException e)
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


		public InfoPanel()
		{
			super();
		}


		public void init()
		{
			pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.LINE_AXIS));

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

			txtRealLower = new JFormattedTextField(new Double(DEFAULT_Y_AXIS_COMPLEX.getLeft()));
			txtRealUpper = new JFormattedTextField(new Double(DEFAULT_Y_AXIS_COMPLEX.getRight()));
			txtImaginaryLower = new JFormattedTextField(new Double(DEFAULT_X_AXIS_COMPLEX.getLeft()));
			txtImaginaryUpper = new JFormattedTextField(new Double(DEFAULT_X_AXIS_COMPLEX.getRight()));
			btnChangeAxis = new JButton("Submit New Axis");
			txtIterations = new JFormattedTextField(new Integer(DEFAULT_ITERATIONS));
			btnSubmitIterations = new JButton("Submit Iteration Amount");
			String[] imageList = IMAGE_DIRECTORY.list();
			cmbJuliaFavourites = new JComboBox<String>(imageList);
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

			pnlInfo.setBackground(Color.lightGray);
			pnlInfo.setPreferredSize(new Dimension(DEFAULT_FRAME_WIDTH, (int) (DEFAULT_FRAME_HEIGHT * 0.125)));
			pnlInfo.add(Box.createHorizontalGlue());
			pnlInfo.add(lblRealBounds);
			pnlInfo.add(txtRealLower);
			pnlInfo.add(new JLabel("  -  "));
			pnlInfo.add(txtRealUpper);
			pnlInfo.add(Box.createHorizontalGlue());
			pnlInfo.add(lblImaginaryBounds);
			pnlInfo.add(txtImaginaryLower);
			pnlInfo.add(new JLabel("  -  "));
			pnlInfo.add(txtImaginaryUpper);
			pnlInfo.add(Box.createHorizontalGlue());
			pnlInfo.add(btnChangeAxis);
			pnlInfo.add(Box.createHorizontalGlue());
			pnlInfo.add(lblIterations);
			pnlInfo.add(txtIterations);
			pnlInfo.add(Box.createHorizontalGlue());
			pnlInfo.add(btnSubmitIterations);
			pnlInfo.add(Box.createHorizontalGlue());
			pnlInfo.add(lblSelectedComplexPoint);
			pnlInfo.add(Box.createHorizontalGlue());
			pnlInfo.add(cmbJuliaFavourites);
			pnlInfo.add(Box.createHorizontalGlue());
			pnlInfo.add(new JLabel("Press P to save a Julia Image"));
			pnlInfo.add(Box.createHorizontalGlue());

			btnChangeAxis.addActionListener(this);
			btnSubmitIterations.addActionListener(this);
			pnlOuter.add(pnlInfo);
		}


		@Override
		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			super.paintComponent(g);

			int width = this.getWidth();
			int height = this.getHeight();
		}


		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == btnChangeAxis)
			{
				setXAxisComplex(new Pair<Double, Double>(Double.parseDouble(txtRealLower.getText()), Double.parseDouble(txtRealUpper.getText())));
				setYAxisComplex(new Pair<Double, Double>(Double.parseDouble(txtImaginaryLower.getText()), Double.parseDouble(txtImaginaryUpper
						.getText())));
			} else if (e.getSource() == btnSubmitIterations)
			{
				setIterations(Integer.parseInt(txtIterations.getText()));
			}
			pnlMandelbrot.repaint();
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
					pnlJulia.setJuliaImage(ImageIO.read(fileSelected));
					favouriteSelected = true;
					pnlJulia.repaint();
				} catch (IOException exc)
				{
					exc.printStackTrace();
				}

			}
		}

	}

	public class JuliaThread extends Thread
	{

		public void run()
		{
			while (true)
			{
				if (juliaNeedsRecalculate)
				{
					setComplexCoordinate(Maths.convertCoordinateToComplexPlane(pnlMandelbrot.getClickLocation(), pnlJulia.getConversionRatio(),
							getWidth(), getHeight(), getxAxisComplex(), getyAxisComplex()));
					pnlJulia.setJuliaImage(new BufferedImage(getWidth(), getHeight(), PAINT_TYPE));
					pnlJulia.paintJuliaSet();
					juliaNeedsRecalculate = false;
					pnlJulia.repaint();
				}
			}
		}

	}

}