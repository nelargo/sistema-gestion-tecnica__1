package cl.inexcell.sistemadegestion;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class Certificar_Wifi extends Activity {
	
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	public static final int DIALOG_DOWNLOAD_PROGRESS1 = 1;
	public static final int DIALOG_DOWNLOAD_PROGRESS2 = 2;
    private ProgressDialog mProgressDialog, mProgressDialog1;
    private TextView tdown,tup,test_wsdl;
    
    private TextView test;
    private Button startBtn;
	private Button btnWSDL;
        
    
    public void onCreate(Bundle savedInstanceState) {
         
	   super.onCreate(savedInstanceState);
	   
	   // Activity sin parte superior
	   requestWindowFeature(Window.FEATURE_NO_TITLE);
	   setContentView(R.layout.activity_certificar_wifi);
	   
	   tdown = (TextView)findViewById(R.id.tdown);
	   tup = (TextView)findViewById(R.id.tup);
	   startBtn = (Button)findViewById(R.id.startBtn);
	   
	   
	   ///////////////////////////////////////////////////////////////////////////////
	   
	   test_wsdl = (TextView)findViewById(R.id.test_wsdl);
	   btnWSDL = (Button)findViewById(R.id.btnWSDL);

	   
	   
	   
	   ///////////////////////////////////////////////////////////////////////////////
	   
	   TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	   
	   test = (TextView)findViewById(R.id.test);
	   test.setText("\nDatos Telefono:\nIMEI: "+telephonyManager.getDeviceId()
			   +"\nIMSI: "+telephonyManager.getSimSerialNumber());
	   
	   
	   //////////////////////////////////////////////////////////////
	   
	   startBtn.setOnClickListener(new OnClickListener()
	   {
	       	public void onClick(View v) 
	       	{	       			       		
	       		try
	       		{	
	       			
					startDownload();
		       		muestra_descarga(0);
		       		


				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}	
	       		
	       	}
       	});
    }
    

    
        
    // Clase Asincrona para descargar archivo    
    private void startDownload() {
        String url = "http://www.wallpaperhdphoto.com/Full-HD-1080p-Wallpaper/images/Full%20HD%201080p%20Wallpaper%2043.jpg";
        //String url = "http://alumnos.inf.utfsm.cl/~abastias/upload/upload/test.jpg";
        new DownloadFileAsync().execute(url);
    }
    
    // Clase Asincrona para subir archivo
    private void startUpload() {
    	String archivo_seleccionado = "/sdcard/test1.jpg";
    	new UploadFileTask(Certificar_Wifi.this).execute(archivo_seleccionado);
    }


	@SuppressLint("SdCardPath")
	class DownloadFileAsync extends AsyncTask<String, String, String> 
	{

        @SuppressWarnings("deprecation")
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

        try {

	        URL url = new URL(aurl[0]);
	        URLConnection conexion = url.openConnection();
	        conexion.connect();
	
	        int lenghtOfFile = conexion.getContentLength();
	        Log.d("ANDRO_ASYNC", "Largo del archivo: " + lenghtOfFile);
	
	        InputStream input = new BufferedInputStream(url.openStream());
	        OutputStream output = new FileOutputStream("/sdcard/test-download1.jpg");
	
	        byte data[] = new byte[1024];
	
	        long total = 0;
        
        	long tiempoInicio_down = System.currentTimeMillis();
        
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }
            
            long tiempoFin_down = System.currentTimeMillis();
            long tiempo_down = tiempoFin_down - tiempoInicio_down; 
            
            muestra_descarga(tiempo_down);

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {}
        return null;

        }
        

		protected void onProgressUpdate(String... progress) {
             Log.d("ANDRO_ASYNC",progress[0]);
             mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @SuppressWarnings("deprecation")
		@Override
        protected void onPostExecute(String unused) {
            startUpload();
            muestra_carga(0);
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }
    
	
	//http://stackoverflow.com/questions/2017414/post-multipart-request-with-android-sdk
	class UploadFileTask extends AsyncTask<String,String,String> {
	
		private Activity activity;
		
		public UploadFileTask(Activity activity){
			this.activity = activity;
		}
		
		@SuppressWarnings("deprecation")
		protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS1);
        }
		
	    protected String doInBackground(String... path) {
            
	    	String outPut = null;
	    	long tiempo_up = 0, tiempoInicio_up,tiempoFin_up;
	    	
	    	try {
	    		
	    		for (String sdPath : path) {
		    		Bitmap bitmapOrg = BitmapFactory.decodeFile(sdPath);
	                ByteArrayOutputStream bao = new ByteArrayOutputStream();
	                 
	                //Resize the image
	                double width = bitmapOrg.getWidth();
	                double height = bitmapOrg.getHeight();
	                double ratio = 400/width;
	                int newheight = (int)(ratio*height);
	                 
	                System.out.println("----width" + width);
	                System.out.println("----height" + height);
	                 
	                //bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, 400, newheight, true);
	                 
	                //Here you can define .PNG as well
	                bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 95, bao);
	                byte[] ba = bao.toByteArray();
	                String ba1 = Base64.encodeToString(ba, TRIM_MEMORY_COMPLETE);
	                 
	                System.out.println("uploading image now ---" + ba1);
		    		
		    		
		    		
		    		tiempoInicio_up = System.currentTimeMillis();
		    		
		    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	                //nameValuePairs.add(new BasicNameValuePair("image", path[0]));
		    		nameValuePairs.add(new BasicNameValuePair("image", ba1));
			    	
		    		HttpClient httpclient = new DefaultHttpClient();
	                HttpPost httppost = new HttpPost("http://www.google.com");
	                //HttpPost httppost = new HttpPost("http://alumnos.inf.utfsm.cl/~abastias/upload/upload1.php");
	                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	                 
	                HttpResponse response = httpclient.execute(httppost);
	                HttpEntity entity = response.getEntity();                
	
	                // print responce
	                outPut = EntityUtils.toString(entity);
	                Log.i("GET RESPONSE--", outPut);
	                 
	                //is = entity.getContent();
	                Log.e("log_tag ******", "good connection");	            
		            tiempoFin_up = System.currentTimeMillis();
		            
		            tiempo_up = tiempoFin_up - tiempoInicio_up;
		            
		            muestra_carga(tiempo_up);
	    		}
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    	
			return outPut;
        }      
	
	
		protected void onProgressUpdate(String... progress) {
	        Log.d("ANDRO_ASYNC",progress[0]);
	        mProgressDialog1.setProgress(Integer.parseInt(progress[0]));
	   }
	    
	    @SuppressWarnings("deprecation")
		protected void onPostExecute(String feed) {
	    	dismissDialog(DIALOG_DOWNLOAD_PROGRESS1);
	    	Toast.makeText(activity, feed,
			        Toast.LENGTH_SHORT).show();
	    }
	 }
	

	 @SuppressWarnings("rawtypes")
	 class FileUploadResponseHandler implements ResponseHandler {
	
	    @Override
	    public Object handleResponse(HttpResponse response)
	            throws ClientProtocolException, IOException {
	
	        HttpEntity r_entity = response.getEntity();
	        String responseString = EntityUtils.toString(r_entity);
	        Log.d("UPLOAD", responseString);
	
	        return responseString;
	    }
	
	}

	
	// Dialog Descarga
    @Override
    protected Dialog onCreateDialog(int id) 
    {
        switch (id) 
        {
	        case DIALOG_DOWNLOAD_PROGRESS:
	            mProgressDialog = new ProgressDialog(this);
	            mProgressDialog.setTitle("Certificación Wifi");
	            mProgressDialog.setIcon(R.drawable.ic_wifi);
	            mProgressDialog.setMessage("Midiendo Descarga ...");
	            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            mProgressDialog.setCancelable(true);
	            mProgressDialog.show();
	            return mProgressDialog;
	            
	        case DIALOG_DOWNLOAD_PROGRESS1:
	            mProgressDialog1 = new ProgressDialog(this);
	            mProgressDialog1.setTitle("Certificación Wifi");
	            mProgressDialog1.setIcon(R.drawable.ic_wifi);
	            mProgressDialog1.setMessage("Midiendo Descarga y Subida");
	            mProgressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            mProgressDialog1.setCancelable(true);
	            mProgressDialog1.show();
	            return mProgressDialog1;    
	        default:
	            return null;
      
        }
    }
    
	public void muestra_descarga(long tiempo_down) 
	{
		//String resultado=String.valueOf(tiempo_down);
		//long size_jpg = 1065297;
		long size_jpg = 56237;
		float bw;
		bw = (float)(size_jpg*8)/(float) (tiempo_down*1000);
		DecimalFormat df = new DecimalFormat("0.00");
		String res_bw = df.format(bw);
        tdown.setText("\nAncho de Banda:\nDescarga: "+res_bw+" Mbps");
	}
	
	public void muestra_carga(long tiempo_down) 
	{
		//String resultado=String.valueOf(tiempo_down);
		//long size_jpg = 239125;
		long size_jpg = 56237;
		float bw;
		bw = (float)(size_jpg*8)/(float) (tiempo_down*1000);
		DecimalFormat df = new DecimalFormat("0.00");
		String res_bw = df.format(bw);
        tup.setText("Subida: "+res_bw+" Mbps");
	}
	
	/*
	 * Parser XML
	 */
	
	

	public void volver(View view) {
    	finish();
    	
    	// Vibrar al hacer click        
        Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }
}