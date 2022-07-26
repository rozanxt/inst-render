package zan.lib.gfx;

public class Mesh3D extends Mesh {

	public static final int POS = 0;
	public static final int TEX = 1;
	public static final int NRM = 2;
	public static final int IND = 3;

	public Mesh3D(float[] positions, float[] texcoords, float[] normals, int[] indices) {
		super(4, indices.length);
		bind();
		setVertexData(POS, positions);
		setVertexAttrib(POS, 3, 0, 0);
		setVertexData(TEX, texcoords);
		setVertexAttrib(TEX, 2, 0, 0);
		setVertexData(NRM, normals);
		setVertexAttrib(NRM, 3, 0, 0);
		setIndexData(IND, indices);
		unbind();
	}

	public Mesh3D() {
		this(new float[]{0}, new float[]{0}, new float[]{0}, new int[]{0});
	}

}
