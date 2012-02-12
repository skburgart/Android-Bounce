package com.stevekb.sandbox;

public class Circle {

	public float x, y, vx, vy;
	public float elasticity;
	public int radius;
	public int color;

	public Circle(float x, float y, int radius, float elasticity, int color) {

		this.x = x;
		this.y = y;
		this.radius = radius;
		this.elasticity = elasticity;
		this.color = color;
	}
}
