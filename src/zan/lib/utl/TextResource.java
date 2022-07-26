package zan.lib.utl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class TextResource {

	private TextResource() {

	}

	public static String loadFromFileAsString(String path) {
		StringBuilder text = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = reader.readLine()) != null) {
				text.append(line).append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text.toString();
	}

	public static List<String> loadFromFileAsStringList(String path) {
		List<String> text = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = reader.readLine()) != null) {
				text.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

}
