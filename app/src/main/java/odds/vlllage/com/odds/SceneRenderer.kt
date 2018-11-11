package odds.vlllage.com.odds

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

enum class Device(val fov: Float) {
    X5(8f),
    VADER(45f/1.7f)
}

class SceneRenderer : GLSurfaceView.Renderer {
    companion object {
        val FOVY = Device.VADER.fov
    }

    private val mvpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    private val tempMatrix = FloatArray(16)
    private var program: Int = 0

    private lateinit var triangles: HashSet<Triangle>

    fun rotate(matrix: FloatArray) {
        matrix.forEachIndexed { i, v -> rotationMatrix[i] = v }
    }

    fun move(xyz: FloatArray) {
        Matrix.translateM(viewMatrix, 0, xyz[0] / 50, xyz[1] / 50, xyz[2] / 50);
    }

    override fun onDrawFrame(p0: GL10?) {
        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, rotationMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        triangles.forEach { it.draw(program, mvpMatrix) }
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspectRatio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, FOVY, aspectRatio, 1f, 100f)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        Matrix.setIdentityM(rotationMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)

        triangles = HashSet()

        val random = Random(0)

        for (i in 1..222) {
            val d = random.nextFloat() * 90 + 10
            val x = Math.cos(i / 20.0).toFloat() * (d + 5)
            val y = Math.sin(i / 20.0).toFloat() * (d+ 5)
            val z = -1.8f
            triangles.add(Triangle(floatArrayOf(x, y, z)))
        }

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }


    internal fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        return shader
    }

    private val vertexShaderCode = "uniform mat4 uMVPMatrix;" +
        "attribute vec4 vPosition;" +
        "void main() {" +
        "  gl_Position = uMVPMatrix * vPosition;" +
        "}"

    private val fragmentShaderCode = (
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}")
}