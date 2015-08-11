package scraper;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.objectplanet.image.PngEncoder;

public class SovScraper
{

	String BASE_URL = "http://go-dl1.eve-files.com/media/corp/verite/";
	BufferedImage sovImage = null;
	ExecutorService executorService = Executors.newCachedThreadPool();
	URL imageURL = null;
	PngEncoder encoder = new PngEncoder();
	String encodeType = "png";

	int year;
	int month;
	int day;

	public static void main(String[] args)
	{
		SovScraper sovScraper = new SovScraper();
		try
		{
			sovScraper.scrapeImages(String[] args);
			sovScraper.waitForImages();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void scrapeImages(String[] args) throws IOException
	{
		private String encodeType = "png";
		if (args[0] != null)
		{
			encodeType = args[0];
		}
		for (year = 2015; year < 2016; year++)
		{
			for (month = 01; month < 13; month++)
			{
				for (day = 01; day < 32; day++)
				{
					String uniqueImageCode = readImageFromUrl();
					if (uniqueImageCode != null)
					{
						writeImage(uniqueImageCode, encodeType);
					}
				}
			}
		}
	}

	private String readImageFromUrl()
	{
		String uniqueImageCode = String.format("%04d", year) + String.format("%02d", month)
				+ String.format("%02d", day);
		try
		{
			imageURL = new URL(BASE_URL + uniqueImageCode + ".png");
		}
		catch (MalformedURLException e)
		{
			System.out.println(imageURL + " not found");
			return null;
		}

		// System.out.println(imageURL);

		try
		{
			sovImage = ImageIO.read(imageURL);
		}
		catch (IOException e)
		{
			System.out.println(imageURL + " could not read");
			return null;
		}

		return uniqueImageCode;
	}

	private void writeImage(String uniqueImageCode, String encodeType)
	{
		Runnable ioTask = () ->
		{
			try
			{
				FileOutputStream fos = new FileOutputStream(
						"C:/Users/Will/Documents/Eclipse/git/EVE SovMap Scraper/Output/" + uniqueImageCode + ".png");
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				encoder.encode(sovImage, bos);
				bos.close();
				System.out.println(uniqueImageCode + ".png "
						+ TimeUnit.MILLISECONDS.convert(getCpuTime(), TimeUnit.NANOSECONDS) + "ms");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		};

		executorService.submit(ioTask);
	}

	public void waitForImages()
	{
		executorService.shutdown();

		try
		{
			executorService.awaitTermination(3, TimeUnit.HOURS);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public long getCpuTime()
	{
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
	}
}
