package com.flipflop.game.graphic;

public class Vec2 {
	public float x, y;
	
	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec2) {
			Vec2 vecObj = (Vec2) obj;
			if (vecObj.x == this.x && vecObj.y == this.y) {
				return true;
			}
		} else {
			return super.equals(obj);
		}
		return false;
	}
}
