package jp.dip.firstnote.openhackday2.postPhoto;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import jp.dip.firstnote.openhackday2.R;
import jp.dip.firstnote.openhackday2.R.id;
import jp.dip.firstnote.openhackday2.R.layout;
import jp.dip.firstnote.openhackday2.R.menu;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoPreviewActivity extends Activity {
	
	public final static String INTENT_BITMAP = "preview_bitmap";
	public Bitmap post_picture = null;
	public Runnable post_runner = null;
	
	private Button postButton = null;
	private Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_preview);
		
		handler = new Handler();
		
		post_runner = new Runnable(){
			@Override
			public void run() {
				ArrayList<NameValuePair> postdata = new ArrayList<NameValuePair>();
				photopost poster = new photopost(postdata, post_picture);
				final String response = poster.send();
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(PhotoPreviewActivity.this, "recieved response:" + response, Toast.LENGTH_LONG).show();
						postButton.setEnabled(true);
					}
				});
				
			}
		};
		
		
		Intent intent = getIntent();
		post_picture = (Bitmap)intent.getParcelableExtra(INTENT_BITMAP);
		
		ImageView imageView = (ImageView)findViewById(R.id.imageView1);
		imageView.setImageBitmap(post_picture);
		
		postButton = (Button)findViewById(R.id.postPhoto);
		postButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				new Thread(post_runner).start();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_preview, menu);
		return true;
	}

}
