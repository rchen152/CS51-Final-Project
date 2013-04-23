package image_comparer;
import java.util.Random;
import java.util.*;

/*
 * Matches key points in two images to compare them.  Assumes the images are
 * the same size
 */
public class KeypointComparer implements PixelArrayComparer
{
	public KeypointComparer()
	{

	}

	public double compare(PixelArray a1, PixelArray a2)
	{
		// We want the probability of a collision to be less than 0.5
		int n = (int) Math.sqrt(a1.getWidth() * a1.getHeight() * 1.386);
		if (n == 0)
			n = 1;

		return completeCompare(a1, a2, n);
	}

	/*
	 * Picks random points in two images and compares them.
	 */
	private double randomCompare(PixelArray a1, PixelArray a2, int n) {
		int width = a1.getWidth();
		int height = a1.getHeight();

		int numMatched=0, currWidth, currHeight;
		Random rand = new Random();
		for (int i=0; i<n; i++) {
			currWidth = rand.nextInt(width);
			currHeight = rand.nextInt(height);
			if (a1.getPixel(currWidth, currHeight)==
					a2.getPixel(currWidth, currHeight))
				numMatched++;
		}
		return (double)numMatched/n;
	}

	/*
	 * Intelligently picks points in two images to compare.
	 */
	private double completeCompare(PixelArray a1, PixelArray a2, int n) {
		int width = a1.getWidth();
		int height = a1.getHeight();
		ArrayList<Integer> xcos = new ArrayList<Integer>();
		ArrayList<Integer> ycos = new ArrayList<Integer>();
		double rsum;
		double bsum;
		double gsum;
		double diff;
		int counter = 0;

		// finds interesting pixels
		for (int i=1;++i<height;) {
			for (int j=1;++j<width;) {
				rsum = 0;
				bsum = 0;
				gsum = 0;
				for (int k=-1;k<2;++k) {
					for (int l=-1;l<2;++l) {
						rsum += (double)
								PixelArray.getRed(a1.getPixel(i+k, j+l));
						bsum += (double)
								PixelArray.getBlue(a1.getPixel(i+k, j+l));
						gsum += (double)
								PixelArray.getGreen(a1.getPixel(i+k, j+l));
					}
				}
				rsum /= 9.0; bsum /= 9.0; gsum /= 9.0;
				diff = (Math.abs(rsum-PixelArray.getRed(a1.getPixel(i, j)))
						+ Math.abs(bsum-PixelArray.getBlue(a1.getPixel(i, j)))
						+ Math.abs(gsum-
								PixelArray.getGreen(a1.getPixel(i, j))))/768.0;
				if (diff > 0.1) {
					xcos.add(i);
					ycos.add(j);
					++counter;
				}
			}
		}

		if (counter<20) {
			return randomCompare(a1, a2, n);
		}
		int matched = 0;
		for (int i=0; i<counter; i++) {
			if (a1.getPixel(xcos.get(i), ycos.get(i))==
					a2.getPixel(xcos.get(i), ycos.get(i)))
				matched++;
		}

		return ((double) matched) / ((double) counter);
	}
}
