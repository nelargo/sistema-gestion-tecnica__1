package cl.inexcell.sistemadegestion;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class VistaTopologica extends Activity {
	private String TAG = "TOPOLOGICA";
	public static Activity topo;
	
	//TODO: Declaraciones
	int left;
	private ConnectivityManager conMan;
	private String output;
	private LinearLayout LContenido;
	private ArrayList<String> pares;
	private ScrollView sv1;
	private ArrayList<Integer> ids_botones;
	private ArrayList<Integer> ids_contenidos;
	private String Phone;
	private String parActual;
	private EditText valor;
    private String tipo;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Activity sin parte superior
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_topologica);
		topo = this;
		Phone = getIntent().getStringExtra("PHONE");
		conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);	
		output = getIntent().getStringExtra("RESULT");
		sv1 = (ScrollView)findViewById(R.id.contenido_topologica);
		LContenido = (LinearLayout)findViewById(R.id.contenido_topologica1);
		valor = (EditText)findViewById(R.id.editText11);

        Log.i(TAG, Phone);
		
		
		init();
		Ordenar_informacion tarea1 = new Ordenar_informacion();
		tarea1.execute();
	}

	public void shutdown(View v){
		Principal.p.finish();
		finish();
	}
	

	
	public void consultar(View v){	
		State state3g = conMan.getNetworkInfo(0).getState();
		State stateWifi = conMan.getNetworkInfo(1).getState();
		if (state3g == NetworkInfo.State.CONNECTED || stateWifi == NetworkInfo.State.CONNECTED)
		 {
			 certificacionTipo(0);
		 }else {
			 Toast.makeText(getApplicationContext(), "Error Consultas y Pruebas: \nNo hay conexión a internet", Toast.LENGTH_SHORT).show();
		 }
	}
	public void instalar (View v){
		State stateWifi = conMan.getNetworkInfo(1).getState();
		if (stateWifi == NetworkInfo.State.CONNECTED)
		 {
			 certificacionTipo(1);
		 }else {
			 Toast.makeText(getApplicationContext(), "Error Instalación: \nDebe estar conectado a una red WIFI para continuar.", Toast.LENGTH_SHORT).show();
		 }
	}
	public void reparar (View v){
		State stateWifi = conMan.getNetworkInfo(1).getState();
		if (stateWifi == NetworkInfo.State.CONNECTED)
		 {
			 certificacionTipo(2);
		 }else {
			 Toast.makeText(getApplicationContext(), "Error Reparación: \nDebe estar conectado a una red WIFI para continuar", Toast.LENGTH_SHORT).show();
		 }
	}
	
	public void certificacionTipo(int tipo){
		Intent certificar = new Intent(this, Certificar.class);
		
		certificar.putExtra("PHONE",Phone);
		certificar.putExtra("TIPO", tipo);
		if(!valor.getText().toString().equals(""))
			certificar.putExtra("VALUE", valor.getText().toString());
		else
			certificar.putExtra("VALUE", "?");
		startActivity(certificar);
		//btnCert.performClick();
	}
	
	public void volver(View view){
		this.finish();
	}
	
	public void init(){


	}
	

	
	//TODO: Ordenar_Informacion
private class Ordenar_informacion extends AsyncTask<String,Integer,ArrayList<Bundle>> {
   		
   		private final ProgressDialog dialog = new ProgressDialog(VistaTopologica.this);
   		   		
 		protected void onPreExecute() {
 			this.dialog.setMessage("Consultando Información Cliente...");
 			this.dialog.setCanceledOnTouchOutside(false);
 			this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					Toast.makeText(getApplicationContext(), "Operación Interrumpida.", Toast.LENGTH_SHORT).show();
					
				}
			});
 		    this.dialog.show();
             //super.onPreExecute();
         }
   		 
   	    protected ArrayList<Bundle> doInBackground(String... params) {
   	    	
 			ArrayList<Bundle> todo = new ArrayList<Bundle>();
   			try {
                    Log.i(TAG, output);
   	   				todo = XMLParser.getResourcesNew(output);
   	   				
   			} catch (Exception e1) {
   				e1.printStackTrace();

   			}
   			
   	        return todo;
   	    }
   	    

 		protected void onPostExecute(ArrayList<Bundle> result) {

 			//cantElementos = new int[result.size()];
 			ids_botones = new ArrayList<Integer>();
 			ids_contenidos= new ArrayList<Integer>();
 			for(Bundle b : result){ 
 				String idB = b.getString("ID");
 				if(idB != null){
 					Log.d(TAG,"idb = "+idB);
 				}else
 					Log.d(TAG,"idB= null");
 				String swit = b.getString("TYPE");
 				left=0;
 				if(swit.equals("DIRECCION")){
 					left = R.drawable.cc_direct;
 				}
 				if(swit.equals("PLANTA INTERNA")){
 					left = R.drawable.vt_planta;
 				}
 				if(swit.equals("PLANTA EXTERNA")){
 					left = R.drawable.vt_pe;
 				}
 				if(b.getString("VALUE").equals("SERVICIO TELEFONIA")){
 					left = R.drawable.vt_stb;
 				}
 				if(b.getString("VALUE").equals("SERVICIO BANDA ANCHA")){
 					left = R.drawable.vt_baf;
 				}
 				if(b.getString("VALUE").equals("SERVICIO TELEVISION")){
 					left = R.drawable.vt_dth;
 				}
 				if(swit.equals("CAJA")){
 					left = R.drawable.vt_caja;
 				}
 				if(swit.equals("DSLAM")){
 					left = R.drawable.vt_dslam;
 				}
 				
 				
 				String newId = "Boton"+b.getString("TYPE")+b.getString("VALUE");
 				String newIdCont = "Contenido"+b.getString("TYPE")+b.getString("VALUE");
 				Log.d(TAG, newId);
 				
 				LinearLayout linearLayout;
 				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
 				LayoutInflater inflater2 = LayoutInflater.from(getApplicationContext());
 				
 				int idButton = R.layout.layoutbuttontopologica;
 				int contentlayout = R.layout.contenidolayout;
 				int linea	 = R.layout.layouttextotexto;

 				/** CREAMOS BOTON Y LO AñADIMOS **/
 				LinearLayout linearLayoutBoton = (LinearLayout)inflater.inflate(idButton, null,false);
 				Button boton = (Button)linearLayoutBoton.findViewById(R.id.button1);
 				boton.setText(b.getString("VALUE"));
 				boton.setCompoundDrawablesWithIntrinsicBounds(left,0, R.drawable.ic_bottom1, 0);
 				linearLayout = null;
 				boton.setId(str2int(newId));
 				
 				/**FIN BOTON**/
 				
 				/**CREAMOS CONTENIDO Y LO AñADIMOS**/
 				final LinearLayout contlay = (LinearLayout)inflater2.inflate(contentlayout, null,false);
 				
 				ArrayList<String> datos = b.getStringArrayList("IDENTIFICATION");
 				Log.i(TAG,"FLAG");
 				if(datos != null)
 				{

 					for(String d : datos){
                        Boolean isButton = false;
 						final String[] lineas = d.split(";");
	 					for(int k = 0; k < lineas.length;k++) {
                            String[] informacion = lineas[k].split("&");
                            linearLayout = (LinearLayout) inflater.inflate(linea, null, false);

                            TextView izq = (TextView) linearLayout.findViewById(R.id.textView1);
                            izq.setTextColor(getResources().getColor(R.color.black));
                            final String valor = b.getString("VALUE");
                            if (valor.equals("SERVICIO TELEVISION")) {
                                if (!isButton) {
                                    ImageButton ed = (ImageButton) linearLayout.findViewById(R.id.imageButton1);
                                    ed.setVisibility(View.VISIBLE);
                                    ed.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final CharSequence[] options, actions;
                                            options = new CharSequence[]{"ACTIVAR DECO", "REINICIAR DECO", "REEMPLAZAR DECO", "REINICIAR DSLAM"};
                                            actions = new CharSequence[]{"ACTION101","ACTION102","ACTION103","ACTION201"};

                                            AlertDialog.Builder dialog = new AlertDialog.Builder(VistaTopologica.topo);
                                            dialog.setTitle("Selecione una opción:");
                                            dialog.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, final int which) {
                                                    final String serieantiguaDECO = lineas[1].split("&")[1];
                                                    final String serieantiguaTARJETA = lineas[2].split("&")[1];

                                                    if (actions[which].equals("ACTION101")) {
                                                        Log.w(TAG,"ACTION101--TYPE="+serieantiguaDECO+";"+serieantiguaTARJETA);
                                                        ActionButtonTask ab = new ActionButtonTask(Phone, serieantiguaDECO+";"+serieantiguaTARJETA,actions[which].toString());
                                                        //ab.execute();
                                                    }

                                                    if (actions[which].equals("ACTION102")) {
                                                        Log.w(TAG,"ACTION102--TYPE="+serieantiguaDECO+";"+serieantiguaTARJETA);
                                                        ActionButtonTask ab = new ActionButtonTask(Phone, serieantiguaDECO+";"+serieantiguaTARJETA,actions[which].toString());
                                                        //ab.execute();
                                                    }

                                                    if(actions[which].equals("ACTION103")){
                                                        final Dialog dial = new Dialog(VistaTopologica.topo);
                                                        //dial.setTitle("Datos Nuevo DECO:");
                                                        dial.setContentView(R.layout.new_deco_view);
                                                        final EditText serieDeco = (EditText)dial.findViewById(R.id.editText);
                                                        final EditText serieTarjeta = (EditText)dial.findViewById(R.id.editText2);
                                                        ImageButton ok = (ImageButton)dial.findViewById(R.id.bOK);
                                                        ImageButton nok = (ImageButton)dial.findViewById(R.id.bNOK);

                                                        ok.setOnClickListener(new OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                if (serieDeco.getText().toString().length() > 0
                                                                        && serieTarjeta.getText().toString().length() > 0) {

                                                                    tipo = serieDeco.getText().toString()
                                                                            + ";" + serieTarjeta.getText().toString()
                                                                            + "-" + serieantiguaDECO
                                                                            + ";" + serieantiguaTARJETA;
                                                                    Log.w(TAG,"ACTION103--TYPE="+tipo);
                                                                    ActionButtonTask ab = new ActionButtonTask(Phone, tipo,actions[which].toString());
                                                                    //ab.execute();
                                                                    dial.dismiss();
                                                                } else {
                                                                    Toast.makeText(VistaTopologica.topo,"Debe ingresar ambos números de serie.",Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                        nok.setOnClickListener(new OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                dial.dismiss();
                                                            }
                                                        });

                                                        dial.show();
                                                    }
                                                    if (actions[which].equals("ACTION104")) {
                                                        Log.w(TAG,"ACTION104--TYPE="+serieantiguaDECO+";"+serieantiguaTARJETA);
                                                        ActionButtonTask ab = new ActionButtonTask(Phone, serieantiguaDECO+";"+serieantiguaTARJETA,actions[which].toString());
                                                        //ab.execute();
                                                    }



                                                }
                                            });
                                            dialog.setCancelable(false);
                                            dialog.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {


                                                }
                                            });
                                            dialog.show();
                                        }
                                    });
                                    isButton = true;
                                }
                            }
                            TextView der = (TextView) linearLayout.findViewById(R.id.textView2);
                            der.setTextColor(getResources().getColor(R.color.celeste));
                            if (k == 0 && b.getString("VALUE").equals("SERVICIO TELEVISION")) {
                                der.setBackgroundColor(Color.BLUE);
                                izq.setBackgroundColor(Color.BLUE);
                                der.setTextColor(Color.WHITE);
                                izq.setTextColor(Color.WHITE);

                            }

                            izq.setText(" " + informacion[0] + ":  ");
                            if (informacion.length == 1)
                                der.setText("---");
                            else
                                der.setText(informacion[1]);
                            contlay.addView(linearLayout);


                        }
 					}
	 				
 				}else{
 					continue;
 				}
 				
 				contlay.setVisibility(View.GONE);
 				boton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						Log.d(TAG, arg0.getId()+"");
						Log.d(TAG,"contenido correpondiente: "+ids_contenidos.get(ids_botones.indexOf(arg0.getId())));
						ArrayList<Button> bs = new ArrayList<Button>();
						ArrayList<View> ls = new ArrayList<View>();
						
						int posicion = ids_botones.indexOf(arg0.getId());
						for(int i = 0; i < ids_botones.size(); i++){
							if(i != posicion){
								Button b = (Button)LContenido.findViewById(ids_botones.get(i));								
								View v = LContenido.findViewById(ids_contenidos.get(i));								
								bs.add(b);
								ls.add(v);
								
							}
							
						}
						
						if(contlay.getVisibility() == View.GONE){							
							contlay.setVisibility(View.VISIBLE);
							
							
							for(int i = 0; i<bs.size();i++){
								ls.get(i).setVisibility(View.GONE);
							}
						}else{
							contlay.setVisibility(View.GONE);
						}
					}
 					
 				});
 				
 				int n=0;
 				datos = b.getStringArrayList("SUBELEMENT");
 				if(datos != null){
 					pares = new ArrayList<String>();
 					
 					for(int i = 0; i< datos.size(); i++){
 						String[] d = datos.get(i).split(";");
 						String p="";
 						n = d.length;
 						if(i == 0){
 							for(int j=0;j<n;j++){
 								if(j == 0){
 									p+=d[j];
 								}else
 									p+=";"+d[j];
 							}
 							
 						}
 						else{
 							for(int j=0;j<n;j++){
 								if(j == 0){
 									p+=d[j];
 								}else
 									p+=";"+d[j];
 							}
 						}
 						pares.add(p);
 					}
 					contlay.addView(dibujarTabla(1,datos.size(),n,"#FFFFFF"));
 				}
 				
 				
 				
 				LContenido.addView(linearLayoutBoton);
 				
// 				if(b.getString("TYPE").equals("CAJA")){
// 					ScrollView sv = new ScrollView(getApplicationContext());
// 					sv.addView(contlay);
// 					sv.setId(str2int(newIdCont));
// 					LContenido.addView(sv);
// 				}else{
 					contlay.setId(str2int(newIdCont));
 					LContenido.addView(contlay);
// 				}
 					

					ids_contenidos.add(str2int(newIdCont));
					ids_botones.add(boton.getId());
 			
 				
 			}	
 			
			
//			LContenido.addView(boton);
			
//			rl.setVisibility(View.GONE);
//			LContenido.addView(rl);
				
 			//agregarVistaCertificacion(LContenido, boton);
 			Log.d(TAG,"BOTONES DISPONIBLES: "+ids_botones.toString());
 			Log.d(TAG,"Contenidos DISPONIBLES: "+ids_contenidos.toString());

 			if (this.dialog.isShowing()) {
 		        this.dialog.dismiss();
 		     }
 			
   	    }
 		
 		
   	}

	public void agregarVistaCertificacion(LinearLayout contenido,Button btn){
		
		
	}
	
	//TODO: str2int
	public static int str2int(String a){
		char[] b = a.toCharArray();
		int r = 0;
		int i = 1;
		for(char c: b){
			r+=Character.getNumericValue(c)*i;
			i+=10;
		}
		return r;
	}
	
	//TODO: Dibujar tabla
	public TableLayout dibujarTabla(int tamBorde, int numeroFilas, int numeroColumnas, String colorBorde){
		TableLayout tabla = new TableLayout(this);
		if(numeroFilas > 0 && numeroColumnas > 0){
			TableRow fila = new TableRow(this);

			ArrayList<String> cabeceras = new ArrayList<String>();

			int anchoDefault = getWindowManager().getDefaultDisplay().getWidth()/30;
			int ancho = 0;
			int columna;
			
			for(int i = 0; i < numeroFilas; i ++){
				columna = 0;
				final String[] cols = pares.get(i).split(";");
				fila = new TableRow(this);
				
				for(int j = 0; j < numeroColumnas-1; j++){
                    Boolean telefono_clickable = false;
					RelativeLayout borde = new RelativeLayout(this);
					borde.setPadding(tamBorde, tamBorde, 0, 0);
					
					if(j == numeroColumnas-1){ // SI ES LA ULTIMA COLUMNA
						borde.setPadding(tamBorde, tamBorde, tamBorde, 0);
					}
					if(i == numeroFilas-1){ //SI ES LA ULTIMA FILA
						borde.setPadding(tamBorde, tamBorde, 0, tamBorde);
						if(j == numeroColumnas-1){ // Y ULTIMA COLUMNA
							borde.setPadding(tamBorde, tamBorde, tamBorde, tamBorde);
						}
					}
					
					TextView texto = new TextView(this);
					
					final String tipo;
					final String valor;
					
					
					borde.setBackgroundColor(Color.parseColor(colorBorde));

					texto.setGravity(Gravity.CLIP_HORIZONTAL);
					texto.setPadding(2, 4, 2, 4);
					texto.setTextColor(Color.BLACK);
					
					/** SI ES CABECERA **/
					if(i == 0){
                        tipo = "";
						valor = cols[j].split("/")[1]; //TOMAMOS EL VALOR DE LA CABECERA
						
						/** SETTEAMOS LOS COLORES */
						texto.setBackgroundColor(Color.BLUE);
						texto.setTextColor(Color.WHITE);
						borde.setBackgroundColor(Color.BLUE);
						
						
						cabeceras.add(valor); /** REGISTRAMOS LA CABECERA */
						texto.setText(valor); /**AGREGAMOS EL VALOR A LA TABLA*/
						ancho = valor.length()*anchoDefault; // LE DEFINIMOS EL ANCHO
						texto.setWidth(ancho); 						
						//if(j != numeroColumnas-1)
							borde.addView(texto);
					}
					else{ /** SI NO ES CABECERA... EL RESTO DEL ARREGLO*/



						
						texto.setBackgroundColor(Color.WHITE); /** FONDO BLANCO*/
						String[] asd = cols[columna].split("/");
						
						tipo = asd[0];  /** TOMAMOS EL TIPO DE COLUMNA */
						if(asd.length == 2)
							valor = asd[1]; /** TOMAMOS EL VALOR */
						else
							valor = "--";


                        ancho = valor.length()*anchoDefault;
						if(cabeceras.get(j).equals(tipo)){ // SI EL TIPO CORRESPONDE A LA COLUMNA
							texto.setText(valor); //* LO AGREGO
							columna++;
							texto.setWidth(ancho);
						}else{
							texto.setText("");	
							texto.setWidth(anchoDefault);
						}
						
						
						
						if(cols[cols.length-1].split("/")[1].equals("2")){
								texto.setTextColor(Color.BLUE);
								parActual = cols[0].split("/")[1];
							}

                        if (tipo.compareTo("TELEFONO")==0 && valor.compareTo(" ") != 0 && valor.length() > 2) {
                            telefono_clickable = true;
                            texto.setBackgroundResource(R.drawable.custom_button_search);
                        }
                        if(tipo.compareTo("ESTADO")==0){
                            texto.setBackgroundResource(R.drawable.custom_button_search);
                        }

						borde.addView(texto);
					}

                    if (j == 2 && tipo.compareTo("ESTADO") == 0) {
                        borde.setClickable(true);
                        borde.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(VistaTopologica.topo);
                                dialog.setTitle("Cambiando el par "+parActual+" por "+cols[0].split("/")[1]+":");
                                dialog.setMessage("¿Desea continuar?");
                                dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActionButtonTask a;
                                        if (!parActual.equals(cols[0].split("/")[1])) {
                                            a = new ActionButtonTask(Phone, parActual + ";" + cols[0].split("/")[1], "ACTION301");
                                            a.execute();
                                        } else
                                            Toast.makeText(VistaTopologica.topo, "Error: Par actualmente asignado", Toast.LENGTH_LONG).show();

                                    }
                                });
                                dialog.setCancelable(false);
                                dialog.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                });
                                dialog.show();
                            }
                        });
                    }

                    if(telefono_clickable){
                        borde.setClickable(true);
                        borde.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent certificar = new Intent(VistaTopologica.topo, CertificarPar.class);
                                certificar.putExtra("PHONE",valor);
                                startActivity(certificar);

                            }
                        });
                    }
					/** AGREGO LA COLUMNA A LA FILA **/
					fila.addView(borde);
					
				}
				
				/** AGREGO LA FILA A LA TABLA */
				tabla.addView(fila);
			}
			}
			return tabla;
		}

    private class ActionButtonTask extends AsyncTask<String, Integer, ArrayList<String>> {

        private final ProgressDialog dialog = new ProgressDialog(VistaTopologica.this);
        private String Phone;
        private String Type;
        private String Action;

        public ActionButtonTask(String phone, String type, String action) {
            super();
            this.Phone = phone;
            this.Type = type;
            this.Action = action;
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> todo;
            try {
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = telephonyManager.getDeviceId();
                String IMSI =  telephonyManager.getSimSerialNumber();
                String response = SoapRequestMovistar.getActionButton(IMEI,IMSI, this.Phone,this.Type, this.Action);
                todo = XMLParser.getReturnCode(response);

            } catch (Exception e1) {
                e1.printStackTrace();
                todo = null;
            }

            return todo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Enviando solicitud de accion...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(getApplicationContext(), "Operación Interrumpida.", Toast.LENGTH_SHORT).show();

                }
            });
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            if(s != null && s.size() > 1)
                Toast.makeText(VistaTopologica.topo, s.get(1), Toast.LENGTH_LONG).show();
            if(s == null)
                Toast.makeText(VistaTopologica.topo,"Error en la conexión",Toast.LENGTH_LONG).show();
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
    }



    }
