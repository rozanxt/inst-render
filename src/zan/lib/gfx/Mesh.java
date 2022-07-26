package zan.lib.gfx;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

public class Mesh {

	private static final int BYTES_PER_INT = 4;
	private static final int BYTES_PER_FLOAT = 4;

	private final int vao;
	private final int[] vbo;

	private int numElements;

	public Mesh(int numVBO, int numElements) {
		vao = glGenVertexArrays();
		vbo = new int[numVBO];
		for (int i = 0; i < vbo.length; i++) {
			vbo[i] = glGenBuffers();
		}
		this.numElements = numElements;
	}

	public void delete() {
		for (int i = 0; i < vbo.length; i++) {
			glDeleteBuffers(vbo[i]);
		}
		glDeleteVertexArrays(vao);
	}

	public void bind() {
		glBindVertexArray(vao);
	}

	public void unbind() {
		glBindVertexArray(0);
	}

	public void draw(int mode, int count, int offset) {
		glDrawElements(mode, count, GL_UNSIGNED_INT, offset * BYTES_PER_INT);
	}

	public void draw() {
		draw(GL_TRIANGLES, numElements, 0);
	}

	public void setVertexData(int index, float[] data) {
		FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
		buffer.put(data).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo[index]);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		MemoryUtil.memFree(buffer);
	}

	public void setIndexData(int index, int[] data) {
		IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
		buffer.put(data).flip();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[index]);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		MemoryUtil.memFree(buffer);
	}

	public void setVertexAttrib(int index, int size, int stride, int offset) {
		glEnableVertexAttribArray(index);
		glVertexAttribPointer(index, size, GL_FLOAT, false, stride * BYTES_PER_FLOAT, offset * BYTES_PER_FLOAT);
	}

	public void setVertexAttribDivisor(int index, int divisor) {
		glVertexAttribDivisor(index, divisor);
	}

	public int getVAO() {
		return vao;
	}

	public int getVBO(int index) {
		return vbo[index];
	}

	public void setNumElements(int num) {
		numElements = num;
	}

	public int getNumElements() {
		return numElements;
	}

}
