package zan.lib.app;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_AUTO_ICONIFY;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_FLOATING;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUSED;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class Window {

	public static class Attributes {
		public String title = "";
		public String icon = "";

		public int x = GLFW_DONT_CARE;
		public int y = GLFW_DONT_CARE;

		public int width;
		public int height;

		public boolean fullscreen = false;
		public boolean vsync = true;
		public boolean resizable = true;
		public boolean decorated = true;
		public boolean focused = true;
		public boolean autoiconify = true;
		public boolean floating = false;
		public boolean maximized = false;
		public boolean minimized = false;
		public boolean visible = true;

		public int samples = 0;

		public Attributes(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}

	private final Attributes attrib;

	private int width;
	private int height;

	private long handle;

	private boolean framebufferSizeCallback = false;

	public Window(Attributes attrib) {
		this.attrib = attrib;
	}

	public void init() {
		initHints();
		initWindow();
		initCallbacks();
		initContext();
		initFinish();
	}

	private void initHints() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, attrib.resizable ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_DECORATED, attrib.decorated ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_FOCUSED, attrib.focused ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_AUTO_ICONIFY, attrib.autoiconify ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_FLOATING, attrib.floating ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_MAXIMIZED, attrib.maximized ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_SAMPLES, attrib.samples);
	}

	private void initWindow() {
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode vidmode = glfwGetVideoMode(monitor);

		if (attrib.fullscreen) {
			width = vidmode.width();
			height = vidmode.height();
			handle = glfwCreateWindow(width, height, attrib.title, monitor, NULL);
		} else {
			width = attrib.width;
			height = attrib.height;
			handle = glfwCreateWindow(width, height, attrib.title, NULL, NULL);
		}
		if (handle == NULL) {
			throw new RuntimeException("Failed to create the GLFW window!");
		}

		if (attrib.x == GLFW_DONT_CARE && attrib.y == GLFW_DONT_CARE) {
			attrib.x = (vidmode.width() - attrib.width) / 2;
			attrib.y = (vidmode.height() - attrib.height) / 2;
		}
		glfwSetWindowPos(handle, attrib.x, attrib.y);

		setIcon(attrib.icon);
	}

	private void initCallbacks() {
		glfwSetWindowPosCallback(handle, (window, x, y) -> {
			if (!attrib.fullscreen) {
				attrib.x = x;
				attrib.y = y;
			}
		});
		glfwSetWindowSizeCallback(handle, (window, width, height) -> {
			if (!attrib.fullscreen) {
				attrib.width = width;
				attrib.height = height;
			}
		});
		glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
			this.width = width;
			this.height = height;
			framebufferSizeCallback = true;
		});
	}

	private void initContext() {
		glfwMakeContextCurrent(handle);
		glfwSwapInterval(attrib.vsync ? 1 : 0);
		GL.createCapabilities();
	}

	private void initFinish() {
		if (attrib.minimized) glfwIconifyWindow(handle);
		if (attrib.visible) glfwShowWindow(handle);
	}

	public void refresh() {
		glfwSwapBuffers(handle);
		glfwPollEvents();
	}

	public void exit() {
		glfwFreeCallbacks(handle);
		glfwDestroyWindow(handle);
	}

	public void setTitle(String title) {
		attrib.title = title;
		glfwSetWindowTitle(handle, title);
	}

	public String getTitle() {
		return attrib.title;
	}

	public void setIcon(String icon) {
		attrib.icon = icon;
		if (!icon.isEmpty()) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer w = stack.mallocInt(1);
				IntBuffer h = stack.mallocInt(1);
				IntBuffer c = stack.mallocInt(1);
				ByteBuffer ico = stbi_load(icon, w, h, c, 4);
				glfwSetWindowIcon(handle, GLFWImage.malloc(1, stack).width(w.get(0)).height(h.get(0)).pixels(ico));
				stbi_image_free(ico);
			}
		}
	}

	public String getIcon() {
		return attrib.icon;
	}

	public void setPos(int x, int y) {
		glfwSetWindowPos(handle, x, y);
	}

	public int getX() {
		return attrib.x;
	}

	public int getY() {
		return attrib.y;
	}

	public void setSize(int width, int height) {
		glfwSetWindowSize(handle, width, height);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setFullScreen(boolean fullscreen) {
		attrib.fullscreen = fullscreen;
		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode vidmode = glfwGetVideoMode(monitor);
		if (fullscreen) {
			glfwSetWindowMonitor(handle, monitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
		} else {
			glfwSetWindowMonitor(handle, NULL, attrib.x, attrib.y, attrib.width, attrib.height, vidmode.refreshRate());
		}
		glfwSwapInterval(attrib.vsync ? 1 : 0);
		attrib.fullscreen = (glfwGetWindowMonitor(handle) == monitor);
	}

	public boolean isFullScreen() {
		return attrib.fullscreen;
	}

	public void setVSync(boolean vsync) {
		attrib.vsync = vsync;
		glfwSwapInterval(vsync ? 1 : 0);
	}

	public boolean isVSync() {
		return attrib.vsync;
	}

	public void close() {
		glfwSetWindowShouldClose(handle, true);
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(handle);
	}

	public long getHandle() {
		return handle;
	}

	public boolean onFramebufferSizeCallback() {
		boolean callback = framebufferSizeCallback;
		framebufferSizeCallback = false;
		return callback;
	}

}
