package cl.inexcell.sistemadegestion;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
	private String Phone;
	private ArrayList<String> res;
	private ListView listView;
	private CertificarDSL asyncDSL;
	ProgressBar progressBar;
	
	private String asd;
	
	private boolean certifyDslCorrecto;

    Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_certificar);
        context = this;
		init();

		tipo.setText("Certificación");

		Phone = getIntent().getStringExtra("PHONE");
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

        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        State state3g = conMan.getNetworkInfo(0).getState();
        State stateWifi = conMan.getNetworkInfo(1).getState();
        if (state3g == NetworkInfo.State.CONNECTED || stateWifi == NetworkInfo.State.CONNECTED)
        {
            tdown.setText("");
            tup.setText("");
            testado.setVisibility(View.GONE);
            dslContent.setAdapter(null);
            dslContent.setVisibility(View.GONE);
            wifiContent.setVisibility(View.GONE);
            certificar();
        }else {
            Toast.makeText(getApplicationContext(), "Error: \nNo hay conexión a internet", Toast.LENGTH_SHORT).show();
        }

	}
	public void fin_certificar(View v){
		volver(null);
	}
	

	public void shutdown1(View v){
        if(VistaTopologica.topo != null)
		    VistaTopologica.topo.finish();
        if(Principal.p != null)
		    Principal.p.finish();
		finish();
	}



    private class SpeedTest extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                String DownloadUrl = "http://madgoatstd.com/pipe/test.jpg";
                String UploadUrl = "http://madgoatstd.com/pipe/upload.php";
                String fileName = "testfile.jpg";


                File dir = new File (context.getFilesDir() + "/temp/");
                if(!dir.exists()) {
                    if(dir.mkdir()){
                        Log.d(TAG, "Se creo la carpeta correctamente");
                    }
                }

                publishProgress("D","0","Preparando descarga...", null);

                URL url = new URL(DownloadUrl); //you can write here any link
                File file = new File(context.getFilesDir() + "/temp/" + fileName);

                Log.d("DownloadManager", "URL:" + url);
                Log.d("DownloadManager", "Local Filename:" + fileName);
                Log.d("DownloadManager", "Download Start");
                    /* Open a connection to that URL. */
                long conectionTInit = System.currentTimeMillis();
                URLConnection ucon = url.openConnection();
                long ping = System.currentTimeMillis()-conectionTInit;

                //Define InputStreams to read from the URLConnection.
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                Log.d("DownloadManager", "ping: "+ping+" ms");

                //Read bytes to the Buffer until there is nothing more to read(-1).
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int currentBytesDownloaded = 0;
                int current;
                int b = 2097152;
                byte[] buf = new byte[1024];

                double rate = 0;

                double downloadStartTime = System.currentTimeMillis();
                long rxBytesBefore = TrafficStats.getTotalRxBytes();
                String progress = String.valueOf((currentBytesDownloaded)*100/b);
                publishProgress("D",progress, "Midiendo Descarga...", null);
                while ((current = bis.read(buf)) != -1) {
                    currentBytesDownloaded += current;
                    baos.write(buf,0,current);
                    double trafficActual = (TrafficStats.getTotalRxBytes()- rxBytesBefore)*0.0009765625;
                    double timeActual = (System.currentTimeMillis() - downloadStartTime)/1000;
                    if(trafficActual/timeActual > rate)
                        rate = trafficActual/timeActual;
                    progress = String.valueOf((currentBytesDownloaded)*100/b);
                    publishProgress("D",progress, "Midiendo Descarga...", String.valueOf(currentBytesDownloaded));
                }
                long rxBytesTotal = TrafficStats.getTotalRxBytes()- rxBytesBefore;
                double rxKBytesTotal = rxBytesTotal * 0.0009765625;
                double downloadEndTime = System.currentTimeMillis(); //maybe
                Log.d("DownloadManager", "TopDownspeed: "+rate+" KB/s");
                progress = "0";
                publishProgress("U",progress, "Preparando Subida...", String.valueOf(rate).substring(0,6));
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();



                double totalTimeDownload = downloadEndTime - downloadStartTime;
                double secs = totalTimeDownload/1000;

                Log.d("DownloadManager", "Tiempo total descarga: "+ secs +" segundos");
                Log.d("DownloadManager",rxKBytesTotal/secs+" KB/s");



                double tUploadStart, tUploadTotal;
                HttpURLConnection conn;
                DataOutputStream dos;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead,bytesAvailable, bufferSize;
                File done = new File(context.getFilesDir() + "/temp/" + fileName);
                if(!done.isFile())
                    Log.e("DownloadManager","no existe");
                else{
                    FileInputStream fileInputStream = new FileInputStream(done);
                    url = new URL(UploadUrl);

                    conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection","Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
                    conn.setRequestProperty("uploaded_file",fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens+boundary+lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+fileName+"\""+lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable,1*1024*1024);
                    buf = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buf,0,bufferSize);

                    while(bytesRead > 0){

                        dos.write(buf,0,bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable,1*1024*1024);
                        bytesRead = fileInputStream.read(buf,0,bufferSize);

                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);
                    progress = "20";
                    publishProgress("U",progress, "Midiendo Subida...", null);
                    long trafficUploadBefore = TrafficStats.getTotalTxBytes();
                    tUploadStart = System.currentTimeMillis();
                    int serverResponseCode = conn.getResponseCode();
                    publishProgress("U","65", "Midiendo Subida...", null);
                    String serverResponseMessage = conn.getResponseMessage();

                    tUploadTotal = (System.currentTimeMillis() - tUploadStart)/1000;

                    double totalBytesUpload = (TrafficStats.getTotalTxBytes() - trafficUploadBefore) * 0.0009765625;
                    publishProgress("U","100", "Ok.", String.valueOf(totalBytesUpload/tUploadTotal).substring(0,6));
                    Log.d("UploadManager", totalBytesUpload/tUploadTotal+"KB/s");

                    Log.i("UploadManager", "HTTP response is: "+serverResponseMessage+": "+serverResponseCode);

                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    if(done.delete()){
                        Log.d(TAG, "Archivo eliminado");
                    }else{
                        Log.w(TAG, "Error al eliminar archivo");
                    }

                }




            } catch (Exception e) {
                Log.d("TAG", "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar)findViewById(R.id.progressBar);
            progressBar.setMax(100);
            progressBar.setVisibility(View.VISIBLE);
            testado.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            testado.setVisibility(View.GONE);
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            testado.setText(values[2]);
            if(values[0].equals("D")){
                progressBar.setProgress(Integer.parseInt(values[1]));

            }
            if(values[0].equals("U")){
                if(values[1].equals("0")){
                    tdown.setText(values[3] + " KB/s");
                }
                progressBar.setProgress(Integer.parseInt(values[1]));
                if(values[1].equals("100")){
                    tup.setText(values[3]+ " KB/s");
                }
            }


        }
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
   	    	
 			String respuesta;
   			res = new ArrayList<>();
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

 			   
 			if(certifyDslCorrecto)
 			{ 				 
 				items_certify = new ArrayList<>();
 				dsl.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_bottom1, 0,R.drawable.ok,0);
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
 			   if(!certifyDslCorrecto)
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
            State stateWifi = conMan.getNetworkInfo(1).getState();

            if(stateWifi == NetworkInfo.State.CONNECTED){
                WifiManager wman = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                WifiInfo winfo = wman.getConnectionInfo();
                testado.setVisibility(View.VISIBLE);
                wifi.setText(" Wifi: "+winfo.getSSID());
                wifi.setVisibility(View.VISIBLE);
                new SpeedTest().execute();
            }
            else {
                wifi.setVisibility(View.GONE);
                wifiContent.setVisibility(View.GONE);
            }


 			if (this.dialog.isShowing()) {
 		        this.dialog.dismiss();
 		     }
   	    }
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
