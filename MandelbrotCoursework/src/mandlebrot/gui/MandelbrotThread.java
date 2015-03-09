package mandlebrot.gui;

import java.awt.image.BufferedImage;


public class MandelbrotThread extends Thread
{

	/**
	 * 
	 */
	private GUI gui;


	public MandelbrotThread(GUI gui)
	{
		this.gui = gui;
		this.setPriority(Thread.MAX_PRIORITY);
	}


	/**
	 * Constantly iterates, checking if the flag that the Mandelbrot set needs a recalculate is set to true, if true,
	 * then recalculate the Mandelbrot set
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		while (true)
		{
			if (gui.isMandelbrotNeedsRecalculate())
			{
				// Sets a flag indicating that the Julia calculation is over
				gui.setMandelbrotNeedsRecalculate(false);

				// Generates a Julia set image with the complex coordinate that was set
				BufferedImage tempImage = gui.getPnlMandelbrot().paintMandelbrotSet();

				// Adds the Julia image just created to a ring, where the most recent image is at index is 0
				gui.getPnlMandelbrot().setMandelbrotImage(tempImage);

				// Tells the Swing thread to repaint the Julia panel
				gui.getPnlMandelbrot().repaint();
			}
		}
	}

}
