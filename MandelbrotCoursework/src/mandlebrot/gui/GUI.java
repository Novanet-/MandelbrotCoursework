package mandlebrot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

public class GUI extends JFrame
{

	private final static int defaultFrameWidth = 1000;
	private final static int defaultFrameHeight = 800;

	private JPanel pnlOuter;
	private MandelbrotPanel pnlMandelbrot;
	private JPanel pnlInfo;
	private JFormattedTextField txtRealLower, txtImaginaryLower;
	private JFormattedTextField txtRealUpper, txtImaginaryUpper;
	private JButton btnChangeAxis;
	private JLabel lblRealBounds;
	private JLabel lblImaginaryBounds;
	private JLabel lblIterations;
	private JFormattedTextField txtIterations;
	private JButton btnSubmitIterations;

	private static final long serialVersionUID = -9167797785983558030L;


	public static void main(String[] args)
	{
		GUI frame = new GUI();
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(defaultFrameWidth, defaultFrameHeight));
		frame.init();
	}


	public void init()
	{
		setTitle("Mandelbrot Visualiser");
		pnlOuter = new JPanel();
		pnlMandelbrot = new MandelbrotPanel();
		pnlInfo = new JPanel();
		pnlOuter.setSize(defaultFrameWidth, defaultFrameHeight);
		setContentPane(pnlOuter);
		pnlOuter.setLayout(new BoxLayout(pnlOuter, BoxLayout.PAGE_AXIS));

		pnlMandelbrot.setBackground(Color.GRAY);
		pnlMandelbrot.setPreferredSize(new Dimension(defaultFrameWidth, (int) (defaultFrameHeight*0.875)));
		pnlOuter.add(pnlMandelbrot);

		pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.LINE_AXIS));

		lblRealBounds = new JLabel("Real axis (x):     ");
		lblImaginaryBounds = new JLabel("Imaginary axis (y):     ");
		lblIterations = new JLabel("Iterations:     ");

		/*try
		{
			txtRealLower = new JFormattedTextField(new MaskFormatter("##.##"));
			txtImaginaryLower = new JFormattedTextField(new MaskFormatter("##.##"));
			txtRealUpper = new JFormattedTextField(new MaskFormatter("##.##"));
			txtImaginaryUpper = new JFormattedTextField(new MaskFormatter("##.##"));
			
			txtRealLower.setValue(new Double(-2));
			txtImaginaryLower.setValue(new Double(-1.6));
			txtRealUpper.setValue(new Double(2));
			txtImaginaryUpper.setValue(new Double(1.6));
		} catch (ParseException e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}*/

		
		txtRealLower = new JFormattedTextField(new Double(-2));
		txtImaginaryLower = new JFormattedTextField(new Double(-1.6));
		txtRealUpper = new JFormattedTextField(new Double(2));
		txtImaginaryUpper = new JFormattedTextField(new Double(1.6));
		btnChangeAxis = new JButton("Submit New Axis");
		txtIterations = new JFormattedTextField(new Integer(100));
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
		pnlInfo.setPreferredSize(new Dimension(defaultFrameWidth, (int) (defaultFrameHeight*0.125)));
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
		pnlOuter.add(pnlInfo);

		setSize(defaultFrameWidth, defaultFrameHeight);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pnlMandelbrot.requestFocusInWindow();
		setVisible(true);
	}


	class MandelbrotPanel extends JPanel implements MouseListener
	{

		private static final long serialVersionUID = 1900295689838487856L;


		public MandelbrotPanel()
		{
			super();
			this.addMouseListener(this);
		}


		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			super.paintComponent(g);

			int width = this.getWidth();
			int height = this.getHeight();

		}


		@Override
		public void mouseClicked(MouseEvent e)
		{

			System.out.println("Click");

			int x = e.getX();
			int y = e.getY();

			repaint();

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

	}

}