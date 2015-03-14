package cl.inexcell.sistemadegestion;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Notificar_Averias extends Activity {
	
	private String observacion, objeto;
	private ArrayList<String> res;
	private List<String> res1;
	private ArrayList<String> dano1;
	private ArrayList<String> dano2;
	private ArrayList<String> dano3;
	private ArrayList<String> dano4;
	private String selected;
	private String[] tipos = {"Cable","Armario","Caja","Tablero"};
	
	private ArrayList<ArrayList<String>> danos;
	
	private Bitmap foto;
	private Spinner s, dano;
	private EditText et;	
	private ImageButton ib;
	
	private String TAG = "Localizar Avería";
	
	private Bitmap b = null, bmini = null;
	private static int TAKE_PICTURE = 1;
	private static int SELECT_PICTURE = 2;
	final CharSequence[] opcionCaptura = {
    		"Tomar Fotografía"
    };
	final CharSequence[] SpinnerText = {
    		"Cable", "Armario","Caja","Tablero"
    };
	private String tipodano;
	private String name = "";
	private LocationManager locationManager;
	private Location location;
	private Location loc;
	private String Lat,Lng;
	private double latitude, longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		// Activity sin parte superior
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_notificar_averias);
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		
		
		
//		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		Log.i("LOCALIZACION", loc.getLatitude()+"\n"+loc.getLongitude());
//		if(loc != null){
//			Lat = String.valueOf(loc.getLatitude());
//			Lng = String.valueOf(loc.getLongitude());
//		}
		danos = new ArrayList<ArrayList<String>>();		
		dano1 = new ArrayList<String>();
		dano2= new ArrayList<String>();
		dano3= new ArrayList<String>();
		dano4= new ArrayList<String>();
		
		dano1.add("Tipo1");dano1.add("Tipo3");dano1.add("Tipo4");
		dano2.add("Tipo2");dano2.add("Tipo3");dano2.add("Tipo5");dano2.add("Tipo6");
		dano3.add("Tipo4");dano3.add("Tipo6");
		dano4.add("Tipo2");dano4.add("Tipo4");dano4.add("Tipo5");		
		danos.add(dano1);danos.add(dano2);danos.add(dano3);danos.add(dano4);
		
		s = (Spinner) findViewById(R.id.Spinner021);
		dano = (Spinner) findViewById(R.id.Spinner011);
		et = (EditText) findViewById(R.id.editText11);
		ib = (ImageButton) findViewById(R.id.ibtnImagen1);
		name = Environment.getExternalStorageDirectory() + "/test.jpg";
		
		s.setOnItemSelectedListener(new OnItemSelectedListener(){

			

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub				

				selected = tipos[arg2];
				Buscar_damage bd = new Buscar_damage();
				bd.execute();
			}
			
			
		});
	}
	
	public void OnItemSelected(View v){
		
	}
	public void shutdown(View v){
		Principal.p.finish();
		finish();
	}
	/** Boton Guardar Informacion **/
	public void guardarInformacion (View view){
		
		if(et.getText().toString().compareTo("") == 0){
			Toast.makeText(getApplicationContext(), "Debe ingresar una observación.", Toast.LENGTH_LONG).show();
			return;
		}
		if(b == null){
			Toast.makeText(getApplicationContext(), "Debe tomar fotografía.", Toast.LENGTH_LONG).show();
			return;
		}
		
		observacion = et.getText().toString();
		objeto = SpinnerText[s.getSelectedItemPosition()].toString();
		tipodano = dano.getSelectedItem().toString();
		//tipodano = "ASD";
		foto = b;
		
		//Enviar_Averia ea = new Enviar_Averia();
		//ea.execute();
		/*Drawable d = new BitmapDrawable(getResources(), bmini);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(" ");
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
	          @Override
	          public void onClick(DialogInterface dialog, int which){
	            //Salir
	          	return;
	          }
	        });
        builder.setMessage("Elemento Dañado: "+SpinnerText[s.getSelectedItemPosition()]+"\nTipo Dako: "+tipodano+"\nObservación: "+et.getText()+"\n\n¿Todo Correcto?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
	          @Override
	          public void onClick(DialogInterface dialog, int which){
	            
	          	//Toast.makeText(getApplicationContext(), "BIEN", Toast.LENGTH_LONG).show();
	          	Enviar_Averia ea = new Enviar_Averia();
	    		ea.execute();
	    		
	          }
	        });
        
        builder.setIcon(d);
        
        AlertDialog alert = builder.create();
        alert.show();
        */

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_averia);
        dialog.setTitle("¿Todo Correcto?");


        // set the custom dialog components - text, image and button
        TextView elemento = (TextView) dialog.findViewById(R.id.dialogaveria_elemento);
        TextView siniestro = (TextView) dialog.findViewById(R.id.dialogaveria_siniestro);
        TextView comentario = (TextView) dialog.findViewById(R.id.dialogaveria_comentario);
        elemento.setText(objeto);
        siniestro.setText(tipodano);
        comentario.setText(observacion);
        ImageView image = (ImageView) dialog.findViewById(R.id.dialogaveria_captura);
        image.setImageBitmap(b);

        ImageButton dialogOk = (ImageButton) dialog.findViewById(R.id.dialogaveria_ok);
        ImageButton dialogNok = (ImageButton) dialog.findViewById(R.id.dialogaveria_nok);
        // if button is clicked, close the custom dialog
        dialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Enviar_Averia ea = new Enviar_Averia();
                ea.execute();
            }
        });

        dialogNok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
	}
	
	/** Boton Camara **/
	public void capturarImagen(View view){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escoja una Opcion:");
        builder.setIcon(R.drawable.ic_camera);
        builder.setItems(opcionCaptura, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
            	Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            	int code = TAKE_PICTURE;
            	if (item==TAKE_PICTURE) {            		
            	    Uri output = Uri.fromFile(new File(name));
            	    intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
            	} else if (item==SELECT_PICTURE){
            	    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            	    code = SELECT_PICTURE;
            	}
            	startActivityForResult(intent, code);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == TAKE_PICTURE) {
	    	if (data != null) {
	    		if (data.hasExtra("data")) {
	    		    b = (Bitmap) data.getParcelableExtra("data");
	    		}
	    	} else {
	    			b = BitmapFactory.decodeFile(name);
	    			
	    		}
	    } else if (requestCode == SELECT_PICTURE){
	    	Uri selectedImage = data.getData();
	    	InputStream is;
	    	try {
	    	    is = getContentResolver().openInputStream(selectedImage);
	    	    BufferedInputStream bis = new BufferedInputStream(is);
	    	    b = BitmapFactory.decodeStream(bis);
	    	    
	    	} catch (FileNotFoundException e) {}
	    }
	    try{
	    b = Bitmap.createScaledBitmap(b, 640, 480, true);
	    bmini = Bitmap.createScaledBitmap(b, 64, 64, true);
	    }catch(Exception ex){}
	    
	    
	}
	
	/** Boton Volver **/
	public void volver(View view) {
    	finish();
    	
    	// Vibrar al hacer click        
        Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }
	
private class Enviar_Averia extends AsyncTask<String,Integer,String> {
   		
   		private final ProgressDialog dialog = new ProgressDialog(Notificar_Averias.this);
   		
 		protected void onPreExecute() {
 			this.dialog.setMessage("Enviando Avería Localizada...");
 			this.dialog.setCanceledOnTouchOutside(false);
 			this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Operación Interrumpida.", Toast.LENGTH_LONG).show();
					Notificar_Averias.this.finish();
				}
			});
 		    this.dialog.show();
             //super.onPreExecute();
         }
   		 
   	    protected String doInBackground(String... params) {
   	    	
 			String respuesta = null;
   			
   			try 
   			{
   				TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
   				String IMEI = telephonyManager.getDeviceId();
   				String IMSI =  telephonyManager.getSimSerialNumber();
   				
   				if(location == null){
   					return "GPS";
   				}
   				latitude = location.getLatitude();
   				longitude = location.getLongitude();
   				
   				respuesta = SoapRequestMovistar.setLocation(Funciones.encodeTobase64(foto),
   																objeto,  
   																tipodano,
   																Double.toString(latitude),
   																Double.toString(longitude),
   																observacion,
   																IMEI,
   																IMSI);
   				
   			} catch (Exception e1) {
   				e1.printStackTrace();
   			}

   	        return respuesta;
   	    }
   	    

 		protected void onPostExecute(String result) {
 			
 			if (this.dialog.isShowing()) {
 		        this.dialog.dismiss();
 		     }
   			
   	    	if (result != null)
   	    	{
   	    		if(result.equals("GPS")){

   					Toast.makeText(getApplicationContext(), "Error con GPS", Toast.LENGTH_SHORT).show();
   					return;
   	    		}
   	    			
   	    		try {
   	    			
   	    			//ArrayList<String> res = XMLParser.getVendor(result);
   	    			res = XMLParser.setLocation(result);
   	    			Toast.makeText(getApplicationContext(), res.get(1), Toast.LENGTH_LONG).show();
   	    			et.setText("");
   	    			b = foto = null;
   	    			//final CharSequence[] fab = res.toArray(new CharSequence[res.size()]);
   	    			//ListarFabricantesBandaAncha(fab);
   	    			finish();
   	    	    	
   	    	    	// Vibrar al hacer click        
   	    	        Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
   	    	        vibrator.vibrate(50);
   	    			
 				} catch (Exception e) {
 					e.printStackTrace();
 				}
   	    	}
   	    	else
   	    	{
   	    		Toast.makeText(getApplicationContext(), "Error en la conexión del servicio. Revise su conexión de Internet o 3G.", Toast.LENGTH_LONG).show();
   	    	}
   	    }
   	}

private class Buscar_damage extends AsyncTask<String,Integer,String> {
		
		private final ProgressDialog dialog = new ProgressDialog(Notificar_Averias.this);
		
		protected void onPreExecute() {
			this.dialog.setMessage("Buscando tipos de Daño...");
			this.dialog.setCanceledOnTouchOutside(false);
			this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Operación Interrumpida.", Toast.LENGTH_LONG).show();
				Notificar_Averias.this.finish();
			}
		});
		    this.dialog.show();
         //super.onPreExecute();
     }
		 
	    protected String doInBackground(String... params) {
	    	
			String respuesta = null;
			
			try 
			{
				TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				String IMEI = telephonyManager.getDeviceId();
				String IMSI =  telephonyManager.getSimSerialNumber();
				
				
				respuesta = SoapRequestMovistar.getDamage(IMEI,IMSI,selected);
				//respuesta = nada();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

	        return respuesta;
	    }
	    

		protected void onPostExecute(String result) {
			
			if (this.dialog.isShowing()) {
		        this.dialog.dismiss();
		     }
			
	    	if (result != null)
	    	{
	    		try {
	    			
	    			//ArrayList<String> res = XMLParser.getVendor(result);
	    			res =  XMLParser.getDamage(result);
	    			List<String> re1 = res;
	    			
//	    			List<String> list= danos.get(arg2);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, re1);
					adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
//					((TextView) dano.getSelectedView()).setTextColor(Color.BLACK);
					dano.setAdapter(adapter);
//					for(int i = 0;i < danos.get(arg2).length;i++){
//						adapter.add(danos.get(arg2)[i]);
//					}
					adapter.notifyDataSetChanged();
	    			
	    			
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    	else
	    	{
	    		Toast.makeText(getApplicationContext(), "Error en la conexión del servicio. Revise su conexión de Internet o 3G.", Toast.LENGTH_LONG).show();
	    	}
	    }
	}

public String nada(){
	return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Demo\">"+
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
       "<Service xsi:type=\"tns:ServiceDamageOut\">"+
          "<Damage xsi:type=\"tns:DamageOut\">"+
             "<Output xsi:type=\"tns:DamageOutData\">"+
                "<TypeDamage xsi:type=\"xsd:string\">FALLA ZONA CLIENTE</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">PROBLEMA CABLE LOCAL</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">PROBLEMA CABLE EXTERNO</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">PROBLEMA PLANTA EXTERNA</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">CORTE ENERGIA</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">INTERMITENCIA EN ANALISIS</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">ROBO DE CABLE</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">INTERMITENCIA CONFIRMADA</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">MASIVA MDF</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">MASIVA ZONA CLIENTE</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">FALLA ZONA CLIENTE</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">PROBLEMA CABLE LOCAL</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">PROBLEMA CABLE EXTERNO</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">ALERTA CAJA TERMINAL O TABLERO</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">INTERMITENCIA EN CAJATERM O TABLERO</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">CAJA O TABLERO CON BA CON ACTIVIDAD</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">ALERTA CAJA TERMINAL O TABLERO (SIN BA)</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">PROBLEMA CABLE LOCAL</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">INTERMITENCIA PLANTA EXTERNA</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">INTERMITENCIA PLANTA EXTERNA</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">POSIBLE ROBO O AVERIA</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">PROBLEMA PLANTA EXTERNA</TypeDamage>"+
                "<TypeDamage xsi:type=\"xsd:string\">POSIBLE PROBLEMA ENERGIA ZONA CLIENTE</TypeDamage>"+
                "<Return xsi:type=\"tns:ReturnType\">"+
                   "<Code xsi:type=\"xsd:string\">0</Code>"+
                   "<Description xsi:type=\"xsd:string\">OK: LISTA DE AVERIAS</Description>"+
                "</Return>"+
             "</Output>"+
          "</Damage>"+
       "</Service>"+
    "</ResponseResource>"+
 "</ns1:ResourceResponse>"+
"</SOAP-ENV:Body>"+
"</SOAP-ENV:Envelope>";
}




}
