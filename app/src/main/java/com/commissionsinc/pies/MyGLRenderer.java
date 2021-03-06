package com.commissionsinc.pies;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Lese on 3/24/17.
 */

public class MyGLRenderer implements GLSurfaceView.Renderer {

    float[] mMVPMatrix = new float[16];
    float[] mVMatrix = new float[16];
    float[] mPMatrix = new float[16];

    private ArrayList<Triangle> mTriangles;
    private Triangle mHole;
    Random chaos;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mTriangles = new ArrayList<>(100);
        chaos = new Random(System.currentTimeMillis());
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        float[] pos = new float[2];
        float[] color = new float[3];
        int endStep = 0;
        int parts = 20;
        for (int i = 0; i < parts; i++) {
            pos[0] = 0.0f;
            pos[1] = 0.0f;

            if (chaos.nextBoolean()) pos[0] *= -1;
            if (chaos.nextBoolean()) pos[1] *= -1;

            color[0] = chaos.nextFloat();
            color[1] = chaos.nextFloat();
            color[2] = chaos.nextFloat();

            mTriangles.add(new Triangle(pos.clone(), 360/parts, color.clone(), 0.6f, endStep, 10));
            endStep = mTriangles.get(i).endStep;
        }
        mHole = new Triangle(new float[] { 0.0f, 0.0f, 0.1f}, 360, new float[] { 0.0f, 0.0f, 0.0f}, 0.3f, 0, 100);
    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mPMatrix, 0, mVMatrix, 0);
        for (Triangle t : mTriangles) {
            t.draw(mMVPMatrix);
        }
        mHole.draw(mMVPMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width/height;
        Matrix.frustumM(mPMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
