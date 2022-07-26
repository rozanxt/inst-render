package zan.ins;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import zan.lib.gfx.Mesh;

public class MeshRenderer {

	private static final int LOC = 3;
	private static final int IDS = 8;

	public final Mesh mesh;

	private final int ibo;
	private final int pbo;

	private final List<Matrix4f> modelMatrices = new ArrayList<>();
	private final List<Vector4f> modelColors = new ArrayList<>();
	private final List<Integer> modelIDs = new ArrayList<>();

	private int numInstances = 0;

	public MeshRenderer(Mesh mesh) {
		this.mesh = mesh;

		ibo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, ibo);
		mesh.bind();
		for (int i = 0; i < 5; i++) {
			mesh.setVertexAttrib(LOC+i, 4, 20, 4*i);
			mesh.setVertexAttribDivisor(LOC+i, 1);
		}
		mesh.unbind();
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		pbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, pbo);
		mesh.bind();
		glEnableVertexAttribArray(IDS);
		glVertexAttribIPointer(IDS, 4, GL_UNSIGNED_INT, 0, 0);
		glVertexAttribDivisor(IDS, 1);
		mesh.unbind();
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public void delete() {
		glDeleteBuffers(ibo);
		glDeleteBuffers(pbo);
		mesh.delete();
		modelMatrices.clear();
		modelColors.clear();
		modelIDs.clear();
	}

	public void add(Matrix4f trafo, Vector4f color, int id) {
		modelMatrices.add(trafo);
		modelColors.add(color);
		modelIDs.add(id);
	}

	public void update() {
		float[] data = new float[20*modelMatrices.size()];
		for (int i = 0; i < modelMatrices.size(); i++) {
			float[] temp = new float[16];
			modelMatrices.get(i).get(temp);
			for (int j = 0; j < 16; j++) {
				data[20*i+j] = temp[j];
			}
			for (int j = 0; j < 4; j++) {
				data[20*i+j+16] = modelColors.get(i).get(j);
			}
		}

		FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
		buffer.put(data).flip();
		glBindBuffer(GL_ARRAY_BUFFER, ibo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		MemoryUtil.memFree(buffer);

		int[] data2 = new int[4*modelIDs.size()];
		for (int i = 0; i < modelIDs.size(); i++) {
			int id = modelIDs.get(i);
			for (int j = 0; j < 4; j++) {
				data2[4*i+j] = id % 255;
				id /= 255;
			}
		}

		IntBuffer buffer2 = MemoryUtil.memAllocInt(data.length);
		buffer2.put(data2).flip();
		glBindBuffer(GL_ARRAY_BUFFER, pbo);
		glBufferData(GL_ARRAY_BUFFER, buffer2, GL_STATIC_DRAW);
		MemoryUtil.memFree(buffer2);

		numInstances = modelMatrices.size();
		modelMatrices.clear();
		modelColors.clear();
		modelIDs.clear();
	}

	public void render() {
		mesh.bind();
		glDrawElementsInstanced(GL_TRIANGLES, mesh.getNumElements(), GL_UNSIGNED_INT, 0, numInstances);
		mesh.unbind();
	}

}
