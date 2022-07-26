package zan.lib.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

import zan.lib.utl.TypeConverter;

public class TextFont {

	public static class CharInfo {
		public final int x;
		public final int w;

		public CharInfo(int x, int w) {
			this.x = x;
			this.w = w;
		}
	}

	private final Map<Character, CharInfo> fontInfo;

	private final Texture fontTexture;

	public TextFont(Font font, String charset) {
		CharsetEncoder ce = Charset.forName(charset).newEncoder();
		StringBuilder mc = new StringBuilder();
		StringBuilder tc = new StringBuilder();
		for (char ch = 0; ch < Character.MAX_VALUE; ch++) {
			if (ce.canEncode(ch)) {
				mc.append(ch);
				tc.append(ch).append(' ');
			}
		}
		String metricsChars = mc.toString();
		String textureChars = tc.toString();

		fontInfo = new HashMap<>();
		int width = 0;
		int height = 0;
		int padding = 0;
		if (font.isItalic()) padding = 1;

		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
		for (char ch : metricsChars.toCharArray()) {
			CharInfo ci = new CharInfo(width, fm.charWidth(ch) + padding);
			fontInfo.put(ch, ci);
			width += fm.charWidth(ch) + fm.charWidth(' ');
			height = Math.max(height, fm.getHeight());
		}
		g2d.dispose();

		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = bi.createGraphics();
		g2d.setFont(font);
		fm = g2d.getFontMetrics();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.drawString(textureChars, 0, fm.getAscent());
		g2d.dispose();

		ByteBuffer buffer = TypeConverter.BufferedImageToByteBuffer(bi);
		fontTexture = new Texture(buffer, bi.getWidth(), bi.getHeight());
		MemoryUtil.memFree(buffer);
	}

	public TextFont(Font font) {
		this(font, "ISO-8859-1");
	}

	public void delete() {
		fontTexture.delete();
	}

	public CharInfo getCharInfo(char ch) {
		return fontInfo.get(ch);
	}

	public Texture getTexture() {
		return fontTexture;
	}

}
