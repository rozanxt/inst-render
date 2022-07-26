package zan.ins;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4i;

import zan.lib.app.Input;
import zan.lib.app.Window;
import zan.lib.gfx.Mesh;
import zan.lib.gfx.Shader;

public class SelectionManager {

	private Window window;
	private Input input;

	private Camera camera;

	private List<Instance> instances;

	private Vector4i selectionData;
	private int selectionID;

	private List<Instance> selected;

	private Mesh selectionBox;

	private int[] hotkeyNames;
	private int numHotkeys;

	private List<List<Instance>> hotkeys;

	private int hotkeyDelay;
	private int[] hotkeyTimer;

	private Vector2f tempCursorPos;
	private float dragTolerance;
	private boolean dragging;

	private SelectionBuffer frameBuffer;

	public SelectionManager(Window window, Input input, Camera camera, List<Instance> instances, Mesh selectionBox) {
		this.window = window;
		this.input = input;
		this.camera = camera;
		this.instances = instances;
		this.selectionBox = selectionBox;

		selectionData = new Vector4i();
		selectionID = 0;

		selected = new ArrayList<>();

		hotkeyNames = new int[] {GLFW_KEY_1, GLFW_KEY_2, GLFW_KEY_3, GLFW_KEY_4, GLFW_KEY_5, GLFW_KEY_6, GLFW_KEY_7, GLFW_KEY_8, GLFW_KEY_9};

		numHotkeys = hotkeyNames.length;

		hotkeys = new ArrayList<>();
		for (int i = 0; i < numHotkeys; i++) hotkeys.add(new ArrayList<>());

		hotkeyTimer = new int[numHotkeys];
		for (int i = 0; i < numHotkeys; i++) hotkeyTimer[i] = 0;

		hotkeyDelay = 10;

		tempCursorPos = new Vector2f();
		dragTolerance = 10.0f;
		dragging = false;

		frameBuffer = new SelectionBuffer(window.getWidth(), window.getHeight());
	}

	public void delete() {
		frameBuffer.delete();
	}

	public void update(float theta) {
		for (int i = 0; i < instances.size(); i++) instances.get(i).hovered = false;

		if (input.isMousePressed(GLFW_MOUSE_BUTTON_1)) {
			tempCursorPos.set(input.getMouseX(), input.getMouseY());
		}

		if (input.isMouseDown(GLFW_MOUSE_BUTTON_1)) {
			if (tempCursorPos.distance(input.getMouseX(), input.getMouseY()) > dragTolerance) {
				dragging = true;
				for (int i = 0; i < instances.size(); i++) {
					Instance instance = instances.get(i);
					Vector4f ndc = new Matrix4f(camera.getProjectionMatrix()).mulAffine(camera.getViewMatrix()).transform(new Vector4f(instance.position, 1.0f));
					float x = (ndc.x/ndc.z+1)*window.getWidth()/2.0f;
					float y = window.getHeight()-(ndc.y/ndc.z+1)*window.getHeight()/2.0f;
					float l = Math.min(tempCursorPos.x, input.getMouseX());
					float r = Math.max(tempCursorPos.x, input.getMouseX());
					float b = Math.min(tempCursorPos.y, input.getMouseY());
					float t = Math.max(tempCursorPos.y, input.getMouseY());
					if (x > l && x < r && y > b && y < t) {
						instance.hovered = true;
					}
				}
			}
		}

		if (input.isMouseReleased(GLFW_MOUSE_BUTTON_1)) {
			if (!input.isMouseMods(GLFW_MOUSE_BUTTON_1, GLFW_MOD_SHIFT)) {
				for (int i = 0; i < instances.size(); i++) instances.get(i).selected = false;
				selected.clear();
			}
			if (dragging) {
				dragging = false;
				for (int i = 0; i < instances.size(); i++) {
					Instance instance = instances.get(i);
					Vector4f ndc = new Matrix4f(camera.getProjectionMatrix()).mulAffine(camera.getViewMatrix()).transform(new Vector4f(instance.position, 1.0f));
					float x = (ndc.x/ndc.z+1)*window.getWidth()/2.0f;
					float y = window.getHeight()-(ndc.y/ndc.z+1)*window.getHeight()/2.0f;
					float l = Math.min(tempCursorPos.x, input.getMouseX());
					float r = Math.max(tempCursorPos.x, input.getMouseX());
					float b = Math.min(tempCursorPos.y, input.getMouseY());
					float t = Math.max(tempCursorPos.y, input.getMouseY());
					if (x > l && x < r && y > b && y < t) {
						boolean contained = selected.contains(instance);
						if (input.isMouseMods(GLFW_MOUSE_BUTTON_1, GLFW_MOD_SHIFT) && contained) {
							instance.selected = false;
							selected.remove(instance);
						} else if (!contained){
							instance.selected = true;
							selected.add(instance);
						}
					}
				}
			} else {
				if (selectionID != 0) {
					Instance instance = instances.get(selectionID-1);
					boolean contained = selected.contains(instance);
					if (input.isMouseMods(GLFW_MOUSE_BUTTON_1, GLFW_MOD_SHIFT) && contained) {
						instance.selected = false;
						selected.remove(instance);
					} else if (!contained) {
						instance.selected = true;
						selected.add(instance);
					}
				}
			}
			tempCursorPos.set(input.getMouseX(), input.getMouseY());
		}

		if (!dragging && selectionID != 0) {
			instances.get(selectionID-1).hovered = true;
		}

		for (int i = 0; i < numHotkeys; i++) {
			if (input.isKeyPressed(hotkeyNames[i])) {
				if (input.isKeyMods(hotkeyNames[i], GLFW_MOD_CONTROL)) {
					for (int j = 0; j < instances.size(); j++) instances.get(j).groups[i] = false;
					hotkeys.get(i).clear();
					hotkeys.get(i).addAll(selected);
					for (int j = 0; j < selected.size(); j++) selected.get(j).groups[i] = true;
				} else if (input.isKeyMods(hotkeyNames[i], GLFW_MOD_SHIFT)) {
					hotkeys.get(i).addAll(selected);
					for (int j = 0; j < selected.size(); j++) selected.get(j).groups[i] = true;
				} else if (hotkeyTimer[i] > 0) {
					if (!hotkeys.get(i).isEmpty()) {
						Vector3f center = new Vector3f();
						for (int j = 0; j < hotkeys.get(i).size(); j++) {
							center.add(hotkeys.get(i).get(j).position);
						}
						center.div(hotkeys.get(i).size());
						camera.setTarget(center);
					}
				} else {
					hotkeyTimer[i] = hotkeyDelay;
					for (int j = 0; j < instances.size(); j++) instances.get(j).selected = false;
					selected.clear();
					selected.addAll(hotkeys.get(i));
					for (int j = 0; j < selected.size(); j++) selected.get(j).selected = true;
				}
			}
		}

		if (input.isKeyPressed(GLFW_KEY_0)) {
			for (int i = 0; i < selected.size(); i++) {
				for (int j = 0; j < numHotkeys; j++) {
					hotkeys.get(j).remove(selected.get(i));
					selected.get(i).groups[j] = false;
				}
			}
		}

		for (int i = 0; i < numHotkeys; i++) {
			if (hotkeyTimer[i] > 0) {
				hotkeyTimer[i]--;
			}
		}

		if (input.isKeyDown(GLFW_KEY_SPACE)) {
			if (!selected.isEmpty()) {
				Vector3f center = new Vector3f();
				for (int i = 0; i < selected.size(); i++) {
					center.add(selected.get(i).position);
				}
				center.div(selected.size());
				camera.setTarget(center);
			}
		}
	}

	public void render(Shader shader) {
		if (dragging) {
			shader.bind();
			shader.setUniform("projectionMatrix", new Matrix4f().setOrtho(0.0f, window.getWidth(), 0.0f, window.getHeight(), -1.0f, 1.0f));
			shader.setUniform("modelViewMatrix", new Matrix4f().translate(tempCursorPos.x, window.getHeight()-tempCursorPos.y, 0.0f).scale(input.getMouseX()-tempCursorPos.x, tempCursorPos.y-input.getMouseY(), 0.0f));
			shader.setUniform("uniformColor", new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
			selectionBox.bind();
			selectionBox.draw(GL_LINE_LOOP, 4, 0);
			selectionBox.unbind();
			shader.unbind();
		}
	}

	public void buffer(Shader shader, Map<String, MeshRenderer> meshes) {
		if (window.onFramebufferSizeCallback()) {
			frameBuffer.delete();
			frameBuffer = new SelectionBuffer(window.getWidth(), window.getHeight());
		}

		frameBuffer.bind();

		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);

		shader.bind();
		shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
		shader.setUniform("viewMatrix", camera.getViewMatrix());
		for (String key : meshes.keySet()) {
			meshes.get(key).render();
		}
		shader.unbind();

		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		frameBuffer.unbind();

		selectionData.set(frameBuffer.pick(input.getMouseX(), input.getMouseY()));

		selectionID = selectionData.x+255*selectionData.y+255*255*selectionData.z+255*255*255*selectionData.w;
	}

	public int getNumHotkeys() {
		return numHotkeys;
	}

	public List<Instance> getHotkeyedInstances(int i) {
		return hotkeys.get(i);
	}

	public boolean isDragging() {
		return dragging;
	}

}
