package zan.ins;

import org.joml.Matrix4fc;
import org.joml.Vector3fc;

public interface Camera {

	void update(float theta);

	void capture(float theta);

	void setTarget(Vector3fc target);

	float getNearClipDistance();

	Matrix4fc getProjectionMatrix();

	Matrix4fc getViewMatrix();

}
