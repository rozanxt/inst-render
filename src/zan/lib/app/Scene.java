package zan.lib.app;

public interface Scene {

	void init();

	void exit();

	void update(float theta);

	void render(float theta);

}
