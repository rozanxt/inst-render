package zan.lib.sfx;

import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.AL10.alDopplerFactor;
import static org.lwjgl.openal.AL10.alListener3f;
import static org.lwjgl.openal.AL10.alListenerfv;
import static org.lwjgl.openal.AL11.alSpeedOfSound;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;

import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;

public final class SoundSystem {

	private static long device;
	private static long context;

	private SoundSystem() {

	}

	public static void init() {
		device = alcOpenDevice(alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER));
		context = alcCreateContext(device, new int[] { 0 });
		alcMakeContextCurrent(context);
		AL.createCapabilities(ALC.createCapabilities(device));
	}

	public static void exit() {
		alcDestroyContext(context);
		alcCloseDevice(device);
	}

	public static void setDistanceModel(int model) {
		alDistanceModel(model);
	}

	public static void setDopplerFactor(float factor) {
		alDopplerFactor(factor);
	}

	public static void setSoundSpeed(float speed) {
		alSpeedOfSound(speed);
	}

	public static void setListenerPosition(Vector3f position) {
		alListener3f(AL_POSITION, position.x, position.y, position.z);
	}

	public static void setListenerVelocity(Vector3f velocity) {
		alListener3f(AL_VELOCITY, velocity.x, velocity.y, velocity.z);
	}

	public static void setListenerOrientation(Vector3f at, Vector3f up) {
		float[] orientation = new float[6];
		orientation[0] = at.x;
		orientation[1] = at.y;
		orientation[2] = at.z;
		orientation[3] = up.x;
		orientation[4] = up.y;
		orientation[5] = up.z;
		alListenerfv(AL_ORIENTATION, orientation);
	}

}
