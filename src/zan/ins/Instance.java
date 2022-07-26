package zan.ins;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import zan.lib.app.Window;
import zan.lib.gfx.Shader;

public class Instance {

	public int id;

	public MeshRenderer mesh;

	public Vector3f position;
	public Quaternionf rotation;
	public Vector3f scale;
	public Matrix4f trafo;
	public Vector4f color;

	public boolean[] groups;

	public boolean hovered = false;
	public boolean selected = false;

	public Instance(int id, MeshRenderer mesh) {
		this.id = id;
		this.mesh = mesh;
		position = new Vector3f();
		rotation = new Quaternionf();
		scale = new Vector3f(1.0f, 1.0f, 1.0f);
		trafo = new Matrix4f();
		color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		groups = new boolean[9]; //selection.getNumHotkeys()
		for (int i = 0; i < groups.length; i++) {
			groups[i] = false;
		}
	}

	public void delete() {

	}

	public void render(float theta) {
		mesh.add(trafo.identity().translate(position).rotate(rotation).scale(scale), color, id);
	}

	public void renderOld(Window window, Shader shader, TopDownCamera camera, float theta) {
		shader.bind();
		shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
		shader.setUniform("modelViewMatrix", new Matrix4f(camera.getViewMatrix()).translate(position).rotate(rotation).scale(scale));
		shader.setUniform("uniformColor", color);
		mesh.mesh.bind();
		mesh.mesh.draw();
		mesh.mesh.unbind();
		shader.unbind();
	}

}
