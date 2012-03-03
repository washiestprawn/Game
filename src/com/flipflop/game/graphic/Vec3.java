package com.flipflop.game.graphic;

public class Vec3 extends Vec2 {
	public float z;
	
	public Vec3(float x, float y, float z) {
		super(x, y);
		this.z = z;
	}
	
	public Vec3(float p1, float p2) {
		super(p1, p2);
		this.z = 0.0f;
	}
	
	public Vec3() {
		super(0.0f, 0.0f);
		this.z = 0.0f;
	}
}
