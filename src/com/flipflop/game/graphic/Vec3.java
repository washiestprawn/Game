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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec3) {
			Vec3 vecObj = (Vec3) obj;
			if (super.equals(obj) && vecObj.z == this.z) {
				return true;
			}
		}
		return super.equals(obj);
	}
}
