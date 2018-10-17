package odds.vlllage.com.odds

import android.opengl.GLES20

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle(positionCoords: FloatArray) {
    private val vertexBuffer: FloatBuffer
    private var mPositionHandle = 0
    private var mColorHandle = 0
    private var mMVPMatrixHandle = 0
    private val vertexCount = 3
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    private val position: FloatArray = positionCoords

    internal var color: FloatArray

    init {
        val bb = ByteBuffer.allocateDirect(positionCoords.size * vertexStride)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        setPosition(positionCoords)
        color = floatArrayOf(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat(), 0.0f)
    }

    internal fun setPosition(position: FloatArray) {
        position.forEachIndexed { index, value -> this.position[index] = value }

        val x = position[0]
        val y = position[1]
        val z = position[2]
        val triangleCoords = floatArrayOf(
                x, y + 6.3f, z,
                x - 0.5f, y, z,
                x + 0.5f, y, z
        )

        vertexBuffer.put(triangleCoords)
        vertexBuffer.position(0)
    }

    fun draw(program: Int, mvpMatrix: FloatArray) {
        if (Math.random() < 0.05) {
            setPosition(floatArrayOf(position[0] * 0.99f, position[1] * 0.99f, position[2]))
        }

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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
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