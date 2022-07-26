package zan.lib.gfx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import zan.lib.utl.TextResource;
import zan.lib.utl.TypeConverter;

public final class OBJLoader {

	private static class Group {
		public static final int NO_VALUE = -1;
		public int pos = NO_VALUE;
		public int tex = NO_VALUE;
		public int nrm = NO_VALUE;
	}

	private static class Face {
		private Group[] groups = new Group[3];

		public Face(String t0, String t1, String t2) {
			groups[0] = parseToken(t0);
			groups[1] = parseToken(t1);
			groups[2] = parseToken(t2);
		}

		public Group[] getGroups() {
			return groups;
		}

		private Group parseToken(String token) {
			Group group = new Group();
			String[] items = token.split("/");
			int length = items.length;
			group.pos = Integer.parseInt(items[0]) - 1;
			if (length > 1) {
				group.tex = items[1].length() > 0 ? Integer.parseInt(items[1]) - 1 : Group.NO_VALUE;
				if (length > 2) {
					group.nrm = Integer.parseInt(items[2]) - 1;
				}
			}
			return group;
		}
	}

	private OBJLoader() {

	}

	public static Mesh3D loadFromFile(String path) {
		return parseOBJ(TextResource.loadFromFileAsStringList(path));
	}

	public static Mesh3D parseOBJ(String data) {
		return parseOBJ(Arrays.asList(data.split("\n")));
	}

	public static Mesh3D parseOBJ(List<String> data) {
		List<Vector3f> positions = new ArrayList<>();
		List<Vector2f> texcoords = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		List<Face> faces = new ArrayList<>();

		for (String line : data) {
			String[] tokens = line.split("\\s+");
			switch (tokens[0]) {
			case "v":
				positions.add(new Vector3f(
					Float.parseFloat(tokens[1]),
					Float.parseFloat(tokens[2]),
					Float.parseFloat(tokens[3])));
				break;
			case "vt":
				texcoords.add(new Vector2f(
					Float.parseFloat(tokens[1]),
					Float.parseFloat(tokens[2])));
				break;
			case "vn":
				normals.add(new Vector3f(
					Float.parseFloat(tokens[1]),
					Float.parseFloat(tokens[2]),
					Float.parseFloat(tokens[3])));
				break;
			case "f":
				faces.add(new Face(tokens[1], tokens[2], tokens[3]));
				break;
			}
		}

		return createMesh(positions, texcoords, normals, indices, faces);
	}

	private static Mesh3D createMesh(List<Vector3f> positions, List<Vector2f> texcoords, List<Vector3f> normals, List<Integer> indices, List<Face> faces) {
		float[] posArr = new float[3 * positions.size()];
		float[] texArr = new float[2 * positions.size()];
		float[] nrmArr = new float[3 * positions.size()];

		int i = 0;
		for (Vector3f pos : positions) {
			posArr[3 * i] = pos.x;
			posArr[3 * i + 1] = pos.y;
			posArr[3 * i + 2] = pos.z;
			i++;
		}

		for (Face face : faces) {
			Group[] groups = face.getGroups();
			for (Group group : groups) {
				indices.add(group.pos);
				if (group.tex > Group.NO_VALUE) {
					Vector2f tex = texcoords.get(group.tex);
					texArr[2 * group.pos] = tex.x;
					texArr[2 * group.pos + 1] = 1.0f - tex.y;
				}
				if (group.nrm > Group.NO_VALUE) {
					Vector3f nrm = normals.get(group.nrm);
					nrmArr[3 * group.pos] = nrm.x;
					nrmArr[3 * group.pos + 1] = nrm.y;
					nrmArr[3 * group.pos + 2] = nrm.z;
				}
			}
		}

		return new Mesh3D(posArr, texArr, nrmArr, TypeConverter.IntegerListToArray(indices));
	}

}
