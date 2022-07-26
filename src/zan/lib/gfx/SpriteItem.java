package zan.lib.gfx;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

import java.util.ArrayList;
import java.util.List;

import zan.lib.utl.TypeConverter;

public class SpriteItem {

	private Texture sheet;

	private Mesh2D mesh;

	private float width;
	private float height;

	private int cols;
	private int rows;

	public SpriteItem(Texture sheet, int cols, int rows) {
		this.sheet = sheet;
		this.cols = cols;
		this.rows = rows;
		List<Float> positions = new ArrayList<>();
		List<Float> texcoords = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		float sheetWidth = sheet.getWidth();
		float sheetHeight = sheet.getHeight();
		width = sheetWidth / cols;
		height = sheetHeight / rows;
		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < cols; i++) {
				positions.add(0.0f);
				positions.add(0.0f);
				texcoords.add(i * width / sheetWidth);
				texcoords.add((j + 1) * height / sheetHeight);

				positions.add(width);
				positions.add(0.0f);
				texcoords.add((i + 1) * width / sheetWidth);
				texcoords.add((j + 1) * height / sheetHeight);

				positions.add(width);
				positions.add(height);
				texcoords.add((i + 1) * width / sheetWidth);
				texcoords.add(j * height / sheetHeight);

				positions.add(0.0f);
				positions.add(height);
				texcoords.add(i * width / sheetWidth);
				texcoords.add(j * height / sheetHeight);

				indices.add(4 * cols * j + 4 * i + 0);
				indices.add(4 * cols * j + 4 * i + 1);
				indices.add(4 * cols * j + 4 * i + 2);
				indices.add(4 * cols * j + 4 * i + 3);
				indices.add(4 * cols * j + 4 * i + 0);
				indices.add(4 * cols * j + 4 * i + 2);
			}
		}
		mesh = new Mesh2D(TypeConverter.FloatListToArray(positions), TypeConverter.FloatListToArray(texcoords), TypeConverter.IntegerListToArray(indices));
	}

	public SpriteItem(Texture sheet) {
		this(sheet, 1, 1);
	}

	public void delete() {
		mesh.delete();
	}

	public void render(int frame) {
		sheet.bind();
		mesh.bind();
		mesh.draw(GL_TRIANGLES, 6, 6 * frame);
		mesh.unbind();
		sheet.unbind();
	}

	public void render() {
		render(0);
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

}
