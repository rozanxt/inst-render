package zan.lib.utl;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.system.MemoryUtil;

public final class TypeConverter {

	private TypeConverter() {

	}

	public static int[] IntegerListToArray(List<Integer> list) {
		int size = (list != null) ? list.size() : 0;
		int[] array = new int[size];
		for (int i = 0; i < size; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static float[] FloatListToArray(List<Float> list) {
		int size = (list != null) ? list.size() : 0;
		float[] array = new float[size];
		for (int i = 0; i < size; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static ByteBuffer BufferedImageToByteBuffer(BufferedImage image) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		ByteBuffer buffer = MemoryUtil.memAlloc(image.getWidth() * image.getHeight() * 4);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[image.getWidth() * y + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		buffer.flip();
		return buffer;
	}

}
