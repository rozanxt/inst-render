package zan.lib.app;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryStack;

public class Input {

	private static class Event {
		public int mods;
		public boolean pressed;
		public boolean released;
		public boolean repeated;

		public void clear() {
			mods = 0;
			pressed = false;
			released = false;
			repeated = false;
		}
	}

	private final Window window;

	private final Event[] keyEvents;
	private final Event[] mouseEvents;
	private final List<Character> charEvents;

	private float mouseX;
	private float mouseY;
	private float mouseDeltaX;
	private float mouseDeltaY;
	private float mouseScrollX;
	private float mouseScrollY;
	private boolean mouseEntered;
	private boolean mouseLeft;

	public Input(Window window) {
		this.window = window;
		keyEvents = new Event[GLFW_KEY_LAST];
		for (int i = 0; i < GLFW_KEY_LAST; i++) {
			keyEvents[i] = new Event();
		}
		mouseEvents = new Event[GLFW_MOUSE_BUTTON_LAST];
		for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
			mouseEvents[i] = new Event();
		}
		charEvents = new ArrayList<Character>();
	}

	public void init() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer x = stack.mallocDouble(1);
			DoubleBuffer y = stack.mallocDouble(1);
			glfwGetCursorPos(window.getHandle(), x, y);
			mouseX = (float) x.get(0);
			mouseY = (float) y.get(0);
		}
		glfwSetKeyCallback(window.getHandle(), (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_UNKNOWN) return;
			Event keyEvent = keyEvents[key];
			keyEvent.mods = mods;
			switch (action) {
			case GLFW_PRESS:
				keyEvent.pressed = true;
				break;
			case GLFW_RELEASE:
				keyEvent.released = true;
				break;
			case GLFW_REPEAT:
				keyEvent.repeated = true;
				break;
			}
		});
		glfwSetMouseButtonCallback(window.getHandle(), (window, button, action, mods) -> {
			if (button == GLFW_KEY_UNKNOWN) return;
			Event mouseEvent = mouseEvents[button];
			mouseEvent.mods = mods;
			switch (action) {
			case GLFW_PRESS:
				mouseEvent.pressed = true;
				break;
			case GLFW_RELEASE:
				mouseEvent.released = true;
				break;
			}
		});
		glfwSetCharCallback(window.getHandle(), (window, ch) -> {
			charEvents.add((char) ch);
		});
		glfwSetCursorPosCallback(window.getHandle(), (window, x, y) -> {
			mouseDeltaX = (float) x - mouseX;
			mouseDeltaY = (float) y - mouseY;
			mouseX = (float) x;
			mouseY = (float) y;
		});
		glfwSetScrollCallback(window.getHandle(), (window, x, y) -> {
			mouseScrollX = (float) x;
			mouseScrollY = (float) y;
		});
		glfwSetCursorEnterCallback(window.getHandle(), (window, entered) -> {
			if (entered) {
				mouseEntered = true;
			} else {
				mouseLeft = true;
			}
		});
	}

	public void clear() {
		for (int i = 0; i < GLFW_KEY_LAST; i++) {
			keyEvents[i].clear();
		}
		for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
			mouseEvents[i].clear();
		}
		charEvents.clear();
		mouseDeltaX = 0.0f;
		mouseDeltaY = 0.0f;
		mouseScrollX = 0.0f;
		mouseScrollY = 0.0f;
		mouseEntered = false;
		mouseLeft = false;
	}

	public void exit() {

	}

	public void setInputMode(int mode, int value) {
		glfwSetInputMode(window.getHandle(), mode, value);
	}

	public float getMouseX() {
		return mouseX;
	}

	public float getMouseY() {
		return mouseY;
	}

	public float getMouseDeltaX() {
		return mouseDeltaX;
	}

	public float getMouseDeltaY() {
		return mouseDeltaY;
	}

	public float getMouseScrollX() {
		return mouseScrollX;
	}

	public float getMouseScrollY() {
		return mouseScrollY;
	}

	public boolean isKeyMods(int key, int mods) {
		return (keyEvents[key].mods == mods);
	}

	public boolean isKeyPressed(int key) {
		return keyEvents[key].pressed;
	}

	public boolean isKeyReleased(int key) {
		return keyEvents[key].released;
	}

	public boolean isKeyRepeated(int key) {
		return keyEvents[key].repeated;
	}

	public boolean isKeyDown(int key) {
		return (glfwGetKey(window.getHandle(), key) == GLFW_PRESS);
	}

	public boolean isMouseMods(int button, int mods) {
		return (mouseEvents[button].mods == mods);
	}

	public boolean isMousePressed(int button) {
		return mouseEvents[button].pressed;
	}

	public boolean isMouseReleased(int button) {
		return mouseEvents[button].released;
	}

	public boolean isMouseDown(int button) {
		return (glfwGetMouseButton(window.getHandle(), button) == GLFW_PRESS);
	}

	public boolean hasMouseEntered() {
		return mouseEntered;
	}

	public boolean hasMouseLeft() {
		return mouseLeft;
	}

	public boolean hasCharEvent() {
		return !charEvents.isEmpty();
	}

	public char getCharEvent() {
		return charEvents.isEmpty() ? 0 : charEvents.remove(0);
	}

}
