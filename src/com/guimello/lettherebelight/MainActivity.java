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
	
	private static Camera camera;
	private boolean lightOn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getCamera();
	}

	private void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
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
	    	// TODO: Use the screen as a flashlight (next best thing)
	    	return;
	    }
	    
	    lightOn = true;
	    
	    Parameters parameters = camera.getParameters();
	    if (parameters == null) {
	    	// TODO: Use the screen as a flashlight (next best thing)
	    	return;
	    }
	    
	    List<String> flashModes = parameters.getSupportedFlashModes();
	    // Check if camera flash exists
	    if (flashModes == null) {
	    	// TODO: Use the screen as a flashlight (next best thing)
	    	return;
	    }
	    
	    String flashMode = parameters.getFlashMode();
	    Log.i(TAG, "Flash mode: " + flashMode);
	    Log.i(TAG, "Flash modes: " + flashModes);
	    
	    // Maybe it is on already
	    // TODO: Remove this check
	    if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
	    	// Turn on the flash
	    	if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
	    		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
	    		camera.setParameters(parameters);
	    		// TODO: Forbid sleeping
	    	} else {
	    		Toast.makeText(
	    			this,
	    			"Flash mode (torch) not supported",
	    			Toast.LENGTH_LONG
	    		).show();
	        
	    		// TODO: Use the screen as a flashlight (next best thing)
	    		Log.e(TAG, "FLASH_MODE_TORCH not supported");
	    	}
	    }
	}
	
	private void turnLightOff() {
		if (lightOn || true) {
			lightOn = false;
	      
			if (camera == null) return;
	      
			Parameters parameters = camera.getParameters();
			if (parameters == null) return;
	      
			List<String> flashModes = parameters.getSupportedFlashModes();
			String flashMode = parameters.getFlashMode();
			
			// Check if camera flash exists
			if (flashModes == null) return;

			Log.i(TAG, "Flash mode: " + flashMode);
			Log.i(TAG, "Flash modes: " + flashModes);

			if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
				// Turn off the flash
				if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					camera.setParameters(parameters);
					// TODO: Remove sleep prevention
				} else {
					Log.e(TAG, "FLASH_MODE_OFF not supported");
				}
			}
	    }
	}
}
