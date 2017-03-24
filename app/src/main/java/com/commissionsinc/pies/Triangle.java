package com.commissionsinc.pies;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * Created by Lese on 3/24/17.
 */

public class Triangle {
    private FloatBuffer vertexBuffer;
    public final int mProgram;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    private int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[];

    float color[] = { 0.0f, 0.5f, 0.0f, 1.0f };

    public Triangle(float[] center, double degrees, float[] color, float radius) {

        int i = 0;
        int triangleCount = 8;
        float twicePi = (float)toRadians(degrees);

        triangleCoords = new float[3 * (triangleCount + 2)];

        triangleCoords[i++] = center[0];
        triangleCoords[i++] = center[1];
        triangleCoords[i++] = 0.0f;

        for (int j = 10; j <= triangleCount+10; j++) {
            triangleCoords[i++] = center[0] + (float)(radius * cos(j * twicePi / triangleCount));
            triangleCoords[i++] = center[1] + (float)(radius * sin(j * twicePi / triangleCount));
            triangleCoords[i++] = 0.0f;
        }

        this.color[0] = color[0];
        this.color[1] = color[1];
        this.color[2] = color[2];

        vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private final String vertexShaderCode =
                    "uniform mat4 uMVPMatrix;\n" +
                    "attribute vec4 vPosition;\n" +
                    "void main() {\n" +
                    "    gl_Position = uMVPMatrix * vPosition;\n" +
                    "}";

    private final String fragmentShaderCode =
                    "precision mediump float;\n" +
                    "uniform vec4 vColor;\n" +
                    "void main() {\n" +
                    "   gl_FragColor = vColor;\n" +
                    "}";
}
