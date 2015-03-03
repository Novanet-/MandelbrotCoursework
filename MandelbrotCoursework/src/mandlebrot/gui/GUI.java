package mandlebrot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mandelbrot.maths.ComplexNumber;
import mandelbrot.maths.Maths;
import utilities.Pair;

public class GUI extends JFrame
{

	private final static int defaultFrameWidth = 1000;
	private final static int defaultFrameHeight = 800;
	private final static Pair<Double, Double> defaultXAxisComplex = new Pair<Double, Double>(-2.0, 2.0);
	private final static Pair<Double, Double> defaultYAxisComplex = new Pair<Double, Double>(-1.6, 1.6);
	private final static int defaultIterations = 100;

	private JPanel pnlOuter;
	private MandelbrotPanel pnlMandelbrot;
	private InfoPanel pnlInfo;

	private Pair<Double, Double> xAxisComplex;
	private Pair<Double, Double> yAxisComplex;
	private int iterations;

	private Pair<Double, Double> conversionRatio;

	private static final long serialVersionUID = -9167797785983558030L;


	public static void main(String[] args)
	{
		GUI frame = new GUI();
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(defaultFrameWidth, defaultFrameHeight));

		frame.setXAxisComplex(defaultXAxisComplex);
		frame.setYAxisComplex(defaultYAxisComplex);
		frame.setIterations(defaultIterations);

		frame.init();
	}


	public void init()
	{
		setTitle("Mandelbrot Visualiser");

		pnlOuter = new JPanel();
		pnlMandelbrot = new MandelbrotPanel();
		pnlInfo = new InfoPanel();

		pnlOuter.setSize(defaultFrameWidth, defaultFrameHeight);
		setContentPane(pnlOuter);
		pnlOuter.setLayout(new BoxLayout(pnlOuter, BoxLayout.PAGE_AXIS));

		pnlMandelbrot.init();
		pnlInfo.init();

		setSize(defaultFrameWidth, defaultFrameHeight);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}


	public Pair<Double, Double> getXPairComplex()
	{
		return xAxisComplex;
	}


	public Pair<Double, Double> getYPairComplex()
	{
		return yAxisComplex;
	}


	public int getIterations()
	{
		return iterations;
	}


	public void setXAxisComplex(Pair<Double, Double> xPairComplex)
	{
		this.xAxisComplex = xPairComplex;
	}


	public void setYAxisComplex(Pair<Double, Double> yPairComplex)
	{
		this.yAxisComplex = yPairComplex;
	}


	public void setIterations(int iterations)
	{
		this.iterations = iterations;
	}


	public Pair<Double, Double> getConversionRatio()
	{
		return conversionRatio;
	}


	public void setConversionRatio(Pair<Double, Double> conversionRatio)
	{
		this.conversionRatio = conversionRatio;
	}


	class MandelbrotPanel extends JPanel implements MouseListener, ComponentListener
	{

		Point clickLocation;
		BufferedImage mandelbrotImage;
		Pair<Double, Double> conversionRatio;

		int paintType;

		private static final long serialVersionUID = 1900295689838487856L;


		public MandelbrotPanel()
		{
			super();
			paintType = BufferedImage.TYPE_INT_ARGB;
			this.addMouseListener(this);
		}


		public void init()
		{
			pnlMandelbrot.setBackground(Color.GRAY);
			pnlMandelbrot.setPreferredSize(new Dimension(defaultFrameWidth, (int) (defaultFrameHeight * 0.875)));
			setConversionRatio(Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), xAxisComplex, yAxisComplex));
			pnlOuter.add(pnlMandelbrot);
		}


		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			super.paintComponent(g2);

			conversionRatio = Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), xAxisComplex, yAxisComplex);
			paintMandelbrotSet();
			g2.drawImage(mandelbrotImage, 0, 0, null);

		}


		public void paintMandelbrotSet()
		{
			int width = getWidth();
			int height = getHeight();
			int color;
			ComplexNumber complexCoordinate;
			Random random = new Random();
			
			int paintType = BufferedImage.TYPE_INT_ARGB;

			mandelbrotImage = new BufferedImage(width, height, paintType);

			for(int x = 0; x < width; x++) {
			    for(int y = 0; y < height; y++) {
			    	complexCoordinate = Maths.convertCoordinateToComplexPlane(new Point(x, y), conversionRatio, getWidth(), getHeight(), xAxisComplex, yAxisComplex);
			    	color = Color.BLACK.getRGB();
			    	ComplexNumber z = complexCoordinate;
			    	for (int i = 0; i < getIterations(); i++)
			    	{
			    		z = (z.square()).add(complexCoordinate);
			    		if (Math.sqrt(z.modulusSquared()) >= 2)
			    		{
			    			//color = new Color( (255 * i % 100) / 100, (175 * (100 - i % 100)) / 100, (255 * (100 - i % 100)) / 100).getRGB();
			    			int test = (int) Math.round(255 - (255/2 * Math.abs(Math.sin(i/getIterations() * 2 * Math.PI))));
			    			color = new Color(0, test, test).getRGB();
			    			break;
			    		}
			    	}
			    	//color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)).getRGB();
			        mandelbrotImage.setRGB(x, y, color);
			    }
			}

		}


		@Override
		public void mouseClicked(MouseEvent e)
		{

			System.out.println("Click");

			clickLocation = new Point(e.getX(), e.getY());

			// repaint();

		}


		@Override
		public void mousePressed(MouseEvent e)
		{
		}


		@Override
		public void mouseReleased(MouseEvent e)
		{
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
			Maths.calculateRealtoComplexRatio(getWidth(), getHeight(), xAxisComplex, yAxisComplex);
		}


		@Override
		public void componentShown(ComponentEvent e)
		{
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

			/*
			 * try { txtRealLower = new JFormattedTextField(new MaskFormatter("##.##")); txtImaginaryLower = new
			 * JFormattedTextField(new MaskFormatter("##.##")); txtRealUpper = new JFormattedTextField(new
			 * MaskFormatter("##.##")); txtImaginaryUpper = new JFormattedTextField(new MaskFormatter("##.##"));
			 * 
			 * txtRealLower.setValue(new Double(-2)); txtImaginaryLower.setValue(new Double(-1.6));
			 * txtRealUpper.setValue(new Double(2)); txtImaginaryUpper.setValue(new Double(1.6)); } catch
			 * (ParseException e) { System.err.println(e.getMessage()); e.printStackTrace(); }
			 */

			txtRealLower = new JFormattedTextField(new Double(defaultXAxisComplex.getLeft()));
			txtRealUpper = new JFormattedTextField(new Double(defaultXAxisComplex.getRight()));
			txtImaginaryLower = new JFormattedTextField(new Double(defaultYAxisComplex.getLeft()));
			txtImaginaryUpper = new JFormattedTextField(new Double(defaultYAxisComplex.getRight()));
			btnChangeAxis = new JButton("Submit New Axis");
			txtIterations = new JFormattedTextField(new Integer(defaultIterations));
			btnSubmitIterations = new JButton("Submit Iteration Amount");

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
			pnlInfo.setPreferredSize(new Dimension(defaultFrameWidth, (int) (defaultFrameHeight * 0.125)));
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
		}
	}

}