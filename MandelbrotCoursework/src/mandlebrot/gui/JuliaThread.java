package mandlebrot.gui;

import java.awt.image.BufferedImage;

import mandelbrot.maths.Maths;

public class JuliaThread extends Thread
{

	private GUI gui;


	public JuliaThread(GUI gui)
	{
		this.gui = gui;
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
			if (gui.isJuliaNeedsRecalculate())
			{
				// Sets a flag indicating that the Julia calculation is over
				gui.setJuliaNeedsRecalculate(false);

				// Sets the complex coordinate used for Julia set calculations to the last position of the cursor in
				// the Mandelbrot panel
				gui.setComplexCoordinate(Maths.convertCoordinateToComplexPlane(gui.getPnlMandelbrot().getCursorLocation(), gui.getPnlMandelbrot()
						.getConversionRatio(), gui.getPnlMandelbrot().getWidth(), gui.getPnlMandelbrot().getHeight(), gui.getxAxisComplex(), gui.getyAxisComplex()));

				// getPnlJulia().setJuliaImage(new BufferedImage(getWidth(), getHeight(), getPAINT_TYPE()));

				// Generates a Julia set image with the complex coordinate that was set
				BufferedImage tempImage = gui.getPnlJulia().paintJuliaSet();

				// Adds the Julia image just created to a ring, where the most recent image is at index is 0
				gui.getPnlJulia().setJuliaImage(tempImage);

				// Tells the Swing thread to repaint the Julia panel
				gui.getPnlJulia().repaint();
			}
		}
	}

}