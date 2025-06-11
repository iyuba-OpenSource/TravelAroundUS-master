package com.iyuba.concept2.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.iyuba.concept2.listener.DownLoadCallBack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageGetFromHttp implements DownLoadCallBack{
	private static final String LOG_TAG = "ImageGetFromHttp";
    
    public static Bitmap downloadBitmap(String url) {
        final HttpClient client = new DefaultHttpClient();
        url= url.replaceAll(" ", "%20");
        url=url.trim();
        final HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w(LOG_TAG, "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }
                                                                   
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    FilterInputStream fit = new FlushedInputStream(inputStream);
                    /* BitmapFactory.Options ops=new BitmapFactory.Options();
                    ops.inSampleSize = 2;
                    Log.d("from", "net");
                    return  BitmapFactory.decodeStream(fit, null, ops);*/
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] b = new byte[1024];
					int len = 0;
					while ((len = fit.read(b, 0, 1024)) != -1) {
						baos.write(b, 0, len);
						baos.flush();
					}
					fit.close();
					fit=null;
					byte[] bytes = baos.toByteArray();
					// 图片全部下载为字节流再decode，否则图片太大会出现无法显示的问题，decode失败。
					BitmapFactory.Options ops=new BitmapFactory.Options();
                    ops.inSampleSize = 2;
                    Log.d("from", "net");
					return BitmapFactory.decodeByteArray(bytes, 0,
							bytes.length,ops);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            client.getConnectionManager().shutdown();
        }
        return null;
    }
    
   
    public static Bitmap downloadBitmap(String url, int inSampleSize) {
        final HttpClient client = new DefaultHttpClient();
        url= url.replaceAll(" ", "%20");
        url=url.trim();
        final HttpGet getRequest = new HttpGet(url);
                                                               
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w(LOG_TAG, "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }
                                                                   
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    FilterInputStream fit = new FlushedInputStream(inputStream);
                    BitmapFactory.Options ops=new BitmapFactory.Options();
                    ops.inSampleSize = inSampleSize  ;
                    return  BitmapFactory.decodeStream(fit, null, ops);
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            client.getConnectionManager().shutdown();
        }
        return null;
    }
    
  
                                                       
    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }
                                                       
        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }



	@Override
	public void downLoadSuccess(Bitmap image) {
		
	}


	@Override
	public void downLoadFaild(String error) {
		
	}

}
