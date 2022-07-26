package zan.lib.gfx;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform3i;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector2ic;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4fc;
import org.joml.Vector4ic;
import org.lwjgl.system.MemoryStack;

import zan.lib.utl.TextResource;

public class Shader {

	private final Map<String, Integer> uniforms;

	private final int program;

	public Shader(String vertexSource, String fragmentSource) {
		uniforms = new HashMap<>();
		program = glCreateProgram();
		createProgram(vertexSource, fragmentSource);
	}

	public static Shader loadFromFile(String vertexPath, String fragmentPath) {
		return new Shader(TextResource.loadFromFileAsString(vertexPath), TextResource.loadFromFileAsString(fragmentPath));
	}

	public void delete() {
		glDeleteProgram(program);
	}

	public void bind() {
		glUseProgram(program);
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void addUniform(String uniform) {
		int location = glGetUniformLocation(program, uniform);
		if (location < 0) {
			System.err.println("Unable to find uniform '" + uniform + "'!");
		}
		uniforms.put(uniform, location);
	}

	public void setUniform(String uniform, boolean value) {
		glUniform1i(getUniform(uniform), value ? GL_TRUE : GL_FALSE);
	}

	public void setUniform(String uniform, int value) {
		glUniform1i(getUniform(uniform), value);
	}

	public void setUniform(String uniform, Vector2ic value) {
		glUniform2i(getUniform(uniform), value.x(), value.y());
	}

	public void setUniform(String uniform, Vector3ic value) {
		glUniform3i(getUniform(uniform), value.x(), value.y(), value.z());
	}

	public void setUniform(String uniform, Vector4ic value) {
		glUniform4i(getUniform(uniform), value.x(), value.y(), value.z(), value.w());
	}

	public void setUniform(String uniform, float value) {
		glUniform1f(getUniform(uniform), value);
	}

	public void setUniform(String uniform, Vector2fc value) {
		glUniform2f(getUniform(uniform), value.x(), value.y());
	}

	public void setUniform(String uniform, Vector3fc value) {
		glUniform3f(getUniform(uniform), value.x(), value.y(), value.z());
	}

	public void setUniform(String uniform, Vector4fc value) {
		glUniform4f(getUniform(uniform), value.x(), value.y(), value.z(), value.w());
	}

	public void setUniform(String uniform, Matrix4fc value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer buffer = stack.mallocFloat(16);
			value.get(buffer);
			glUniformMatrix4fv(getUniform(uniform), false, buffer);
		}
	}

	public int getUniform(String uniform) {
		return uniforms.get(uniform);
	}

	private void createProgram(String vertexSource, String fragmentSource) {
		int vertexShader = createShader(vertexSource, GL_VERTEX_SHADER);
		int fragmentShader = createShader(fragmentSource, GL_FRAGMENT_SHADER);
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println("Unable to link shader program " + program + ":\n" + glGetProgramInfoLog(program));
		}
		deleteShader(vertexShader);
		deleteShader(fragmentShader);
	}

	private int createShader(String source, int type) {
		int shader = glCreateShader(type);
		glShaderSource(shader, source);
		glCompileShader(shader);
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Unable to compile shader " + shader + " of type " + type + ":\n" + glGetShaderInfoLog(shader));
		}
		glAttachShader(program, shader);
		return shader;
	}

	private void deleteShader(int shader) {
		glDetachShader(program, shader);
		glDeleteShader(shader);
	}

}
