package odds.vlllage.com.odds

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import madgaze.x5_gesture.activity.MADGestureActivity
import madgaze.x5_gesture.hardware.KeyCodeHelper
import madgaze.x5_gesture.hardware.KeyCodeHelper.KeyType.*

class MainActivity : MADGestureActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorCallback: SensorEventListener
    private lateinit var glView: GLSurfaceView
    private lateinit var renderer: SceneRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        renderer = SceneRenderer()

        glView = GLSurfaceView(this)
        glView.setEGLContextClientVersion(2)
        glView.setRenderer(renderer)
        glView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        setContentView(glView)
        glView.keepScreenOn = true
        glView.layoutParams.height = MATCH_PARENT
        glView.layoutParams.width = MATCH_PARENT

        sensorCallback = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                when (event!!.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        val matrix = FloatArray(16)
                        Matrix.setIdentityM(matrix, 0)
                        SensorManager.getRotationMatrixFromVector(matrix, event.values)
                        renderer.rotate(matrix)
                        glView.requestRender()
                    }
                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        renderer.move(event.values)
                    }
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private fun mod(a: Double, b: Double): Double {
        return (a % b + b) % b;
    }

    override fun onStart() {
        super.onStart()

        sensorManager.registerListener(sensorCallback,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                48,
                SensorManager.SENSOR_DELAY_FASTEST)

//        sensorManager.registerListener(sensorCallback,
//                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
//                48,
//                SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onStop() {
        super.onStop()

        sensorManager.unregisterListener(sensorCallback)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        when (KeyCodeHelper.CheckKeyType(event.keyCode, event)) {
            RIGHT_DOWN -> {

            }
            LEFT_UP -> {

            }
            CONFIRM -> {

            }
            MENU -> {

            }
            BACK -> {

            }
            else -> super.dispatchKeyEvent(event)
        }

        return true
    }
}
