package socialdear.views.component;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.net.URL;

import javax.swing.ImageIcon;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * 1.2
 */
public class CustomIcon extends ImageIcon {

	/**
	 * 
	 */

	public static final int ICON_SIZE = 15;

	private static final long serialVersionUID = -5733493009811416289L;

	public CustomIcon(String path, Object resourceLoader) {
		this(path, ICON_SIZE, resourceLoader);
	}

	public CustomIcon(String path, int size, Object resourceLoader) {

		if (path.endsWith(".png")) {
			URL url = null;
			try {
				url = resourceLoader.getClass().getResource("/src/main/resources/icons/" + path);
			} catch (NullPointerException e) {
				url = this.getClass().getResource("/src/main/resources/icons/" + path);

			}
			try {
				ImageIcon imageIcon = new ImageIcon(url);
				Image image = imageIcon.getImage();
				loadImage(image);
				Image scaledInstance = image.getScaledInstance(size, size, Image.SCALE_DEFAULT);
				loadImage(scaledInstance);
				setImage(scaledInstance);
			} catch (Exception e) {
				org.eclipse.swt.graphics.Image image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
				setImage(convertToAWT(image.getImageData()));
			}
		} else if (path.endsWith(".svg")) {
			//
		}
	}

	/**
	 * @since 3.0
	 */
	public static ImageData convertToSWT(BufferedImage bufferedImage) {
		int[] bitMasks = new int[] {16711680, 65280, 255};
		int[] bitOffsets = new int[] {16, 8, 0};

		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					if (rgb != -1) {
						//raster.getPixel(x, y, pixelArray);
						for (int i = 0; i < 3; i++) {
							pixelArray[i] = ((rgb & bitMasks[i]) >>> bitOffsets[i]);
						}
						int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
						data.setPixel(x, y, pixel);
					} else {
						data.setPixel(x, y, rgb);
					}
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

	static BufferedImage convertToAWT(ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					bufferedImage.setRGB(x, y, rgb.red << 16 | rgb.green << 8 | rgb.blue);
				}
			}
			return bufferedImage;
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte) rgb.red;
				green[i] = (byte) rgb.green;
				blue[i] = (byte) rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
			}
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}

	public static org.eclipse.swt.graphics.Image addImages(org.eclipse.swt.graphics.Image a, org.eclipse.swt.graphics.Image b, int offsetX, int offsetY) {
		Rectangle boundsA = a.getBounds();
		Rectangle boundsB = b.getBounds();

		int leftStart = Math.min(0, offsetX);
		int topStart = Math.min(0, offsetY);

		int leftEnd = Math.max(boundsA.width, offsetX + boundsB.width);
		int topEnd = Math.max(boundsA.height, offsetY + boundsB.height);

		Rectangle bounds = new Rectangle(0, 0, leftEnd - leftStart, topEnd - topStart);

		org.eclipse.swt.graphics.Image img = new org.eclipse.swt.graphics.Image(a.getDevice(), bounds);
		ImageData imgData = img.getImageData();
		ImageData first = a.getImageData();
		ImageData other = b.getImageData();
		for (int i = 0; i < first.width; i++) {
			for (int j = 0; j < first.height; j++) {
				try {
					int pixel = first.getPixel(i, j);
					int alpha2 = first.getAlpha(i, j);
					imgData.setPixel(i, j, pixel);
					imgData.setAlpha(i, j, alpha2);
				} catch (IllegalArgumentException e) {
					//
				}
			}
		}

		for (int i = 0; i < other.width; i++) {
			for (int j = 0; j < other.height; j++) {
				try {
					int pixel = other.getPixel(i, j);
					int alpha = other.getAlpha(i, j);
					imgData.setPixel(i + offsetX, j + offsetY, pixel);
					imgData.setAlpha(i + offsetX, j + offsetY, alpha);
				} catch (IllegalArgumentException e) {
					//
				}

			}
		}

		return new org.eclipse.swt.graphics.Image(a.getDevice(), imgData);

	}

}
