package com.sdis.tetris.network;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;

public class ColorSerializable extends Color implements Serializable {
	float r;
	float g;
	float b;
	float a;
	
	public ColorSerializable(float r, float g, float b, float a) {
		this.r=r;
		this.g=g;
		this.b=b;
		this.a=a;
	}

}
