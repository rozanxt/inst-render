package zan.ins;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F10;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import zan.lib.app.Input;
import zan.lib.app.Window;

public class TopDownCamera implements Camera {

	private static class State {

		private Vector3f target = new Vector3f();
		private float distance = 0.0f;
		private float altitude = 60.0f;
		private float azimuth = 0.0f;

		private void set(State state) {
			target.set(state.target);
			distance = state.distance;
			altitude = state.altitude;
			azimuth = state.azimuth;
		}

	}

	private Window window;
	private Input input;

	private float width;
	private float height;

	private State prevState = new State();
	private State nextState = new State();

	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();

	private int zoomLevel = 2;
	private int zoomLevels = 10;
	private float zoomMinimum = 20.0f;
	private float zoomMaximum = 800.0f;
	private float[] zoomValues = new float[zoomLevels];

	private Vector3f scrollVelocity = new Vector3f(0.0f, 0.0f, 0.0f);
	private float scrollBaseMaxSpeed = 0.5f;
	private float scrollBaseAcceleration = 0.05f;
	private float scrollAttenuation = 0.5f;
	private float scrollAreaSize = 2.0f;

	private float fovy = 10.0f;
	private float near = 0.01f;
	private float far = 1000.0f;

	private int[] cameraHotkeyNames = {GLFW_KEY_F1, GLFW_KEY_F2, GLFW_KEY_F3, GLFW_KEY_F4, GLFW_KEY_F5, GLFW_KEY_F6, GLFW_KEY_F7, GLFW_KEY_F8, GLFW_KEY_F9, GLFW_KEY_F10};
	private int numCameraHotkeys = cameraHotkeyNames.length;
	private Vector3f[] cameraHotkeys = new Vector3f[numCameraHotkeys];

	public TopDownCamera(Window window, Input input) {
		this.window = window;
		this.input = input;
		for (int i = 0; i < zoomLevels; i++) {
			zoomValues[i] = (float) (zoomMinimum*Math.exp(Math.log(zoomMaximum/zoomMinimum)*i/(zoomLevels-1)));
		}
		prevState.distance = zoomValues[zoomLevel];
		nextState.distance = zoomValues[zoomLevel];
	}

	@Override
	public void update(float theta) {
		prevState.set(nextState);

		float scrollMaxSpeed = scrollBaseMaxSpeed*zoomValues[zoomLevel]/zoomMinimum;
		float scrollAcceleration = scrollBaseAcceleration*zoomValues[zoomLevel]/zoomMinimum;

		if (Math.abs(input.getMouseX()) < scrollAreaSize || input.isKeyDown(GLFW_KEY_LEFT)) {
			scrollVelocity.add(-scrollAcceleration, 0.0f, 0.0f);
		}
		if (Math.abs(input.getMouseX()-window.getWidth()) < scrollAreaSize || input.isKeyDown(GLFW_KEY_RIGHT)) {
			scrollVelocity.add(scrollAcceleration, 0.0f, 0.0f);
		}
		if (Math.abs(input.getMouseY()) < scrollAreaSize || input.isKeyDown(GLFW_KEY_UP)) {
			scrollVelocity.add(0.0f, 0.0f, -scrollAcceleration);
		}
		if (Math.abs(input.getMouseY()-window.getHeight()) < scrollAreaSize || input.isKeyDown(GLFW_KEY_DOWN)) {
			scrollVelocity.add(0.0f, 0.0f, scrollAcceleration);
		}

		scrollVelocity.x = Math.min(Math.max(scrollVelocity.x, -scrollMaxSpeed), scrollMaxSpeed);
		scrollVelocity.z = Math.min(Math.max(scrollVelocity.z, -scrollMaxSpeed), scrollMaxSpeed);
		nextState.target.add(scrollVelocity);
		scrollVelocity.mul(scrollAttenuation);
		if (scrollVelocity.length() < scrollAttenuation*scrollAcceleration) {
			scrollVelocity.zero();
		}

		zoomLevel -= (int) input.getMouseScrollY();
		zoomLevel = Math.min(Math.max(zoomLevel, 0), zoomLevels-1);

		if (Math.abs(zoomValues[zoomLevel]-nextState.distance) >= scrollAttenuation*scrollAcceleration) {
			nextState.distance += scrollAttenuation*(zoomValues[zoomLevel]-nextState.distance);
		} else {
			nextState.distance = zoomValues[zoomLevel];
		}

		for (int i = 0; i < numCameraHotkeys; i++) {
			if (input.isKeyMods(cameraHotkeyNames[i], GLFW_MOD_CONTROL)) {
				if (input.isKeyPressed(cameraHotkeyNames[i])) {
					if (cameraHotkeys[i] == null) {
						cameraHotkeys[i] = new Vector3f(nextState.target);
					} else {
						cameraHotkeys[i].set(nextState.target);
					}
				}
			} else if (input.isKeyDown(cameraHotkeyNames[i])) {
				if (cameraHotkeys[i] != null) {
					nextState.target.set(cameraHotkeys[i]);
				}
			}
		}
	}

	@Override
	public void capture(float theta) {
		if (width != window.getWidth() || height != window.getHeight()) {
			width = window.getWidth();
			height = window.getHeight();
			projectionMatrix.setPerspective((float) Math.toRadians(fovy), width/height, near, far);
		}

		Vector3f target = new Vector3f();
		prevState.target.lerp(nextState.target, theta, target);
		float distance = prevState.distance+theta*(nextState.distance-prevState.distance);
		float altitude = prevState.altitude+theta*(nextState.altitude-prevState.altitude);
		float azimuth = prevState.azimuth+theta*(nextState.azimuth-prevState.azimuth);

		viewMatrix.identity()
			.translate(0.0f, 0.0f, -distance)
			.rotateX((float) Math.toRadians(altitude))
			.rotateY((float) Math.toRadians(azimuth))
			.translate(target.negate());
	}

	@Override
	public void setTarget(Vector3fc target) {
		nextState.target.set(target);
	}

	@Override
	public float getNearClipDistance() {
		return near;
	}

	@Override
	public Matrix4fc getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4fc getViewMatrix() {
		return viewMatrix;
	}

}
