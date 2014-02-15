package jp.dip.firstnote.openhackday2.postPhoto;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.NameValuePair;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class photopost {
	public static final String server_url = "http://210.140.146.61/up.php";
	
	private static final String BOUNDARY = "----------V2ymHFg03ehbqgZCaKO6jy";
	private String url;
	private List<NameValuePair> postData;
	private String imageName = "upfile";
	private String image_path = "tmp.jpg";
	private Bitmap image_data = null;

	public photopost(List<NameValuePair> postData, Bitmap image) {
		this.url = server_url;
		this.postData = postData;
		this.image_data = image;
	}

	public String send() {
		URLConnection conn = null;
		String res = null;
		try {
			conn = new URL(url).openConnection();
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			// User Agentの設定はAndroid1.6の場合のみ必要
			conn.setRequestProperty("User-Agent", "Android");
			// HTTP POSTのための設定
			((HttpURLConnection) conn).setRequestMethod("POST");
			conn.setDoOutput(true);
			// HTTP接続開始
			conn.connect();
			// send post data
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			image_data.compress(CompressFormat.JPEG, 80, bao);
			
			OutputStream os = conn.getOutputStream();
			os.write(createBoundaryMessage().getBytes());
			//os.write(getImageBytes(file));
			os.write(bao.toByteArray());
			String endBoundary = "\r\n--" + BOUNDARY + "--\r\n";
			os.write(endBoundary.getBytes());
			os.close();
			// get response
			InputStream is = conn.getInputStream();
			res = convertToString(is);
		} catch (Exception e) {
			Log.d("HttpMultipartRequest:", e.getMessage());
		} finally {
			if (conn != null)
				((HttpURLConnection) conn).disconnect();
		}
		return res;
	}

	private String createBoundaryMessage() {
		StringBuffer res = new StringBuffer("--").append(BOUNDARY).append(
				"\r\n");
		for (NameValuePair nv : postData) {
			res.append("Content-Disposition: form-data; name=\"")
					.append(nv.getName()).append("\"\r\n").append("\r\n")
					.append(nv.getValue()).append("\r\n").append("--")
					.append(BOUNDARY).append("\r\n");
		}
		String fileType = "image/jpg";
		res.append("Content-Disposition: form-data; name=\"").append(imageName)
				.append("\"; filename=\"").append(image_path).append("\"\r\n")
				.append("Content-Type: ").append(fileType).append("\r\n\r\n");
		return res.toString();
	}

	private byte[] getImageBytes(File file) {
		byte[] b = new byte[10];
		FileInputStream fis = null;
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try {
			fis = new FileInputStream(file);
			while (fis.read(b) > 0) {
				bo.write(b);
			}
		} catch (FileNotFoundException e) {
			Log.d("HttpMultipartRequest:", e.getMessage());
		} catch (IOException e) {
			Log.d("HttpMultipartRequest:", e.getMessage());
		} finally {
			try {
				bo.close();
			} catch (IOException e) {
			}
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}
		return bo.toByteArray();
	}

	private String convertToString(InputStream stream) {
		InputStreamReader streamReader = null;
		BufferedReader bufferReader = null;
		try {
			streamReader = new InputStreamReader(stream, "UTF-8");
			bufferReader = new BufferedReader(streamReader);
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = bufferReader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			return builder.toString();
		} catch (UnsupportedEncodingException e) {
			Log.e("HttpMultipartRequest:", e.getMessage());
		} catch (IOException e) {
			Log.e("HttpMultipartRequest:", e.toString());
		} finally {
			try {
				stream.close();
				if (bufferReader != null)
					bufferReader.close();
			} catch (IOException e) {
				//
			}
		}
		return null;
	}
}
