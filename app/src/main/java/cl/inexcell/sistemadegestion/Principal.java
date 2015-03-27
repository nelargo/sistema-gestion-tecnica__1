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
                        Log.i(TAG, error1());
                        consulta = error1();
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
    
    public String error1(){
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Demo\">" +
                "<SOAP-ENV:Body>" +
                "<ns1:ResourceResponse xmlns:ns1=\"urn:Demo\">" +
                "<ResponseResource xsi:type=\"tns:ResponseResource\">" +
                "<Operation xsi:type=\"tns:OperationType\">" +
                "<OperationCode xsi:type=\"xsd:string\">?</OperationCode>" +
                "<OperationId xsi:type=\"xsd:string\">1</OperationId>" +
                "<DateTime xsi:type=\"xsd:string\">?</DateTime>" +
                "<IdUser xsi:type=\"xsd:string\">1</IdUser>" +
                "<IMEI xsi:type=\"xsd:string\">358875050182545</IMEI>" +
                "<IMSI xsi:type=\"xsd:string\">8956023100071807198</IMSI>" +
                "</Operation>" +
                "<Service xsi:type=\"tns:ServiceResourceOut\">" +
                "<Resource xsi:type=\"tns:ResourceOut\">" +
                "<Output xsi:type=\"tns:ResourceOutData\">" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">DIRECCION</Type>" +
                "<Value xsi:type=\"xsd:string\">DATOS DEL CLIENTE</Value>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">RUT</Attribute>" +
                "<Value xsi:type=\"xsd:string\">7627589-0</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TITULAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">MILVA ERIKA VASSALLO SUAREZ</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>" +
                "<Value xsi:type=\"xsd:string\">FONTANA ROSA</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">NUMERO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">6640</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PASAJE/BLOCK</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1201</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">NUMERO DPTO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1201</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PISO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">12</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">COMUNA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">LAS CONDES</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">CIUDAD</Attribute>" +
                "<Value xsi:type=\"xsd:string\">SANTIAGO</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PRODUCTOS</Type>" +
                "<Value xsi:type=\"xsd:string\">PRODUCTOS Y SERVICIOS</Value>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">Cantidad de ps</Attribute>" +
                "<Value xsi:type=\"xsd:string\">23</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">187</Attribute>" +
                "<Value xsi:type=\"xsd:string\">SERV. VISUALIZACION NUM LLAM.</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">447</Attribute>" +
                "<Value xsi:type=\"xsd:string\">BLOQUEO 700</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">457</Attribute>" +
                "<Value xsi:type=\"xsd:string\">TRANSF.OTRO NUM.</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">462</Attribute>" +
                "<Value xsi:type=\"xsd:string\">IND LLAMADA EN ESPERA</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">463</Attribute>" +
                "<Value xsi:type=\"xsd:string\">CONFERENCIA TRIPARTITA</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">1199</Attribute>" +
                "<Value xsi:type=\"xsd:string\">MODEM SPEEDY</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">1203</Attribute>" +
                "<Value xsi:type=\"xsd:string\">PROCESO PROVISION SPEEDY</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">1225</Attribute>" +
                "<Value xsi:type=\"xsd:string\">SERV SUP LLDA DIRECTA TEMPORIZ</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">1406</Attribute>" +
                "<Value xsi:type=\"xsd:string\">BUZON FAMILIAR</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">1771</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VISUALIZ LLDAS EN ESPERA</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">3123</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1ER DECODIFICADOR DTH TVD</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">3547</Attribute>" +
                "<Value xsi:type=\"xsd:string\">VISITA TECNICA</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">4902</Attribute>" +
                "<Value xsi:type=\"xsd:string\">EQ ALAMBRIC BAS T-CHILE A-7010</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">5015</Attribute>" +
                "<Value xsi:type=\"xsd:string\">PS CONTENCION RESIDENCIAL</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">5099</Attribute>" +
                "<Value xsi:type=\"xsd:string\">DESCUENTO DECODIFICADOR TV</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">5223</Attribute>" +
                "<Value xsi:type=\"xsd:string\">MODEM INALAMBRICO SPY WIFI</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">5443</Attribute>" +
                "<Value xsi:type=\"xsd:string\">DECODIFICADOR ADICIONAL DTH NP</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">5825</Attribute>" +
                "<Value xsi:type=\"xsd:string\">PLAN MIN ILIMITADO LOC HOGAR</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">5954</Attribute>" +
                "<Value xsi:type=\"xsd:string\">ACCESO MOVISTAR 10MG MONO ADSL</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">5955</Attribute>" +
                "<Value xsi:type=\"xsd:string\">ISP MOVISTAR 10 MEGA MONO</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">5965</Attribute>" +
                "<Value xsi:type=\"xsd:string\">DUO BA MIN ILIMIT 10MEGA ADSL</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">6026</Attribute>" +
                "<Value xsi:type=\"xsd:string\">PLAN PREFERIDO TVD</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">6851</Attribute>" +
                "<Value xsi:type=\"xsd:string\">PLAN PREMIUM FSP</Value>" +
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
                "<Value xsi:type=\"xsd:string\">APOQ</Value>" +
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
                "<Value xsi:type=\"xsd:string\">APOQUINDO_12</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">IP DSLAM</Attribute>" +
                "<Value xsi:type=\"xsd:string\">10.100.22.13</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">RACK / SHELF / SLOT / PORT</Attribute>" +
                "<Value xsi:type=\"xsd:string\">0 / 0 / 13 / 47</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TIPO PUERTO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">ADSL2+</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">MDF STB</Attribute>" +
                "<Value xsi:type=\"xsd:string\">02 / H / 10-CD</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">MDF ADSL</Attribute>" +
                "<Value xsi:type=\"xsd:string\">70 / G / 126-127</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PLANTA EXTERNA</Type>" +
                "<Value xsi:type=\"xsd:string\">PLANTA EXTERNA</Value>" +
                "<Gps xsi:type=\"tns:GPSType\">" +
                "<Lat xsi:type=\"xsd:string\">-33.4182</Lat>" +
                "<Lng xsi:type=\"xsd:string\">-70.5596</Lng>" +
                "</Gps>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PLANTA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">APOQ</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">CABLE</Attribute>" +
                "<Value xsi:type=\"xsd:string\">10052</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR PRIMARIO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2328</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\">358586</Id>" +
                "<Type xsi:type=\"xsd:string\">CAJA</Type>" +
                "<Value xsi:type=\"xsd:string\">CAJA TERMINAL</Value>" +
                "<Gps xsi:type=\"tns:GPSType\">" +
                "<Lat xsi:type=\"xsd:string\">-33.4182</Lat>" +
                "<Lng xsi:type=\"xsd:string\">-70.5596</Lng>" +
                "</Gps>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TIPO DE CAJA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">TABLERO</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">CALLE</Attribute>" +
                "<Value xsi:type=\"xsd:string\">FONTANA ROSA</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ALTURA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">006640</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR DESDE</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2327</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR HASTA</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2352</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,327</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">OCUPADO</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,328</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">00222011108</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">OCUPADO</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,329</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,330</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">00222113863</Value>" +
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
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,331</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">00222112206</Value>" +
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
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,332</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">00222121702</Value>" +
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
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,333</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,334</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,335</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">TIERRA</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,336</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,337</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,338</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">00222114982</Value>" +
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
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,339</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,340</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">00222114188</Value>" +
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
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,341</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">TIERRA</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,342</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,343</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,344</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,345</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,346</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,347</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
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
                "<Value xsi:type=\"xsd:string\">1</Value>" +
                "</Parameters>" +
                "</SubElement>" +
                "<SubElement xsi:type=\"tns:SubElementType\">" +
                "<Id xsi:nil=\"true\" xsi:type=\"xsd:string\"/>" +
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,348</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">00222014969</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">OCUPADO</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,349</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">CIRCUITO</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,350</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,351</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">CIRCUITO</Value>" +
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
                "<Type xsi:type=\"xsd:string\">PAR2</Type>" +
                "<Head xsi:type=\"xsd:string\">0</Head>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">PAR</Attribute>" +
                "<Value xsi:type=\"xsd:string\">2,352</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">TELEFONO</Attribute>" +
                "<Value xsi:type=\"xsd:string\"/>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">ESTADO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">CIRCUITO</Value>" +
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
                "<Value xsi:type=\"xsd:string\">NUCOM R5000UN</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Element xsi:type=\"tns:ElementType\">" +
                "<Id xsi:type=\"xsd:string\">0</Id>" +
                "<Type xsi:type=\"xsd:string\">FAMILYSERVICE</Type>" +
                "<Value xsi:type=\"xsd:string\">SERVICIO TELEVISION</Value>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">DECO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">Zinwell ZDX 7510</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieDeco</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1667493369</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieTarjeta</Attribute>" +
                "<Value xsi:type=\"xsd:string\">324317795</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">DECO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">Kathrein S271 / C271 - 0</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieDeco</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1667297227</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieTarjeta</Attribute>" +
                "<Value xsi:type=\"xsd:string\">324317794</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">DECO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">Echostar DSB 636 - 1</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieDeco</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1700193740</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieTarjeta</Attribute>" +
                "<Value xsi:type=\"xsd:string\">324234836</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">DECO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">Zinwell ZDX 7510</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieDeco</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1667493348</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieTarjeta</Attribute>" +
                "<Value xsi:type=\"xsd:string\">324317796</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "<Identification xsi:type=\"tns:IdentificationType\">" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">DECO</Attribute>" +
                "<Value xsi:type=\"xsd:string\">Kathrein S271 / C271 - 0</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieDeco</Attribute>" +
                "<Value xsi:type=\"xsd:string\">1667297243</Value>" +
                "</Parameters>" +
                "<Parameters xsi:type=\"tns:ParametersType\">" +
                "<Attribute xsi:type=\"xsd:string\">SerieTarjeta</Attribute>" +
                "<Value xsi:type=\"xsd:string\">324317793</Value>" +
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
                "<Value xsi:type=\"xsd:string\">222011108</Value>" +
                "</Parameters>" +
                "</Identification>" +
                "</Element>" +
                "<Return xsi:type=\"tns:ReturnType\">" +
                "<Code xsi:type=\"xsd:string\">0</Code>" +
                "<Description xsi:type=\"xsd:string\">OK:  ELEMENTOS DE PLANTA EXTERNA IDENTIFICADOS PARA [222011108]</Description>" +
                "</Return>" +
                "</Output>" +
                "</Resource>" +
                "</Service>" +
                "</ResponseResource>" +
                "</ns1:ResourceResponse>" +
                "</SOAP-ENV:Body>" +
                "</SOAP-ENV:Envelope>";
    }

}
