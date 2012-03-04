package com.flipflop.game.graphic;

import static org.lwjgl.opengl.GL11.*;

public class RecPrism implements Drawable {

	// Indices of v to create faces.  Dependent on the order in which v is instantiated.
	private static int[][] faces = {{2,1,5,6}, {3,0,4,7}, {7,3,2,6}, {0,4,5,1}, {3,0,1,2}, {4,7,6,5}};
	protected Vec3[] v;
	protected Vec3[] faceColor = new Vec3[6];
	protected Vec3[] indexColor = new Vec3[8];
	protected Vec3 center;
	protected Vec3 rot = new Vec3(0.0f, 0.0f, 0.0f);
	protected float degree = 0.0f;


	static {
	}

	public RecPrism(Vec3 center, Vec3 rot, float width, float height, float depth, float degree) {
		this.center = center;
		this.rot = rot;
		this.degree = degree;
		initVertices(width, height, depth);
	}
	
	public RecPrism(Vec3 center, float width, float height, float depth) {
		this.center = center;
		initVertices(width, height, depth);
	}
	
	private void initVertices(float width, float height, float depth) {
		float depthBy2 = depth / 2;
		float widthBy2 = width / 2;
		float heightBy2 = height / 2;
		v = new Vec3[8];
		v[0] = new Vec3(( + widthBy2), ( - heightBy2), ( + depthBy2));
		v[1] = new Vec3(( + widthBy2), ( + heightBy2), ( + depthBy2));
		v[2] = new Vec3(( - widthBy2), ( + heightBy2), ( + depthBy2));
		v[3] = new Vec3(( - widthBy2), ( - heightBy2), ( + depthBy2));
		v[4] = new Vec3(( + widthBy2), ( - heightBy2), ( - depthBy2));
		v[5] = new Vec3(( + widthBy2), ( + heightBy2), ( - depthBy2));
		v[6] = new Vec3(( - widthBy2), ( + heightBy2), ( - depthBy2));
		v[7] = new Vec3(( - widthBy2), ( - heightBy2), ( - depthBy2));
	}

	@Override
	public void render() {
		glPushMatrix();
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glLineWidth(1.0f);
		glColor3f(0.5f,0.5f,1.0f);
		
		glTranslatef(center.x, center.y, center.z);
		glRotatef(this.degree, rot.x, rot.y, rot.z);
		
		glBegin(GL_QUADS);
		for (int i=0; i<faces.length; i++) {
			for (int j=0; j<faces[i].length; j++) {
				glVertex3f(v[faces[i][j]].x, v[faces[i][j]].y, v[faces[i][j]].z);
			}
		}
		glEnd();

		glColor3f(0.0f,0.0f,0.0f);
		
		for (int i=0; i<faces.length; i++) {
			glBegin(GL_LINE_LOOP);
			for (int j=0; j<faces[i].length; j++) {
				glVertex3f(v[faces[i][j]].x, v[faces[i][j]].y, v[faces[i][j]].z);
			}
			glEnd();
		}

		glPopMatrix();
	}
	
	public void rotate(Vec3 rot, float degree) {
		this.rot = rot;
		this.degree = degree;
	}
}
