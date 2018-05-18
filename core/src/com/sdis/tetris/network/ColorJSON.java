package com.sdis.tetris.network;

import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.badlogic.gdx.graphics.Color;
public class ColorJSON extends Color {
	float r;
	float g;
	float b;
	float a;
	int y;
	int x;
	
	public ColorJSON(float r, float g, float b, float a, int y, int x) {
		this.r=r;
		this.g=g;
		this.b=b;
		this.a=a;
		this.y=y;
		this.x=x;
	}
	
	public JsonObject toJSON() {
		return Json.createObjectBuilder()
				.add("r", ""+r)
				.add("g", ""+g)
				.add("b", ""+b)
				.add("a", ""+a)
				.add("y", ""+y)
				.add("x", ""+x)
				.build();
	}
	
	public static ColorJSON fromJSON(JsonObject obj) {
		float r=Float.parseFloat(obj.getString("r"));
		float g=Float.parseFloat(obj.getString("g"));
		float b=Float.parseFloat(obj.getString("b"));
		float a=Float.parseFloat(obj.getString("a"));
		int y = Integer.parseInt(obj.getString("y"));
		int x = Integer.parseInt(obj.getString("x"));
		return new ColorJSON(r,g,b,a,y,x);
	}
	
	public static JsonArray toJSONfromArrayList(ArrayList<ColorJSON> colors) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(ColorJSON col: colors) {
			builder.add(col.toJSON());
		}
		return builder.build();
	}
	
	public static ArrayList<ColorJSON> fromJSONtoArrayList(String trim) {
		ArrayList<ColorJSON> colors = new ArrayList<>();
		JsonReader jsonReader = Json.createReader(new StringReader(trim));
		JsonArray arr = jsonReader.readArray();
		for(JsonValue obj: arr) {
			colors.add(fromJSON(obj.asJsonObject()));
		}
		jsonReader.close();
		return colors;
	}
	


}
