package cl.inexcell.sistemadegestion;


import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class Principal extends Activity  {
	public static Activity p;
	

	private static final String TAG = "Principal Activity";
	LocationManager locationManager;
	private EditText phone;
	
	private String asd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"INICIANDO APLICACION");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_principal);		
		p = this;
		phone = (EditText)findViewById(R.id.etPpal_telefono);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		/** Se inicia el DEMONIO **/
		
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			try{
				Intent service = new Intent(this, Demonio_Certificar_3G.class);
				startService(service);
			}catch(Exception e){					
				Toast.makeText(getApplicationContext(), "START SERVICE ERROR", Toast.LENGTH_LONG).show();
			}
		
		}else{
			 new AlertDialog.Builder(this)
		      .setIcon(android.R.drawable.ic_dialog_alert)
		      .setTitle("GPS está desactivado!")
		      .setMessage("Active GPS e reinicie la aplicación.")
		      .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      //Salir
                      Principal.this.finish();
                  }
              }).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_principal, menu);
		return true;
	}
	

	public void show_notificar_averias(View view) {
		
		 ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 	
		 State senal3g = conMan.getNetworkInfo(0).getState();
		 State wifi = conMan.getNetworkInfo(1).getState();
		 
		 if (senal3g == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED)
		 {
			 Intent i = new Intent(this, Notificar_Averias.class );
		     startActivityForResult(i,0);
		        
		        // Vibrar al hacer click
		     Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		     vibrator.vibrate(50);
			 			 
		 }
		 else
		 {
			 Toast.makeText(getApplicationContext(), "No existe conexión a internet para utilizar el Programa", Toast.LENGTH_LONG).show();
		 }  
	}
	
	public void buscar_cliente(View view){
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	 	
		 State senal3g = conMan.getNetworkInfo(0).getState();
		 State wifi = conMan.getNetworkInfo(1).getState();
		 if (senal3g == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED)
		 {
			nada2();
			if(phone.getText().length() == 0 || phone.getText() == null){
				Toast.makeText(getApplicationContext(), "Ingrese télefono del cliente", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Consulta_Resources c = new Consulta_Resources();
			c.execute();		
		 }else
			 {
				 Toast.makeText(getApplicationContext(), "No existe conexión a internet para utilizar el Programa", Toast.LENGTH_LONG).show();
			 }  
	}
	

	
	public void show_plantas_externas(View view) {
		ConnectivityManager conMan = (ConnectivityManager)
			      getSystemService(Context.CONNECTIVITY_SERVICE);

		 State senal3g = conMan.getNetworkInfo(0).getState();
		 State wifi = conMan.getNetworkInfo(1).getState();

		 if (senal3g == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED)
		 {
			 Intent i = new Intent(this, Plantas_Externas.class );
			 startActivityForResult(i,0);
		        // Vibrar al hacer click
		     Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		     vibrator.vibrate(50);

		 }
		 else
		 {
			 Toast.makeText(getApplicationContext(), "No existe conexión a internet para utilizar el Programa", Toast.LENGTH_LONG).show();
		 }
	}

	public void shutdown(View view){
		this.finish();
	}

    public void openFAQ(View view){
        startActivity(new Intent(this,FAQActivity.class));
        //startActivity(new Intent(this,Certificar_Wifi.class));
    }
	
	
    private class Consulta_Resources extends AsyncTask<String,Integer,String> {
   		
   		private final ProgressDialog dialog = new ProgressDialog(Principal.this);
   		private int code;
   		
 		protected void onPreExecute() {
 			this.dialog.setMessage("Buscando Cliente...");
 			this.dialog.setCanceledOnTouchOutside(false);
 			this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Operación Interrumpida.", Toast.LENGTH_SHORT).show();
					
				}
			});
 		    this.dialog.show();
             //super.onPreExecute();
         }
   		 
   	    protected String doInBackground(String... params) {
   	    	Log.i(TAG, "doInBackground");
 			String respuesta = null;
   			
   			try {
   				TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
   				String IMEI = telephonyManager.getDeviceId();
   				String IMSI =  telephonyManager.getSimSerialNumber();
   					String consulta;
   					if(phone.getText().toString().equals("2")) {
                        Log.i(TAG, "Consulta Dummy");
                        Log.i(TAG, asd);
                        consulta = asd;
                    }else
   						consulta = SoapRequestMovistar.getResource(IMEI, IMSI, phone.getText().toString());
   					
   					ArrayList<String> retorno = XMLParser.getReturnCode(consulta);
   					
   					code = Integer.valueOf(retorno.get(0));
   					
   					if(code == 0){
   						respuesta = consulta;
                        Log.i(TAG, retorno.get(1));
   					}else
   						respuesta = retorno.get(1);
   	   				
   			} catch (Exception e1) {
   				e1.printStackTrace();
   			}
   			
   	        return respuesta;
   	    }
   	    

 		protected void onPostExecute(String result) {
 			if(result != null){
 				if(code == 0){
	 				Intent topologica = new Intent(getApplicationContext(), VistaTopologica.class);
	 				topologica.putExtra("PHONE", phone.getText().toString());
	 				topologica.putExtra("RESULT", result);
	 				startActivityForResult(topologica,0);
	 				Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	 			    vibrator.vibrate(50);
 				}else{
 					Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
 				}
 			}else{
 				Toast.makeText(getApplicationContext(),"Error en la conexión.", Toast.LENGTH_LONG).show();
 			}
 			
 			if (this.dialog.isShowing()) {
 		        this.dialog.dismiss();
 		     }
 			
   	    }
   	}

    public void nada2(){
        asd = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Demo\">" +
                "<SOAP-ENV:Body>" +
                    "<ns1:ResourceResponse xmlns:ns1=\"urn:Demo\">" +
                        "<ResponseResource xsi:type=\"tns:ResponseResource\">" +
                            "<Operation xsi:type=\"tns:OperationType\">" +
                                "<OperationCode xsi:type=\"xsd:string\">?</OperationCode>" +
                                "<OperationId xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                                "<DateTime xsi:type=\"xsd:string\">201503031630</DateTime>" +
                                "<IdUser xsi:type=\"xsd:string\">1</IdUser>" +
                                "<IMEI xsi:type=\"xsd:string\">355847057585809</IMEI>" +
                                "<IMSI xsi:type=\"xsd:string\">8956023100065957322</IMSI>" +
                            "</Operation>" +
                            "<Service xsi:type=\"tns:ServiceResourceOut\">" +
                "<Resource xsi:type=\"tns:ResourceOut\">" +
                "<Output xsi:type=\"tns:ResourceOutData\">" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">DIRECCION</Type>" +
                "<Value xsi:type=\"xsd:string\">DIRECCION</Value>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>" +
                "<Value xsi:type=\"xsd:string\">LOS ESTANQUES</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">NUMERO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">9583</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">COMUNA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VITACURA</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">CIUDAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">SANTIAGO</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PLANTA INTERNA</Type>" +
                "<Value xsi:type=\"xsd:string\">PLANTA INTERNA</Value>" +
                "<Gps xsi:type=\"tns:GPSType\">" +
                "<Lat xsi:type=\"xsd:string\"/>" +
                "<Lng xsi:type=\"xsd:string\"/>" +
                "</Gps>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PLANTA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">W056</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">AGREGADOR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">APQ2-PE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">IP AGREGADOR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">10.52.224.1</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">DSLAM</Attribute>" +
                "<Value xsi:type=\"xsd:string\">ARMARIO_A056-1(APOQ)</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">IP DSLAM</Attribute>" +
                "<Value xsi:type=\"xsd:string\">10.100.22.63</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">RACK / SHELF / SLOT / PORT</Attribute>" +
                "<Value xsi:type=\"xsd:string\">0/0/2/9</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TIPO PUERTO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VDSL</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">MDF STB</Attribute>" +
                "<Value xsi:type=\"xsd:string\">08/D/22-AB</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">MDF ADSL</Attribute>" +
                "<Value xsi:type=\"xsd:string\">A/4/P20-L20</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PLANTA EXTERNA</Type>" +
                "<Value xsi:type=\"xsd:string\">PLANTA EXTERNA</Value>" +
                "<Gps xsi:type=\"tns:GPSType\">" +
                "<Lat xsi:type=\"xsd:string\">-33.378</Lat>" +
                "<Lng xsi:type=\"xsd:string\">-70.5439</Lng>" +
                "</Gps>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PLANTA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">APOQ</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">CABLE</Attribute>" +
                "<Value xsi:type=\"xsd:string\">10019</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR PRIMARIO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">19</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ARMARIO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">A56</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>" +
                "<Value xsi:type=\"xsd:string\">LA LLAVERIA</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ALTURA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">001736-056</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR SECUNDARIO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">42</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\">17395</Id>" +
                "<Type xsi:type=\"xsd:string\">CAJA</Type>" +
                "<Value xsi:type=\"xsd:string\">CAJA TERMINAL</Value>" +
                "<Gps xsi:type=\"tns:GPSType\">" +
                "<Lat xsi:type=\"xsd:string\">-33.378</Lat>" +
                "<Lng xsi:type=\"xsd:string\">-70.5439</Lng>" +
                "</Gps>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TIPO DE CAJA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">CAJA</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>" +
                "<Value xsi:type=\"xsd:string\">LOS ESTANQUES</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ALTURA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">009583-000</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR DESDE</Attribute>" +
                "<Value xsi:type=\"xsd:string\">41</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR HASTA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">50</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:type=\"xsd:string\">0</Id>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">1</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">PAR</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">TELEFONO</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">ESTADO</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\"></Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">ESTADO</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">41</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">42</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">00222011551</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">OCUPADO ADSL</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">43</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">44</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">45</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">46</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">47</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">48</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">49</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">50</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">FAMILYSERVICE</Type>" +
                "<Value xsi:type=\"xsd:string\">SERVICIO BANDA ANCHA</Value>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">MODEM</Attribute>" +
                "<Value xsi:type=\"xsd:string\">ZTE ZXDSL 831</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">FAMILYSERVICE</Type>" +
                "<Value xsi:type=\"xsd:string\">SERVICIO TELEVISION</Value>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">DECO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">Echostar DSB 646</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieDeco</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1779748046</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieTarjeta</Attribute>" +
                "<Value xsi:type=\"xsd:string\">324615478</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">DECO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">Echostar DSB 646</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieDeco</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1823552917</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieTarjeta</Attribute>" +
                "<Value xsi:type=\"xsd:string\">324615477</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">FAMILYSERVICE</Type>" +
                "<Value xsi:type=\"xsd:string\">SERVICIO TELEFONIA</Value>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">222011551</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Return xsi:type=\"tns:ReturnType\">" +
                "<Code xsi:type=\"xsd:string\">0</Code>" +
                "<Description xsi:type=\"xsd:string\">OK:  ELEMENTOS DE PLANTA EXTERNA IDENTIFICADOS PARA [222011551]</Description>" +
                "</Return>" +
                "</Output>" +
                "</Resource>" +
                "</Service>" +
                "</ResponseResource>" +
                "</ns1:ResourceResponse>" +
                "</SOAP-ENV:Body>" +
                "</SOAP-ENV:Envelope>";
    }

    public void nada1(){
        asd = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Demo\">"+
                "<SOAP-ENV:Body>"+
                "<ns1:ResourceResponse xmlns:ns1=\"urn:Demo\">"+
                "<ResponseResource xsi:type=\"tns:ResponseResource\">"+
                "<Operation xsi:type=\"tns:OperationType\">"+
                "<OperationCode xsi:type=\"xsd:string\">?</OperationCode>"+
                "<OperationId xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
                "<DateTime xsi:type=\"xsd:string\">201503031630</DateTime>"+
                "<IdUser xsi:type=\"xsd:string\">1</IdUser>"+
                "<IMEI xsi:type=\"xsd:string\">355847057585809</IMEI>"+
                "<IMSI xsi:type=\"xsd:string\">8956023100065957322</IMSI>"+
                "</Operation>"+
                "<Service xsi:type=\"tns:ServiceResourceOut\">"+
                "<Resource xsi:type=\"tns:ResourceOut\">"+
                "<Output xsi:type=\"tns:ResourceOutData\">"+
                "<Element xsi:type=\"tns:ElementType\">"+
                "<Id xsi:type=\"xsd:string\"/>"+
                "<Type xsi:type=\"xsd:string\">DIRECCION</Type>"+
                "<Value xsi:type=\"xsd:string\">DIRECCION</Value>"+
                "<Identification xsi:type=\"tns:IdentificationType\">"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>"+
                "<Value xsi:type=\"xsd:string\">LOS TAMARUGOS</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">NUMERO</Attribute>"+
                "<Value xsi:type=\"xsd:string\">75</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">COMUNA</Attribute>"+
                "<Value xsi:type=\"xsd:string\">TALCA</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">CIUDAD</Attribute>"+
                "<Value xsi:type=\"xsd:string\">TALCA</Value>"+
                "</Parameters>"+
                "</Identification>"+
                "</Element>"+
                "<Element xsi:type=\"tns:ElementType\">"+
                "<Id xsi:type=\"xsd:string\"/>"+
                "<Type xsi:type=\"xsd:string\">PLANTA INTERNA</Type>"+
                "<Value xsi:type=\"xsd:string\">PLANTA INTERNA</Value>"+
                "<Gps xsi:type=\"tns:GPSType\">"+
                "<Lat xsi:type=\"xsd:string\"/>"+
                "<Lng xsi:type=\"xsd:string\"/>"+
                "</Gps>"+
                "<Identification xsi:type=\"tns:IdentificationType\">"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">PLANTA</Attribute>"+
                "<Value xsi:type=\"xsd:string\">TAL2</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">AGREGADOR</Attribute>"+
                "<Value xsi:type=\"xsd:string\">TCA4-PE</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">IP AGREGADOR</Attribute>"+
                "<Value xsi:type=\"xsd:string\">10.52.216.1</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">DSLAM</Attribute>"+
                "<Value xsi:type=\"xsd:string\">TALCA_2_4</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">IP DSLAM</Attribute>"+
                "<Value xsi:type=\"xsd:string\">10.100.118.9</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">RACK / SHELF / SLOT / PORT</Attribute>"+
                "<Value xsi:type=\"xsd:string\">0/0/4/18</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">TIPO PUERTO</Attribute>"+
                "<Value xsi:type=\"xsd:string\">ADSL</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">MDF STB</Attribute>"+
                "<Value xsi:type=\"xsd:string\">23/J/03-6</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">MDF ADSL</Attribute>"+
                "<Value xsi:type=\"xsd:string\">P/25/37-36</Value>"+
                "</Parameters>"+
                "</Identification>"+
                "</Element>"+
                "<Element xsi:type=\"tns:ElementType\">"+
                "<Id xsi:type=\"xsd:string\"/>"+
                "<Type xsi:type=\"xsd:string\">PLANTA EXTERNA</Type>"+
                "<Value xsi:type=\"xsd:string\">PLANTA EXTERNA</Value>"+
                "<Gps xsi:type=\"tns:GPSType\">"+
                "<Lat xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
                "<Lng xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
                "</Gps>"+
                "<Identification xsi:type=\"tns:IdentificationType\">"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">PLANTA</Attribute>"+
                "<Value xsi:type=\"xsd:string\">TAL2</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">CABLE</Attribute>"+
                "<Value xsi:type=\"xsd:string\">9</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">PAR PRIMARIO</Attribute>"+
                "<Value xsi:type=\"xsd:string\">584</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">ARMARIO</Attribute>"+
                "<Value xsi:type=\"xsd:string\">A9</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>"+
                "<Value xsi:type=\"xsd:string\">AV COLIN</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">ALTURA</Attribute>"+
                "<Value xsi:type=\"xsd:string\">*00110-0A9</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">PAR SECUNDARIO</Attribute>"+
                "<Value xsi:type=\"xsd:string\">442</Value>"+
                "</Parameters>"+
                "</Identification>"+
                "</Element>"+
                "<Element xsi:type=\"tns:ElementType\">"+
                "<Id xsi:type=\"xsd:string\">310681</Id>"+
                "<Type xsi:type=\"xsd:string\">CAJA</Type>"+
                "<Value xsi:type=\"xsd:string\">CAJA TERMINAL</Value>"+
                "<Gps xsi:type=\"tns:GPSType\">"+
                "<Lat xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
                "<Lng xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
                "</Gps>"+
                "<Identification xsi:type=\"tns:IdentificationType\">"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">TIPO DE CAJA</Attribute>"+
                "<Value xsi:type=\"xsd:string\">CAJA</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>"+
                "<Value xsi:type=\"xsd:string\">PJE CATORCE PONIENTE</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">ALTURA</Attribute>"+
                "<Value xsi:type=\"xsd:string\">*00077-000</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">PAR DESDE</Attribute>"+
                "<Value xsi:type=\"xsd:string\">441</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">PAR HASTA</Attribute>"+
                "<Value xsi:type=\"xsd:string\">450</Value>"+
                "</Parameters>"+
                "</Identification>"+
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:type=\"xsd:string\">0</Id>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">1</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">PAR</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">TELEFONO</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">ESTADO</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\"></Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">ESTADO</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">41</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">42</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">00222011551</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">OCUPADO ADSL</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">43</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">44</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">45</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">46</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">47</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">48</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">49</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">50</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VACANTE</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\"></Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>"+
                "</Element>"+
                "<Element xsi:type=\"tns:ElementType\">"+
                "<Id xsi:type=\"xsd:string\"/>"+
                "<Type xsi:type=\"xsd:string\">FAMILYSERVICE</Type>"+
                "<Value xsi:type=\"xsd:string\">SERVICIO BANDA ANCHA</Value>"+
                "<Identification xsi:type=\"tns:IdentificationType\">"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">MODEM</Attribute>"+
                "<Value xsi:type=\"xsd:string\">NUCOM R5000UN</Value>"+
                "</Parameters>"+
                "</Identification>"+
                "</Element>"+
                "<Element xsi:type=\"tns:ElementType\">"+
                "<Id xsi:type=\"xsd:string\"/>"+
                "<Type xsi:type=\"xsd:string\">FAMILYSERVICE</Type>"+
                "<Value xsi:type=\"xsd:string\">SERVICIO TELEVISION</Value>"+
                "<Identification xsi:type=\"tns:IdentificationType\">"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">DECO</Attribute>"+
                "<Value xsi:type=\"xsd:string\">Echostar DSB 646</Value>"+
                "</Parameters>"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">DECO</Attribute>"+
                "<Value xsi:type=\"xsd:string\">Echostar DSB 646</Value>"+
                "</Parameters>"+
                "</Identification>"+
                "</Element>"+
                "<Element xsi:type=\"tns:ElementType\">"+
                "<Id xsi:type=\"xsd:string\"/>"+
                "<Type xsi:type=\"xsd:string\">FAMILYSERVICE</Type>"+
                "<Value xsi:type=\"xsd:string\">SERVICIO TELEFONIA</Value>"+
                "<Identification xsi:type=\"tns:IdentificationType\">"+
                "<Parameters xsi:type=\"tns:ParametersType\">"+
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>"+
                "<Value xsi:type=\"xsd:string\">712220084</Value>"+
                "</Parameters>"+
                "</Identification>"+
                "</Element>"+
                "<Return xsi:type=\"tns:ReturnType\">"+
                "<Code xsi:type=\"xsd:string\">0</Code>"+
                "<Description xsi:type=\"xsd:string\">OK:  ELEMENTOS DE PLANTA EXTERNA IDENTIFICADOS PARA [712220084]</Description>"+
                "</Return>"+
                "</Output>"+
                "</Resource>"+
                "</Service>"+
                "</ResponseResource>"+
                "</ns1:ResourceResponse>"+
                "</SOAP-ENV:Body>"+
                "</SOAP-ENV:Envelope>";
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
				            "<Service xsi:type=\"tns:ServiceResourceOut\">"+
				               "<Resource xsi:type=\"tns:ResourceOut\">"+
				                  "<Output xsi:type=\"tns:ResourceOutData\">"+
				                     "<Element xsi:type=\"tns:ElementType\">"+
				                        "<Id xsi:type=\"xsd:string\"/>"+
				                        "<Type xsi:type=\"xsd:string\">DIRECCION</Type>"+
				                        "<Value xsi:type=\"xsd:string\">DIRECCION</Value>"+
				                        "<Gps xsi:type=\"tns:GPSType\">"+
				                           "<Lat xsi:type=\"xsd:string\"/>"+
				                           "<Lng xsi:type=\"xsd:string\"/>"+
				                        "</Gps>"+
				                        "<Identification xsi:type=\"tns:IdentificationType\">"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">LOS FRESNOS</Value>"+
				                           "</Parameters>"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">NUMERO</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">380</Value>"+
				                           "</Parameters>"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">PASAJE/BLOCK</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\"/>"+
				                           "</Parameters>"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">CIUDAD</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">S FRANCISC</Value>"+
				                           "</Parameters>"+
				                        "</Identification>"+
				                     "</Element>"+
				                     "<Element xsi:type=\"tns:ElementType\">"+
				                        "<Id xsi:type=\"xsd:string\"/>"+
				                        "<Type xsi:type=\"xsd:string\">PLANTA INTERNA</Type>"+
				                        "<Value xsi:type=\"xsd:string\">PLANTA INTERNA</Value>"+
				                        "<Gps xsi:type=\"tns:GPSType\">"+
				                           "<Lat xsi:type=\"xsd:string\"/>"+
				                           "<Lng xsi:type=\"xsd:string\"/>"+
				                        "</Gps>"+
				                        "<Identification xsi:type=\"tns:IdentificationType\">"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">PLANTA</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">SFMO</Value>"+
				                           "</Parameters>"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">AGREGADOR</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">PE-RGA</Value>"+
				                           "</Parameters>"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">IP AGREGADOR</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">10.52.70.1</Value>"+
				                           "</Parameters>"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">DSLAM</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">SAN-FCO-DE-MOSTAZAL</Value>"+
				                           "</Parameters>"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">IP DSLAM</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">10.100.80.138</Value>"+
				                           "</Parameters>"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">RACK / SHELF / SLOT / PORT</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">1/1/2/2</Value>"+
				                           "</Parameters>"+
				                           "<Parameters xsi:type=\"tns:ParametersType\">"+
				                              "<Attribute xsi:type=\"xsd:string\">TIPO PUERTO</Attribute>"+
				                              "<Value xsi:type=\"xsd:string\">ADSL</Value>"+
				                           "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">MDF STB</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">02/F/11-EF</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">MDF ADSL</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">H/2/50-51</Value>"+
				                          "</Parameters>"+
				                       "</Identification>"+
				                    "</Element>"+
				                    "<Element xsi:type=\"tns:ElementType\">"+
				                       "<Id xsi:type=\"xsd:string\"/>"+
				                       "<Type xsi:type=\"xsd:string\">PLANTA EXTERNA</Type>"+
				                       "<Value xsi:type=\"xsd:string\">PLANTA EXTERNA</Value>"+
				                       "<Gps xsi:type=\"tns:GPSType\">"+
				                          "<Lat xsi:type=\"xsd:string\"/>"+
				                          "<Lng xsi:type=\"xsd:string\"/>"+
				                       "</Gps>"+
				                    "</Element>"+
				                    "<Element xsi:type=\"tns:ElementType\">"+
				                       "<Id xsi:type=\"xsd:string\">289510</Id>"+
				                       "<Type xsi:type=\"xsd:string\">CAJA</Type>"+
				                       "<Value xsi:type=\"xsd:string\">CAJA TERMINAL</Value>"+
				                       "<Gps xsi:type=\"tns:GPSType\">"+
				                          "<Lat xsi:type=\"xsd:string\">-33.9737</Lat>"+
				                          "<Lng xsi:type=\"xsd:string\">-70.7083</Lng>"+
				                       "</Gps>"+
				                       "<Identification xsi:type=\"tns:IdentificationType\">"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">LOS FRESNOS</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">ALTURA</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">000351</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">PAR DESDE</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">131</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">PAR HASTA</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">140</Value>"+
				                          "</Parameters>"+
				                       "</Identification>"+
				                       "<SubElement xsi:type=\"tns:SubElementType\">"+
			                           "<Id xsi:type=\"xsd:string\">0</Id>"+
			                           "<Type xsi:type=\"xsd:string\">PAR</Type>"+
			                           "<Head xsi:type=\"xsd:string\">1</Head>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">PAR</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">AREA</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">FONO</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">EST</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">HEAD</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">ESTADO</Value>"+
			                           "</Parameters>"+
			                        "</SubElement>"+
			                        "<SubElement xsi:type=\"tns:SubElementType\">"+
			                           "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
			                           "<Type xsi:type=\"xsd:string\">PAR</Type>"+
			                           "<Head xsi:type=\"xsd:string\">0</Head>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">131</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">FONO</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">07202491486</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">EST</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">OA</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">1</Value>"+
			                           "</Parameters>"+
			                        "</SubElement>"+
			                        "<SubElement xsi:type=\"tns:SubElementType\">"+
			                           "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
			                           "<Type xsi:type=\"xsd:string\">PAR</Type>"+
			                           "<Head xsi:type=\"xsd:string\">0</Head>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">132</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">FONO</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">07202492973</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">EST</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">OC</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">1</Value>"+
			                           "</Parameters>"+
			                        "</SubElement>"+
			                        "<SubElement xsi:type=\"tns:SubElementType\">"+
			                           "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
			                           "<Type xsi:type=\"xsd:string\">PAR</Type>"+
			                           "<Head xsi:type=\"xsd:string\">0</Head>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">133</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">FONO</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">07202491816</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">EST</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">OA</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">1</Value>"+
			                           "</Parameters>"+
			                        "</SubElement>"+
			                        "<SubElement xsi:type=\"tns:SubElementType\">"+
			                           "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
			                           "<Type xsi:type=\"xsd:string\">PAR</Type>"+
			                           "<Head xsi:type=\"xsd:string\">0</Head>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">134</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">FONO</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">07202491362</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">EST</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">OA</Value>"+
			                           "</Parameters>"+
			                           "<Parameters xsi:type=\"tns:ParametersType\">"+
			                              "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>"+
			                              "<Value xsi:type=\"xsd:string\">2</Value>"+
			                           "</Parameters>"+
			                        "</SubElement>"+
				                       "<SubElement xsi:type=\"tns:SubElementType\">"+
				                          "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
				                          "<Type xsi:type=\"xsd:string\">PAR</Type>"+
				                          "<Head xsi:type=\"xsd:string\">0</Head>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">135</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">FONO</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">07202491633</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">EST</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">OC</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">1</Value>"+
				                          "</Parameters>"+
				                       "</SubElement>"+
				                       "<SubElement xsi:type=\"tns:SubElementType\">"+
				                          "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
				                          "<Type xsi:type=\"xsd:string\">PAR</Type>"+
				                          "<Head xsi:type=\"xsd:string\">0</Head>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">136</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">FONO</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">07202491731</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">EST</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">OC</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">1</Value>"+
				                          "</Parameters>"+
				                       "</SubElement>"+
				                       "<SubElement xsi:type=\"tns:SubElementType\">"+
				                          "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
				                          "<Type xsi:type=\"xsd:string\">PAR</Type>"+
				                          "<Head xsi:type=\"xsd:string\">0</Head>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">137</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">FONO</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\"/>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">EST</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">OE</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">1</Value>"+
				                          "</Parameters>"+
				                       "</SubElement>"+
				                       "<SubElement xsi:type=\"tns:SubElementType\">"+
				                          "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
				                          "<Type xsi:type=\"xsd:string\">PAR</Type>"+
				                          "<Head xsi:type=\"xsd:string\">0</Head>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">131</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">AREA</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">72</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">EST</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">OA</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">1</Value>"+
				                          "</Parameters>"+
				                       "</SubElement>"+
				                       "<SubElement xsi:type=\"tns:SubElementType\">"+
				                          "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>"+
				                          "<Type xsi:type=\"xsd:string\">PAR</Type>"+
				                          "<Head xsi:type=\"xsd:string\">0</Head>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">0</Value>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">FONO</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\"/>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">EST</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\"/>"+
				                          "</Parameters>"+
				                          "<Parameters xsi:type=\"tns:ParametersType\">"+
				                             "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>"+
				                             "<Value xsi:type=\"xsd:string\">0</Value>"+
				                          "</Parameters>"+
				                       "</SubElement>"+
				                    "</Element>"+
				                    "<Return xsi:type=\"tns:ReturnType\">"+
				                       "<Code xsi:type=\"xsd:string\">0</Code>"+
				                       "<Description xsi:type=\"xsd:string\">OK:  ELEMENTOS DE PLANTA EXTERNA IDENTIFICADOS PARA [72-2491362]</Description>"+
				                    "</Return>"+
				                 "</Output>"+
				              "</Resource>"+
				           "</Service>"+
				        "</ResponseResource>"+
				     "</ns1:ResourceResponse>"+
				  "</SOAP-ENV:Body>"+
				"</SOAP-ENV:Envelope>";
	}

}
