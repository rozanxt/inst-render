package zan.lib.gfx;

public class Mesh2D extends Mesh {

	public static final int POS = 0;
	public static final int TEX = 1;
	public static final int IND = 2;

	public Mesh2D(float[] positions, float[] texcoords, int[] indices) {
		super(3, indices.length);
		bind();
		setVertexData(POS, positions);
		setVertexAttrib(POS, 2, 0, 0);
		setVertexData(TEX, texcoords);
		setVertexAttrib(TEX, 2, 0, 0);
		setIndexData(IND, indices);
		unbind();
	}

	public Mesh2D() {
		this(new float[]{0}, new float[]{0}, new int[]{0});
	}

}
