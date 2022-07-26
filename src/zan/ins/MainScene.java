package zan.ins;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector4f;

import zan.lib.app.Engine;
import zan.lib.app.Input;
import zan.lib.app.Scene;
import zan.lib.app.Window;
import zan.lib.gfx.Mesh;
import zan.lib.gfx.Mesh2D;
import zan.lib.gfx.OBJLoader;
import zan.lib.gfx.Shader;
import zan.lib.gfx.TextFont;
import zan.lib.gfx.TextItem;

public class MainScene implements Scene {

	private Engine engine;

	private Map<String, Shader> shaders;
	private Map<String, MeshRenderer> meshes;

	private Mesh square;

	private TextFont font;
	private TextItem text;

	private Camera camera;

	private List<Instance> instances;

	private SelectionManager selection;

	private int tick;
	private int time;

	public MainScene(Engine engine) {
		this.engine = engine;
	}

	@Override
	public void init() {
		shaders = new HashMap<>();

		Shader lineShader = Shader.loadFromFile("res/shd/lineshader.vs", "res/shd/lineshader.fs");
		lineShader.addUniform("projectionMatrix");
		lineShader.addUniform("modelViewMatrix");
		lineShader.addUniform("uniformColor");
		shaders.put("lineShader", lineShader);

		Shader textShader = Shader.loadFromFile("res/shd/textshader.vs", "res/shd/textshader.fs");
		textShader.addUniform("projectionMatrix");
		textShader.addUniform("modelViewMatrix");
		textShader.addUniform("uniformColor");
		textShader.addUniform("textureUnit");
		shaders.put("textShader", textShader);

		Shader pickShader = Shader.loadFromFile("res/shd/pickshader.vs", "res/shd/pickshader.fs");
		pickShader.addUniform("projectionMatrix");
		pickShader.addUniform("viewMatrix");
		shaders.put("pickShader", pickShader);

		Shader instShader = Shader.loadFromFile("res/shd/instshader.vs", "res/shd/instshader.fs");
		instShader.addUniform("projectionMatrix");
		instShader.addUniform("viewMatrix");
		shaders.put("instShader", instShader);

		meshes = new HashMap<>();
		meshes.put("cube", new MeshRenderer(OBJLoader.loadFromFile("res/obj/cube.obj")));
		meshes.put("uvsphere", new MeshRenderer(OBJLoader.loadFromFile("res/obj/uvsphere.obj")));
		meshes.put("icosphere", new MeshRenderer(OBJLoader.loadFromFile("res/obj/icosphere.obj")));
		meshes.put("toycar", new MeshRenderer(OBJLoader.loadFromFile("res/obj/toycar.obj")));

		float[] pos = {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
		float[] tex = {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
		int[] ind = {0, 1, 2, 3};
		square = new Mesh2D(pos, tex, ind);

		font = new TextFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		text = new TextItem("", font);

		camera = new InteractiveCamera(engine.getWindow(), engine.getInput());

		Random random = new Random();
		instances = new ArrayList<>();
		for (int i = 0; i < 300; i++) {
			MeshRenderer mesh = meshes.get("toycar");
			Instance instance = new Instance(i+1, mesh);
			instance.position.set(200.0f*(random.nextFloat()-0.5f), 200.0f*(random.nextFloat()-0.5f), 200.0f*(random.nextFloat()-0.5f));
			instance.rotation.rotateXYZ(6.28f*random.nextFloat(),6.28f*random.nextFloat(), 6.28f*random.nextFloat());
			instance.scale.set(5.0f*random.nextFloat()+1.0f);
			//instance.scale.set(3.0f*random.nextFloat(), 3.0f*random.nextFloat(), 3.0f*random.nextFloat());
			instance.color.set(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1.0f);
			instances.add(instance);
		}
		/*instances.add(new Instance(1, meshes.get("cube")));
		instances.add(new Instance(2, meshes.get("uvsphere")));
		instances.add(new Instance(3, meshes.get("icosphere")));

		instances.get(0).position.set(0.0f, 0.0f, 0.0f);
		instances.get(1).position.set(5.0f, 0.0f, 4.0f);
		instances.get(2).position.set(10.0f, 0.0f, -6.0f);*/

		selection = new SelectionManager(engine.getWindow(), engine.getInput(), camera, instances, square);

		tick = 0;
		time = 0;
	}

	@Override
	public void exit() {
		for (String key : shaders.keySet()) shaders.get(key).delete();
		shaders.clear();
		for (String key : meshes.keySet()) meshes.get(key).delete();
		meshes.clear();
		square.delete();
		font.delete();
		text.delete();
		for (int i = 0; i < instances.size(); i++) instances.get(i).delete();
		instances.clear();
		selection.delete();
	}

	@Override
	public void update(float theta) {
		Input input = engine.getInput();
		Window window = engine.getWindow();

		if (input.isKeyReleased(GLFW_KEY_F11)) {
			window.setFullScreen(!window.isFullScreen());
		} else if (input.isKeyReleased(GLFW_KEY_F12)) {
			window.close();
		}

		camera.update(theta);

		selection.update(theta);

		tick++;
		if (tick >= engine.getTargetUPS()) {
			tick = 0;
			time++;
		}
	}

	@Override
	public void render(float theta) {
		Window window = engine.getWindow();
		float width = window.getWidth();
		float height = window.getHeight();
		Shader lineShader = shaders.get("lineShader");
		Shader textShader = shaders.get("textShader");
		Shader pickShader = shaders.get("pickShader");
		Shader instShader = shaders.get("instShader");

		glViewport(0, 0, (int) width, (int) height);

		camera.capture(theta);

		glClearColor(0.0f, 0.1f, 0.1f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);

		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		for (int i = 0; i < instances.size(); i++) instances.get(i).render(theta);

		instShader.bind();
		instShader.setUniform("projectionMatrix", camera.getProjectionMatrix());
		instShader.setUniform("viewMatrix", camera.getViewMatrix());
		for (String key : meshes.keySet()) {
			meshes.get(key).update();
			meshes.get(key).render();
		}
		instShader.unbind();

		//for (int i = 0; i < instances.size(); i++) instances.get(i).renderOld(window, lineShader, camera);

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		for (int i = 0; i < instances.size(); i++) {
			Instance instance = instances.get(i);
			Vector4f ndc = new Matrix4f(camera.getProjectionMatrix()).mulAffine(camera.getViewMatrix()).transform(new Vector4f(instance.position, 1.0f));
			float x = (ndc.x/ndc.z+1)*width/2.0f;
			float y = (ndc.y/ndc.z+1)*height/2.0f;
			//float fixedAspectRatio = 16.0f/9.0f;
			float worldHeightToScreenHeightRatio = height*camera.getNearClipDistance()/ndc.z;

			if (selection.isDragging()) {
				lineShader.bind();
				lineShader.setUniform("projectionMatrix", new Matrix4f().setOrtho(0.0f, width, 0.0f, height, -1.0f, 1.0f));
				lineShader.setUniform("modelViewMatrix", new Matrix4f().translate(x-1.0f, y-1.0f, 0.0f).scale(2.0f));
				lineShader.setUniform("uniformColor", new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
				square.bind();
				square.draw(GL_POLYGON, 4, 0);
				square.unbind();
				lineShader.unbind();
			}

			if (instance.selected || instance.hovered) {
				Vector4f color = new Vector4f();
				if (instance.selected) {
					if (instance.hovered) {
						color.set(1.0f, 0.5f, 0.0f, 1.0f);
					} else {
						color.set(0.0f, 1.0f, 0.0f, 1.0f);
					}
				} else if (instance.hovered) {
					color.set(1.0f, 1.0f, 0.0f, 1.0f);
				}

				float scaleFactor = camera instanceof TopDownCamera ? 1600.0f : 240.0f;

				lineShader.bind();
				lineShader.setUniform("projectionMatrix", new Matrix4f().setOrtho(0.0f, width, 0.0f, height, -1.0f, 1.0f));
				lineShader.setUniform("modelViewMatrix", new Matrix4f().translate(x-0.5f*scaleFactor*worldHeightToScreenHeightRatio*instance.scale.x, y-0.5f*scaleFactor*worldHeightToScreenHeightRatio*instance.scale.y, 0.0f).scale(scaleFactor, scaleFactor, 1.0f).scale(worldHeightToScreenHeightRatio, worldHeightToScreenHeightRatio, 1.0f).scale(instance.scale));
				lineShader.setUniform("uniformColor", color);
				square.bind();
				square.draw(GL_LINE_LOOP, 4, 0);
				square.unbind();
				lineShader.unbind();

				String groups = "";
				for (int j = 0; j < selection.getNumHotkeys(); j++) {
					if (instance.groups[j]) {
						groups += Integer.toString(j+1)+",";
					}
				}
				if (groups.length() != 0) {
					textShader.bind();
					textShader.setUniform("projectionMatrix", new Matrix4f().setOrtho(0.0f, width, 0.0f, height, -1.0f, 1.0f));
					textShader.setUniform("modelViewMatrix", new Matrix4f().translate((int) (x-0.5f*scaleFactor*worldHeightToScreenHeightRatio*instance.scale.x), (int) (y+9.0f/16.0f*scaleFactor*worldHeightToScreenHeightRatio*instance.scale.y), 0.0f));
					textShader.setUniform("uniformColor", new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
					textShader.setUniform("textureUnit", 0);
					text.setText(groups.substring(0, groups.length()-1));
					text.update();
					text.render();
					textShader.unbind();
				}
			}
		}

		selection.render(lineShader);

		text.setText(String.format("Time: %d s", time));
		text.update();
		textShader.bind();
		textShader.setUniform("projectionMatrix", new Matrix4f().setOrtho(0.0f, width, 0.0f, height, -1.0f, 1.0f));
		textShader.setUniform("modelViewMatrix", new Matrix4f().translate(18.0f, height-36.0f, 0.0f));
		textShader.setUniform("uniformColor", new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		textShader.setUniform("textureUnit", 0);
		text.render();
		textShader.unbind();

		text.setText(String.format("%d FPS | %d UPS", engine.getCurrentFPS(), engine.getCurrentUPS()));
		text.update();
		textShader.bind();
		textShader.setUniform("projectionMatrix", new Matrix4f().setOrtho(0.0f, width, 0.0f, height, -1.0f, 1.0f));
		textShader.setUniform("modelViewMatrix", new Matrix4f().translate(width-text.getWidth()-18.0f, height-36.0f, 0.0f));
		textShader.setUniform("uniformColor", new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
		textShader.setUniform("textureUnit", 0);
		text.render();
		textShader.unbind();

		glDisable(GL_BLEND);

		selection.buffer(pickShader, meshes);
	}

	public static void main(String[] args) {
		Engine engine = new Engine(60, 20);
		Window.Attributes attrib = new Window.Attributes(800, 600);
		attrib.title = "Instance Rendering";
		attrib.resizable = false;
		Window window = new Window(attrib);
		Input input = new Input(window);
		Scene scene = new MainScene(engine);
		engine.setWindow(window);
		engine.setInput(input);
		engine.setScene(scene);
		engine.start();
	}

}
