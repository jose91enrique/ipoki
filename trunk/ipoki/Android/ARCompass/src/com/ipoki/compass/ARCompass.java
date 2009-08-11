package com.ipoki.compass;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.ipoki.compass.CameraView;
import com.ipoki.compass.Compass;
import com.ipoki.compass.CompassRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

public class ARCompass extends Activity {
    private SensorManager mSensorManager;
	private CameraView mCameraView;
	private GLSurfaceView mGLSurfaceView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        CompassRenderer compassRenderer = new CompassRenderer(); 
        mGLSurfaceView.setRenderer(compassRenderer);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        
        setContentView(mGLSurfaceView);

        mCameraView = new CameraView(this);
        addContentView(mCameraView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (listSensors.size() > 0)
        {
        	mSensorManager.registerListener(compassRenderer, listSensors.get(0), SensorManager.SENSOR_DELAY_UI);
        }

        listSensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (listSensors.size() > 0)
        {
        	mSensorManager.registerListener(compassRenderer, listSensors.get(0), SensorManager.SENSOR_DELAY_UI);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    protected void onStop()
    {
        super.onStop();
    }
}

class CameraView extends SurfaceView implements SurfaceHolder.Callback { 
    SurfaceHolder mHolder;
    Camera mCamera;

    CameraView(Context context) {
        super(context);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        mCamera = Camera.open();
        try {
           mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            Log.e("Camera", "Error en SurfaceCreated", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(w, h);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }
}

/**
 * Render a compass.
 */

class CompassRenderer implements GLSurfaceView.Renderer, SensorEventListener {
        private float   mAccelerometerValues[] = new float[3];
        private float   mMagneticValues[] = new float[3];
        private float rotationMatrix[] = new float[16];
        private float remappedRotationMatrix[] = new float[16];

	    private Compass mCompass;

    public CompassRenderer() {
        mCompass = new Compass();
    }

    public void onDrawFrame(GL10 gl) {
    	// Get rotation matrix from the sensor
        SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerValues, mMagneticValues);
        // As the documentation says, we are using the device as a compass in landscape mode
        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, remappedRotationMatrix);

        // Clear color buffer
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // Load remapped matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glLoadMatrixf(remappedRotationMatrix, 0);
        
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        mCompass.draw(gl);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
         gl.glViewport(0, 0, width, height);

         /*
          * Set our projection matrix. This doesn't have to be done
          * each time we draw, but usually a new projection needs to
          * be set when the viewport is resized.
          */
         float ratio = (float) width / height;
         gl.glMatrixMode(GL10.GL_PROJECTION);
         gl.glLoadIdentity();
         gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        gl.glDisable(GL10.GL_DITHER);

        /*
         * Some one-time OpenGL initialization can be made here
         * probably based on features of this particular context
         */
         gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                 GL10.GL_FASTEST);

         gl.glClearColor(0,0,0,0);
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	       synchronized (this) {
	        	switch(event.sensor.getType()) {
	        	case Sensor.TYPE_ACCELEROMETER:
	        		mAccelerometerValues = event.values.clone();
		            break;
	        	case Sensor.TYPE_MAGNETIC_FIELD:
	        		mMagneticValues = event.values.clone();
		            break;
		        default:
		        	break;
	        	}
	       }
	}
}

/**
 * Definition of the cube.
 */
class Compass
{
    private FloatBuffer   mVertexBuffer;
    private IntBuffer   mColorBuffer;
    private ByteBuffer  mIndexBuffer;

    public Compass()
    {
    	int one = 0x10000;

        int colorLines[] = {
                0,  one,    0,  one,
                0,  one,    0,  one,
          };
        
        int colorLetters[] = {
        		//North
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                // South
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                // East
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                // West
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
                one,  one,    0,  one,
        };

        // Buffers to be passed to gl*Pointer() functions
        // must be direct, i.e., they must be placed on the
        // native heap where the garbage collector cannot
        // move them.
        //
        // Buffers with multi-byte datatypes (e.g., short, int, float)
        // must have their byte order set to native order

        // (( vertices_per_compass_line * coords_per_vertex * lines_number) 
        // + north_vertices * coords_per_vertex + south_vertices * coords_per_vertex 
        // + east_vertices * coords_per_vertex + west_vertices * coords_per_vertex) 
        // * bytes_per_float
        ByteBuffer vbb = ByteBuffer.allocateDirect(((2 * 3 * 16) + (6 * 3) + (10 * 3) + (8 * 3) + (8 * 3)) * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();

        // ((total_compass_vertices * coords_per_color) + 
        // (north_vertices * coords_per_color)  + (south_vertices * coords_per_color))
        // * bytes_per_int
        ByteBuffer cbb = ByteBuffer.allocateDirect(((32 * 4) + (6 * 4) + (10 * 4) + (8 * 4) + (8 * 4)) * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asIntBuffer();

        mIndexBuffer = ByteBuffer.allocateDirect(32 + 6 + 10 + 8 + 8);
        float x;
        float y;
        float z;
        for (int i = 0; i < 16; i++)
        {
        	if (i % 2 == 0) 
        		if (i % 4 == 0) 
        			z = 6.0f;
        		else 
        			z = 4.0f;
        	else 
        		z = 2.0f;
        	
        	x = (float)(Math.sin(((double)i / 16) * 2 * Math.PI) * 32);
        	y = (float)(Math.cos(((double)i / 16) * 2 * Math.PI) * 32);
        	mVertexBuffer.put(x);
        	mVertexBuffer.put(y);
        	mVertexBuffer.put(-z);
	        mIndexBuffer.put((byte)(2 * i));

        	mVertexBuffer.put(x);
        	mVertexBuffer.put(y);
        	mVertexBuffer.put(z);
	        mIndexBuffer.put((byte)(2 * i + 1));

	        mColorBuffer.put(colorLines);
        }
        
        float north[] = {
        	-2.0f, 32.0f, 7.0f,
        	-2.0f, 32.0f, 11.0f,
        	-2.0f, 32.0f, 11.0f,
        	2.0f, 32.0f, 7.0f,
        	2.0f, 32.0f, 7.0f,
        	2.0f, 32.0f, 11.0f,
        };
        mVertexBuffer.put(north);
        byte indices[] = {
                32, 33, 34, 35, 36, 37,
        };
        mIndexBuffer.put(indices);
        
        float south[] = {
            	2.0f, -32.0f, 7.0f,
            	-2.0f, -32.0f, 7.0f,
            	-2.0f, -32.0f, 7.0f,
            	-2.0f, -32.0f, 9.0f,
            	-2.0f, -32.0f, 9.0f,
            	2.0f, -32.0f, 9.0f,
            	2.0f, -32.0f, 9.0f,
            	2.0f, -32.0f, 11.0f,
            	2.0f, -32.0f, 11.0f,
            	-2.0f, -32.0f, 11.0f,
        };
        mVertexBuffer.put(south);
        indices = new byte[]{
                38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
        };
        mIndexBuffer.put(indices);

        float east[] = {
        		32.0f, -2.0f, 7.0f,
        		32.0f, 2.0f, 7.0f,
        		32.0f, -2.0f, 9.0f,
        		32.0f, 2.0f, 9.0f,
        		32.0f, -2.0f, 11.0f,
        		32.0f, 2.0f, 11.0f,
        		32.0f, 2.0f, 7.0f,
        		32.0f, 2.0f, 11.0f,
        };
        mVertexBuffer.put(east);
        indices = new byte[]{
                48, 49, 50, 51, 52, 53, 54, 55, 
        };
        mIndexBuffer.put(indices);
        
        float west[] = {
        		-32.0f, 2.0f, 11.0f,
        		-32.0f, 1.0f, 7.0f,
        		-32.0f, 1.0f, 7.0f,
        		-32.0f, 0, 9.0f,
        		-32.0f, 0, 9.0f,
        		-32.0f, -1.0f, 7.0f,
        		-32.0f, -1.0f, 7.0f,
        		-32.0f, -2.0f, 11.0f,
        };
        mVertexBuffer.put(west);
        indices = new byte[]{
                56, 57, 58, 59, 60, 61, 62, 63, 
        };
        mIndexBuffer.put(indices);
        
        mColorBuffer.put(colorLetters);
        
        mColorBuffer.position(0);
        mVertexBuffer.position(0);
        mIndexBuffer.position(0);
    }

    public void draw(GL10 gl)
    {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuffer);
        gl.glDrawElements(GL10.GL_LINES, 32 + 6 + 10 + 8 + 8, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
    }
}