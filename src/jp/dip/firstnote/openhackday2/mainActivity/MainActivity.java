package jp.dip.firstnote.openhackday2.mainActivity;

import jp.dip.firstnote.openhackday2.R;
import jp.dip.firstnote.openhackday2.R.layout;
import jp.dip.firstnote.openhackday2.R.menu;
import jp.dip.firstnote.openhackday2.postPhoto.PhotoPreviewActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	CameraPreview preview = null;
    private PictureCallback pictureCallback = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pictureCallback = new PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				Bitmap scaled_bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);

				Intent intent = new Intent(MainActivity.this, PhotoPreviewActivity.class);
				intent.putExtra(PhotoPreviewActivity.INTENT_BITMAP, scaled_bitmap);
				startActivity(intent);
			}
		};
		
		preview = (CameraPreview)findViewById(R.id.cameraPreview1);
		Button button = (Button)findViewById(R.id.takePictureButton);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				preview.takePicture(pictureCallback);
			}
		});
		
	}
	
	

	@Override
	protected void onResume() {
		preview.setCameraDisplayOrientation(this);

		super.onResume();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
