package odds.vlllage.com.odds

import android.opengl.GLES20

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle(triangleCoords: FloatArray) {
    private val vertexBuffer: FloatBuffer
    private var mPositionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var mMVPMatrixHandle: Int = 0
    private val vertexCount = 9 / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    internal var color: FloatArray

    init {
        val bb = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)
        color = floatArrayOf(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), 0.0f)
    }

    fun draw(program: Int, mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)
        mPositionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer)
        mColorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)
        mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        checkGlError("glGetUniformLocation")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, vertexCount)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    companion object {
        internal val COORDS_PER_VERTEX = 3

        fun checkGlError(glOperation: String) {
            val error: Int
            while (true) {
                error = GLES20.glGetError()
                if (error == GLES20.GL_NO_ERROR) break
                throw RuntimeException(glOperation + ": glError " + error)
            }
        }
    }
}