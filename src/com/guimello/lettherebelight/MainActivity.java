package com.guimello.lettherebelight;

import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final String LIGHT_ON_TAG = "LIGHT_ON";
	
	private Camera camera;
	private Parameters cameraParameters;
	private List<String> flashModes;
	private boolean lightOn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startCamera();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean(LIGHT_ON_TAG, lightOn);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		lightOn = savedInstanceState.getBoolean(LIGHT_ON_TAG);
	}

	private void startCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();

				cameraParameters = camera.getParameters();
			    if (cameraParameters == null) return;
			    
			    flashModes = cameraParameters.getSupportedFlashModes();
			} catch (RuntimeException e) {
				Log.i(TAG, "Camera.open() failed: " + e.getMessage());
			}
	    }
	}
	
	public void toggleLight(View view) {
		toggleLight();
	}
	
	private void toggleLight() {
		if (lightOn) {
			turnLightOff();
	    } else {
	    	turnLightOn();
	    }
	}
	
	private void turnLightOn() {
		if (camera == null) {
	    	Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show();
	    	return;
	    }
	    
	    lightOn = true;
	    
	    if (!isFlashSupported()) return;

	    String flashMode = cameraParameters.getFlashMode();

	    Log.i(TAG, "Flash mode: " + flashMode);
	    Log.i(TAG, "Flash modes: " + flashModes);

	    // Turn on the flash
	    if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
	    	enableCameraFlash();
	    } else {
	    	Toast.makeText(
	    		this,
	    		"Flash mode (torch) not supported",
	    		Toast.LENGTH_LONG
	    	).show();

	    	Log.e(TAG, "FLASH_MODE_TORCH not supported");
	    }
	}

	private void turnLightOff() {
		if (lightOn) {
			lightOn = false;
	      
			if (camera == null || !isFlashSupported()) return;
			
			String flashMode = cameraParameters.getFlashMode();

			Log.i(TAG, "Flash mode: " + flashMode);
			Log.i(TAG, "Flash modes: " + flashModes);
			
			// Turn on the flash briefly before turning off
			// This avoids an issues when the flash is on but the flash
			// mode returned is off
			enableCameraFlash();

			// Turn off the flash
			if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
				disableCameraFlash();
			} else {
				Toast.makeText(this, "Could not turn off the flash", Toast.LENGTH_LONG).show();
				Log.e(TAG, "FLASH_MODE_OFF not supported");
			}
	    }
	}
	
	private void enableCameraFlash() {
		if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
	    	cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
	    	camera.setParameters(cameraParameters);
	    }
	}
	
	private void disableCameraFlash() {
		if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
			cameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(cameraParameters);
		}
	}
	
	private boolean isFlashSupported() {
		return flashModes != null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
	}
}
