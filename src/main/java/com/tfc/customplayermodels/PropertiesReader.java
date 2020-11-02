package com.tfc.customplayermodels;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class PropertiesReader {
	private final HashMap<String, String> properties = new HashMap<>();
	
	public PropertiesReader(File toParse) throws IOException {
		FileInputStream stream = new FileInputStream(toParse);
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		String prop = new String(bytes);
		String[] properties = prop.split("\n");
		for (String s : properties) {
			if (s.contains(":")) {
				String[] split = s.split(":", 2);
				this.properties.put(split[0].trim(), split[1].trim());
			}
		}
	}
	
	public PropertiesReader(String toParse) {
		String[] properties = toParse.split("\n");
		for (String s : properties) {
			if (s.contains(":")) {
				String[] split = s.split(":", 1);
				this.properties.put(split[0], split[1]);
			}
		}
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		properties.forEach((name, val) -> builder.append(name).append(":").append(val).append("\n"));
		return builder.toString();
	}
	
	public String getValue(String property) {
		if (properties.containsKey(property))
			return properties.get(property);
		return null;
	}
	
	public <A extends java.io.Serializable> A getValue(String property, Class<A> aClass) {
		try {
			return (A) aClass.getMethod("valueOf").invoke(null, properties.get(property));
		} catch (Throwable ignored) {
			return null;
		}
	}
}
