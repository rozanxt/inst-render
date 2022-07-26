package zan.lib.gfx;

import java.util.ArrayList;
import java.util.List;

import zan.lib.utl.TypeConverter;

public class TextItem {

	private static class TextBuild {
		public final float[] pos;
		public final float[] tex;
		public final int[] ind;
		public final int lines;
		public final float offset;
		public final float width;

		public TextBuild(String text, TextFont font, float maxWidth) {
			List<Float> pos = new ArrayList<>();
			List<Float> tex = new ArrayList<>();
			List<Integer> ind = new ArrayList<>();
			char[] chars = text.toCharArray();

			int line = 0;
			float offset = 0.0f;
			float width = 0.0f;
			int fontWidth = font.getTexture().getWidth();
			int fontHeight = font.getTexture().getHeight();
			for (int i = 0; i < chars.length; i++) {
				TextFont.CharInfo data = font.getCharInfo(chars[i]);

				pos.add(offset);
				pos.add((float) -line * fontHeight);
				tex.add((float) data.x / (float) fontWidth);
				tex.add(1.0f);

				pos.add(offset + data.w);
				pos.add((float) -line * fontHeight);
				tex.add((float) (data.x + data.w) / (float) fontWidth);
				tex.add(1.0f);

				pos.add(offset + data.w);
				pos.add((float) (1 - line) * fontHeight);
				tex.add((float) (data.x + data.w) / (float) fontWidth);
				tex.add(0.0f);

				pos.add(offset);
				pos.add((float) (1 - line) * fontHeight);
				tex.add((float) data.x / (float) fontWidth);
				tex.add(0.0f);

				ind.add(4 * i);
				ind.add(4 * i + 1);
				ind.add(4 * i + 2);
				ind.add(4 * i + 3);
				ind.add(4 * i);
				ind.add(4 * i + 2);

				offset += data.w;

				if (offset > width) {
					width = offset;
				}
				if (chars[i] == '\n' || (maxWidth > 0 && offset > maxWidth && chars[i] == ' ')) {
					offset = 0.0f;
					line++;
				}
			}

			this.pos = TypeConverter.FloatListToArray(pos);
			this.tex = TypeConverter.FloatListToArray(tex);
			this.ind = TypeConverter.IntegerListToArray(ind);
			this.lines = line + 1;
			this.offset = offset;
			this.width = width;
		}
	}

	private String text;
	private TextFont font;

	private int lines;
	private float offset;
	private float width;
	private float maxWidth;

	private final Mesh2D mesh;

	public TextItem(String text, TextFont font, float maxWidth) {
		TextBuild build = new TextBuild(text, font, maxWidth);
		this.text = text;
		this.font = font;
		this.maxWidth = maxWidth;
		lines = build.lines;
		offset = build.offset;
		width = build.width;
		mesh = new Mesh2D(build.pos, build.tex, build.ind);
	}

	public TextItem(String text, TextFont font) {
		this(text, font, 0);
	}

	public TextItem(TextFont font, float maxWidth) {
		this("", font, maxWidth);
	}

	public TextItem(TextFont font) {
		this("", font);
	}

	public void delete() {
		mesh.delete();
	}

	public void update() {
		TextBuild build = new TextBuild(text, font, maxWidth);
		lines = build.lines + 1;
		offset = build.offset;
		width = build.width;
		mesh.bind();
		mesh.setVertexData(Mesh2D.POS, build.pos);
		mesh.setVertexData(Mesh2D.TEX, build.tex);
		mesh.setIndexData(Mesh2D.IND, build.ind);
		mesh.setNumElements(build.ind.length);
		mesh.unbind();
	}

	public void render() {
		font.getTexture().bind();
		mesh.bind();
		mesh.draw();
		mesh.unbind();
		font.getTexture().bind();
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setFont(TextFont font) {
		this.font = font;
	}

	public TextFont getFont() {
		return font;
	}

	public int getNumLines() {
		return lines;
	}

	public float getOffset() {
		return offset;
	}

	public float getWidth() {
		return width;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public float getMaxWidth() {
		return maxWidth;
	}

}
