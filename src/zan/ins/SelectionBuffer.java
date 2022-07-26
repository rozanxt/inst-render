package zan.ins;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGBA8UI;
import static org.lwjgl.opengl.GL30.GL_RGBA_INTEGER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

import java.nio.ByteBuffer;

import org.joml.Vector4i;
import org.lwjgl.system.MemoryUtil;

public class SelectionBuffer {

	private final int fbo;
	private final int rbo;
	private final int tex;

	private final int width;
	private final int height;

	public SelectionBuffer(int width, int height) {
		this.width = width;
		this.height = height;

		fbo = glGenFramebuffers();
		rbo = glGenRenderbuffers();
		tex = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, tex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8UI, width, height, 0, GL_RGBA_INTEGER, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glBindTexture(GL_TEXTURE_2D, 0);

		glBindRenderbuffer(GL_RENDERBUFFER, rbo);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
		glBindRenderbuffer(GL_RENDERBUFFER, 0);

		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex, 0);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void delete() {
		glDeleteTextures(tex);
		glDeleteRenderbuffers(rbo);
		glDeleteFramebuffers(fbo);
	}

	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glReadBuffer(GL_COLOR_ATTACHMENT0);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);
	}

	public void unbind() {
		glReadBuffer(GL_NONE);
		glDrawBuffer(GL_NONE);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public Vector4i pick(float x, float y) {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glReadBuffer(GL_COLOR_ATTACHMENT0);

		ByteBuffer buffer = MemoryUtil.memAlloc(4*width*height);
		glReadPixels((int) x, (int) (height-y), 1, 1, GL_RGBA_INTEGER, GL_UNSIGNED_INT, buffer);
		Vector4i data = new Vector4i(buffer);
		MemoryUtil.memFree(buffer);

		glReadBuffer(GL_NONE);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		// hack to prevent undefined values when pressing alt+tab
		for (int i = 0; i < 4; i++) {
			if (data.get(i) < 0 || data.get(i) > 255) {
				data.zero();
				break;
			}
		}

		return data;
	}

}
