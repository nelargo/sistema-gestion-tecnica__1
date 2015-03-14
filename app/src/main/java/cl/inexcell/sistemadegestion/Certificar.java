package cl.inexcell.sistemadegestion;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RawRes;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Certificar extends Activity {
	private String TAG	= "CERTIFICAR";
	private Button wifi, dsl;
	private TextView tipo;
	private LinearLayout wifiContent;
	private ListView	dslContent;
	private ArrayList<itemList> items_certify;
	private TextView tdown, tup, testado;
	private String Phone, Type, Value;
	private ArrayList<String> res;
	private ListView listView;
	private CertificarDSL asyncDSL;
	
	
	private String asd;

    private static final double BYTE_TO_KILOBIT = 0.0078125;
    private static final double KILOBIT_TO_MEGABIT = 0.0009765625;

    private final int MSG_UPDATE_STATUS=0;
    private final int MSG_COMPLETE_STATUS=1;
    private final int MSG_COMPLETE_UPLOAD_STATUS=3;
    private final int MSG_COMPLETE_UPLOAD_START=2;

    private final static int UPDATE_THRESHOLD=300;
    private static final int EXPECTED_SIZE_IN_BYTES = 319016;//1MB 1024*1024

    private DecimalFormat mDecimalFormater;
	
	private boolean certifyDslCorrecto;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Activity sin parte superior
        requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_certificar);
        mDecimalFormater=new DecimalFormat("##.##");
		init();

		tipo.setText("Certificación");
        /*if(t == 0) tipo.setText("Consultas y Pruebas");
		if(t == 1) tipo.setText("Instalación");
		if(t == 2) tipo.setText("Reparación");
		if(t == 3) tipo.setText("Error");*/
		
		
		
		//Type = "0"+String.valueOf(getIntent().getIntExtra("TIPO",3));
		Phone = getIntent().getStringExtra("PHONE");
		//Value = getIntent().getStringExtra("VALUE");
        Log.i(TAG, Phone);
		certificar();
		
	}
	
	public void certificar(){
		
		asyncDSL = new CertificarDSL();
		asyncDSL.execute();
	}
	
	public void init(){
		wifi	= (Button)findViewById(R.id.btnCert_wifi);
		dsl 	= (Button)findViewById(R.id.btnCert_dsl);
		tipo	= (TextView)findViewById(R.id.cert_titulo);
		
		wifiContent = (LinearLayout)findViewById(R.id.LCert_contWifi);
		dslContent = (ListView)findViewById(R.id.lvCert_dslList);
		
		tdown 	=(TextView)findViewById(R.id.tvCert_bajada);
		tup 	=(TextView)findViewById(R.id.tvCert_subida);
        testado =(TextView)findViewById(R.id.tvCert_estado);
	}
	
	public void mostrar_wifi(View v){
		if(wifiContent.getVisibility() == View.GONE){
			wifiContent.setVisibility(View.VISIBLE);
			dslContent.setVisibility(View.GONE);
		}else{
			wifiContent.setVisibility(View.GONE);
		}
	}
	public void mostrar_dsl(View v){
		if(dslContent.getVisibility() == View.GONE){
			dslContent.setVisibility(View.VISIBLE);
			wifiContent.setVisibility(View.GONE);
		}else{
			dslContent.setVisibility(View.GONE);
		}
	}
	public void volver(View view){
		this.finish();
	}
	
	public void certificarAgain(View v){
		/*Intent i = new Intent(VistaTopologica.topo,Certificar.class);
		i.putExtra("TIPO", Type);
		i.putExtra("PHONE", Phone);
		i.putExtra("VALUE", Value);
		startActivity(i);
		this.finish();*/
        tdown.setText("");
        tup.setText("");
        testado.setVisibility(View.GONE);
        dslContent.setAdapter(null);
        dslContent.setVisibility(View.GONE);
        wifiContent.setVisibility(View.GONE);
        certificar();
	}
	public void fin_certificar(View v){
		volver(null);
	}
	

	public void shutdown1(View v){
		VistaTopologica.topo.finish();
		Principal.p.finish();
		finish();
	}
	

	
	/*
   	 * Consulta Asincronica CERTIFY DSL
   	 */
   	
   	private class CertificarDSL extends AsyncTask<String,Integer,ArrayList<String>> {
   		
   		private final ProgressDialog dialog = new ProgressDialog(Certificar.this);
   		
 		protected void onPreExecute() {
 			this.dialog.setMessage("Certificando línea telefónica...");
 			this.dialog.setCanceledOnTouchOutside(false);
 			this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Operación Interrumpida.", Toast.LENGTH_SHORT).show();
					
					Certificar.this.finish();
				}
			});
 		    this.dialog.show();
         }
   		 
   	    protected ArrayList<String> doInBackground(String... params) {
   	    	
 			String respuesta = null;
   			res = new ArrayList<String>();
   			nada();
   			try {

   				TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
   				String IMEI = telephonyManager.getDeviceId();
   				String IMSI =  telephonyManager.getSimSerialNumber();

   				if(Phone.equals("2"))
   					respuesta = asd;
   				else
   					respuesta = SoapRequestMovistar.getCertifyDSL(Phone,"","",IMEI, IMSI);

                if(!XMLParser.getReturnCode(respuesta).get(0).equals("0"))
                    certifyDslCorrecto = false;
   				else{
                    certifyDslCorrecto = true;
                    res = XMLParser.getCertification(respuesta);
                }

   				
   				
   			} catch (Exception e1) {
   				e1.printStackTrace();
   			}   			
   	        return res;
   	    }
   	    

 		protected void onPostExecute(ArrayList<String> result) {

 			   
 			if(certifyDslCorrecto == true)
 			{ 				 
 				items_certify = new ArrayList<itemList>();
// 				Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ok); 		
 				dsl.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_bottom1, 0,R.drawable.ok,0);
 				//String linea="";
 				for(int i = 1; i<res.size();i++){ 						
 					if(res.get(i).split(";")[1].compareTo("OK") == 0)
 						items_certify.add(new itemList(res.get(i).split(";")[0],"",R.drawable.ok));
 					else{
 						items_certify.add(new itemList(res.get(i).split(";")[0],"",R.drawable.error));
 						certifyDslCorrecto = false;
 					}
 				}
 				
 				
 				listView = (ListView) findViewById(R.id.lvCert_dslList);
 				listAdapter adapter = new listAdapter(getApplicationContext(), items_certify);
 				
 			    listView.setAdapter(adapter);
 			    listView.setOnItemClickListener(new OnItemClickListener() {
 			    	  @Override
 			    	  public void onItemClick(AdapterView<?> parent, View view,
 			    	    int position, long id) {
                          AlertDialog.Builder dialog = new AlertDialog.Builder(Certificar.this);
	 			    	  dialog.setTitle(res.get(position+1).split(";")[0]);
	 			    	  dialog.setMessage(res.get(position+1).split(";")[3]);
	 			    	  dialog.setCancelable(false);
	 			    	  dialog.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								
							}
						});
	 			    	  dialog.show();
 			    	  }
 			    	});
 			   if(certifyDslCorrecto == false)
 	 			{
 				   dsl.setText("Banda Ancha: "+Phone);
 				   dsl.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_bottom1, 0,R.drawable.error,0);
 	 			}
 			}
 			else
 			{ 				
 				//Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.error); 				 				 
 				dsl.setCompoundDrawablesWithIntrinsicBounds( 0, 0,R.drawable.error,0);
 				dsl.setText("Banda Ancha: Error de Conexión");
 				//finish.setEnabled(false);
 				Toast.makeText(getApplicationContext(), "No se pudo realizar la certificación.", Toast.LENGTH_SHORT).show();
 				
 			}

            ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            State state3g = conMan.getNetworkInfo(0).getState();
            State stateWifi = conMan.getNetworkInfo(1).getState();

            if(stateWifi == NetworkInfo.State.CONNECTED){
                WifiManager wman = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                WifiInfo winfo = wman.getConnectionInfo();
                testado.setVisibility(View.VISIBLE);
                new Thread(mWorker).start();
                wifi.setText(" Wifi: "+winfo.getSSID());
                wifi.setVisibility(View.VISIBLE);
            }
            else
                wifi.setVisibility(View.GONE);


 			if (this.dialog.isShowing()) {
 		        this.dialog.dismiss();
 		     }
   	    }
   	}


    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            switch(msg.what){
                case MSG_UPDATE_STATUS:
                    testado.setText("Miediendo descarga...");
                    final SpeedInfo info1=(SpeedInfo) msg.obj;
                    tdown.setText(mDecimalFormater.format(info1.kilobits)+" kbit/sec");
                    // Title progress is in range 0..10000
                    setProgress(100 * msg.arg1);
                    break;
                case MSG_COMPLETE_STATUS:
                    testado.setText("Descarga OK.");
                    final  SpeedInfo info2=(SpeedInfo) msg.obj;
                    tdown.setText(info2.kilobits+" kbit/sec");
                    //new Thread(upmWorker).start();
                    setProgressBarVisibility(false);
                    break;
                case MSG_COMPLETE_UPLOAD_START:
                    testado.setText("Mididendo Subida...");
                    tup.setText("-- kbit/sec");
                    break;
                case MSG_COMPLETE_UPLOAD_STATUS:
                    testado.setText("Finalizado.");
                    final  String info4=(String) msg.obj;
                    tup.setText(info4+" kbit/sec");
                    setProgressBarVisibility(false);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    /**
     * Our Slave worker that does actually all the work
     */
    private final Runnable mWorker=new Runnable(){

        @Override
        public void run() {
            InputStream stream=null;
            try {
                int bytesIn=0;
                //String downloadFileUrl="https://pcba.telefonicachile.cl/smartphone/image.JPEG";
                //String downloadFileUrl="http://www.wallpaperhdphoto.com/Full-HD-1080p-Wallpaper/images/Full%20HD%201080p%20Wallpaper%2043.jpg";
                String downloadFileUrl="http://madgoatstd.com/pipe/upload/test2.png";
                long startCon=System.currentTimeMillis();
                URL url=new URL(downloadFileUrl);
                URLConnection con=url.openConnection();
                con.setUseCaches(false);
                long connectionLatency=System.currentTimeMillis()- startCon;
                stream=con.getInputStream();

                /*Message msgUpdateConnection=Message.obtain(mHandler, MSG_UPDATE_CONNECTION_TIME);
                msgUpdateConnection.arg1=(int) connectionLatency;
                mHandler.sendMessage(msgUpdateConnection);*/

                long start=System.currentTimeMillis();
                int currentByte=0;
                long updateStart=System.currentTimeMillis();
                long updateDelta=0;
                int  bytesInThreshold=0;

                while((currentByte=stream.read())!=-1){
                    bytesIn++;
                    bytesInThreshold++;
                    if(updateDelta>=UPDATE_THRESHOLD){
                        int progress=(int)((bytesIn/(double)EXPECTED_SIZE_IN_BYTES)*100);
                        Message msg=Message.obtain(mHandler, MSG_UPDATE_STATUS, calculate(updateDelta, bytesInThreshold));
                        msg.arg1=progress;
                        msg.arg2=bytesIn;
                        mHandler.sendMessage(msg);
                        //Reset
                        updateStart=System.currentTimeMillis();
                        bytesInThreshold=0;
                    }
                    updateDelta = System.currentTimeMillis()- updateStart;
                }

                long downloadTime=(System.currentTimeMillis()-start);
                //Prevent AritchmeticException
                if(downloadTime==0){
                    downloadTime=1;
                }

                Message msg=Message.obtain(mHandler, MSG_COMPLETE_STATUS, calculate(downloadTime, bytesIn));
                msg.arg1=bytesIn;
                mHandler.sendMessage(msg);
            }
            catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }finally{
                try {
                    if(stream!=null){
                        stream.close();
                    }
                } catch (IOException e) {
                    //Suppressed
                }
            }

            String outPut = null;
            long tiempo_up = 0, tiempoInicio_up,tiempoFin_up;
            try {
                Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.raw.test2);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();

                //Resize the image
                double width = bitmapOrg.getWidth();
                double height = bitmapOrg.getHeight();
                double ratio = 400/width;
                int newheight = (int)(ratio*height);

                bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, 400, newheight, true);

                //Here you can define .PNG as well
                bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 95, bao);
                byte[] ba = bao.toByteArray();
                String ba1 = Base64.encodeToString(ba, TRIM_MEMORY_COMPLETE);

                Message msg=Message.obtain(mHandler, MSG_COMPLETE_UPLOAD_START,
                        null);
                mHandler.sendMessage(msg);
                tiempoInicio_up = System.currentTimeMillis();

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //nameValuePairs.add(new BasicNameValuePair("image", path[0]));
                nameValuePairs.add(new BasicNameValuePair("image", ba1));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://madgoatstd.com/pipe/upload.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                // print responce
                outPut = EntityUtils.toString(entity);
                Log.i("GET RESPONSE--", outPut);

                tiempoFin_up = System.currentTimeMillis();

                tiempo_up = tiempoFin_up - tiempoInicio_up;
                msg=Message.obtain(mHandler, MSG_COMPLETE_UPLOAD_STATUS,
                        muestra_carga(tiempo_up));
                mHandler.sendMessage(msg);
            }
            catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }
    };private final Runnable upmWorker=new Runnable(){

        @Override
        public void run() {
            String outPut = null;
            long tiempo_up = 0, tiempoInicio_up,tiempoFin_up;
            try {
                Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.raw.test2);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();

                //Resize the image
                double width = bitmapOrg.getWidth();
                double height = bitmapOrg.getHeight();
                double ratio = 400/width;
                int newheight = (int)(ratio*height);

                bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, 400, newheight, true);

                //Here you can define .PNG as well
                bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 95, bao);
                byte[] ba = bao.toByteArray();
                String ba1 = Base64.encodeToString(ba, TRIM_MEMORY_COMPLETE);




                Message msg=Message.obtain(mHandler, MSG_COMPLETE_UPLOAD_START,
                        null);
                mHandler.sendMessage(msg);
                tiempoInicio_up = System.currentTimeMillis();

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //nameValuePairs.add(new BasicNameValuePair("image", path[0]));
                nameValuePairs.add(new BasicNameValuePair("image", ba1));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://madgoatstd.com/pipe/upload.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                // print responce
                outPut = EntityUtils.toString(entity);
                Log.i("GET RESPONSE--", outPut);

                tiempoFin_up = System.currentTimeMillis();

                tiempo_up = tiempoFin_up - tiempoInicio_up;
                msg=Message.obtain(mHandler, MSG_COMPLETE_UPLOAD_STATUS,
                        muestra_carga(tiempo_up));
                mHandler.sendMessage(msg);
            }
            catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

        }
    };


    public String muestra_carga(long tiempo_down)
    {
        float bw;
        bw = (float)(EXPECTED_SIZE_IN_BYTES*8)/(float) (tiempo_down*1000);
        DecimalFormat df = new DecimalFormat("0.00");

        return df.format(bw);
    }

    /**
     *
     * 1 byte = 0.0078125 kilobits
     * 1 kilobits = 0.0009765625 megabit
     *
     * @param downloadTime in miliseconds
     * @param bytesIn number of bytes downloaded
     * @return SpeedInfo containing current speed
     */
    private SpeedInfo calculate(final long downloadTime, final long bytesIn){
        SpeedInfo info=new SpeedInfo();
        //from mil to sec
        long bytespersecond   =(bytesIn / downloadTime) * 1000;
        double kilobits=bytespersecond * BYTE_TO_KILOBIT;
        double megabits=kilobits  * KILOBIT_TO_MEGABIT;
        info.downspeed=bytespersecond;
        info.kilobits=kilobits;
        info.megabits=megabits;

        return info;
    }

    private static class SpeedInfo{
        public double kilobits=0;
        public double megabits=0;
        public double downspeed=0;
    }
   	
   	public void nada(){
		asd = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Demo\">"+
   "<SOAP-ENV:Body>"+
      "<ns1:ResourceResponse xmlns:ns1=\"urn:Demo\">"+
         "<ResponseResource xsi:type=\"tns:ResponseResource\">"+
            "<Operation xsi:type=\"tns:OperationType\">"+
               "<OperationCode xsi:type=\"xsd:string\">?</OperationCode>"+
               "<OperationId xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
               "<DateTime xsi:type=\"xsd:string\">?</DateTime>"+
               "<IdUser xsi:type=\"xsd:string\">?</IdUser>"+
               "<IMEI xsi:type=\"xsd:string\">?</IMEI>"+
               "<IMSI xsi:type=\"xsd:string\">?</IMSI>"+
            "</Operation>"+
           "<Service xsi:type=\"tns:ServiceCertifyDSLOut\">"+
              "<CertifyDSL xsi:type=\"tns:CertifyDSLOut\">"+
                 "<Output xsi:type=\"tns:CertifyDSLOutData\">"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">NOMBRE CLIENTE</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">NUNEZ N SANDRA IRENE</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">DIRECCION</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">LOS FRESNOS 380, S FRANCISC</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">ESTADO PUERTA (SINCRONISMO)</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">SINCRONIZADO-DESBLOQUEADO</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">PERFIL CONFIGURADO</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">BA4000_ASSIA_16_4</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">VELOCIDAD ACTUAL DE SUBIDA</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">672 (Kb)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">VELOCIDAD ACTUAL DE BAJADA</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">4800 (Kb)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">VELOCIDAD MAXIMA DE SUBIDA</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">928 (Kb)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">VELOCIDAD MAXIMA DE BAJADA</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">6816 (Kb)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">SENAL A RUIDO DE SUBIDA</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">14 (dB)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">SENAL A RUIDO DE BAJADA</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">13 (dB)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">ATENUACION UPSTREAM</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">14 (dB)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">ATENUACION DOWNSTREAM</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">22 (dB)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">OCUPACION UPSTREAM</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">72 (%)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">OCUPACION DOWNSTREAM</Name>"+
                       "<Value xsi:type=\"xsd:string\">OK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">72 (%)</Description>"+
                    "</CertifyParameter>"+
                    "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">"+
                       "<Name xsi:type=\"xsd:string\">NASPORT</Name>"+
                       "<Value xsi:type=\"xsd:string\">NOK</Value>"+
                       "<Code xsi:type=\"xsd:string\">001</Code>"+
                       "<Description xsi:type=\"xsd:string\">La prueba no se ha realizado desde el acceso provicionado al cliente.</Description>"+
                    "</CertifyParameter>"+
                    "<Return xsi:type=\"tns:ReturnType\">"+
                       "<Code xsi:type=\"xsd:string\">0</Code>"+
                       "<Description xsi:type=\"xsd:string\">OK: DATOS DE PRUEBA DE CERTIFICACION [72-2491362]</Description>"+
                    "</Return>"+
                 "</Output>"+
              "</CertifyDSL>"+
           "</Service>"+
        "</ResponseResource>"+
     "</ns1:ResourceResponse>"+
  "</SOAP-ENV:Body>"+
"</SOAP-ENV:Envelope>";
	}
}
