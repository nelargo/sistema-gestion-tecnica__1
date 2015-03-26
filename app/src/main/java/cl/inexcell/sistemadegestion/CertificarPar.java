package cl.inexcell.sistemadegestion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class CertificarPar extends Activity {
	private String TAG	= "CERTIFICARPAR";
	private ArrayList<String> res;
	private CertificarDSL asyncDSL;
    private LinearLayout CONTENIDO;
    String Phone;

	private String asd;


	private boolean certifyDslCorrecto;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	public static final int DIALOG_DOWNLOAD_PROGRESS1 = 1;
    private ProgressDialog mProgressDialog, mProgressDialog1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Activity sin parte superior
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_certificar_par);
        CONTENIDO = (LinearLayout)findViewById(R.id.certificacion_par_content);
        Phone = getIntent().getStringExtra("PHONE");
        Log.i(TAG, Phone);
		certificar();

	}

	public void certificar(){

		asyncDSL = new CertificarDSL();
		asyncDSL.execute();
	}



	public void volver(View view){
		this.finish();
	}

	public void certificarAgain(View v){
		Intent i = new Intent(VistaTopologica.topo,CertificarPar.class);
		i.putExtra("PHONE", Phone);
		startActivity(i);
		this.finish();
	}
	public void fin_certificar(View v){
		volver(null);
	}


	public void shutdown1(View v){
		VistaTopologica.topo.finish();
		Principal.p.finish();
		finish();
	}


   	private class CertificarDSL extends AsyncTask<String,Integer,ArrayList<String>> {

   		private final ProgressDialog dialog = new ProgressDialog(CertificarPar.this);

 		protected void onPreExecute() {
 			this.dialog.setMessage("Buscando información sobre el par...");
 			this.dialog.setCanceledOnTouchOutside(false);
 			this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Operación Interrumpida.", Toast.LENGTH_SHORT).show();

					CertificarPar.this.finish();
				}
			});
 		    this.dialog.show();
         }

   	    protected ArrayList<String> doInBackground(String... params) {

 			String respuesta = null;
   			res = new ArrayList<String>();
   			nada1();
   			try {
//
   				
   				TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
   				String IMEI = telephonyManager.getDeviceId();
   				String IMSI =  telephonyManager.getSimSerialNumber();

                    if(Phone.compareTo("00222011551")!=0) {
                        respuesta = SoapRequestMovistar.getCertifyDSL(Phone, "04", "", IMEI, IMSI);
                    }else
                        respuesta = nada1();

                    Log.w(TAG, "RESPONSE\n"+respuesta);
                    if(XMLParser.getReturnCode(respuesta).get(0).equals("0")) {
                        Log.w(TAG, "RESPONSE OK");
                        certifyDslCorrecto = true;
                        String xml = respuesta.replace("\n","")
                                .replace("<![CDATA[", "")
                                .replace("]]>", "")
                                .replace("</VPT>", "")
                                .replace("<VPT>", "")
                                .replace("&lt;","<")
                                .replace("&gt;",">");
                        Log.w(TAG,"PRE PARSE "+xml);
                        res = XMLParser.getCertificationPar(xml);

                        Log.w(TAG, "RESPONSE PARSE\n"+res.toString());
                    }
   				    else{
                        Log.w(TAG, "RESPONSE NOK");
                        certifyDslCorrecto = false;
                        res = XMLParser.getReturnCode(respuesta);

                    }


   				
   			} catch (Exception e1) {
   				e1.printStackTrace();
                Log.e(TAG, "EXCEPTION!:" + e1.getMessage());
                res = null;
   			}   			
   	        return res;
   	    }
   	    

 		protected void onPostExecute(ArrayList<String> result) {

            if(certifyDslCorrecto) {
                for (String linea : result) {
                    String[] datos = linea.split("&");
                    for (String dato : datos) {
                        String[] info = dato.split(";");
                        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                        int id_linea = R.layout.layouttextotexto;
                        LinearLayout contlay = (LinearLayout) inflater.inflate(id_linea, null, false);
                        TextView izq = (TextView) contlay.findViewById(R.id.textView1);
                        TextView der = (TextView) contlay.findViewById(R.id.textView2);

                        if (info.length > 1) {
                            //Muestro el par de datos
                            izq.setText(info[0] + ": ");
                            der.setText(info[1]);
                            izq.setTextColor(Color.BLACK);
                            der.setTextColor(getResources().getColor(R.color.celeste));
                        } else {
                            //Muestro el titulo del grupo de datos
                            izq.setText(info[0]);
                            der.setText("");
                            izq.setTextColor(Color.WHITE);
                            izq.setTextSize(18);
                            izq.setBackgroundColor(Color.BLUE);
                        }
                        CONTENIDO.addView(contlay);
                    }
                }
            }
            else{
                if(result != null)
                    Log.w(TAG, result.get(0));
                else
                    Log.w(TAG, "NO HUBO RESPUESTA");
                volver(null);
            }
 			if (this.dialog.isShowing()) {
 		        this.dialog.dismiss();
 		     }
   	    }
   	}

    public String nada1() {
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Demo\">" +
                "<SOAP-ENV:Body>" +
                "<ns1:CertifyDSLResponse xmlns:ns1=\"urn:Demo\">" +
                "<ResponseCertifyDSL xsi:type=\"tns:ResponseCertifyDSL\">" +
                "<Operation xsi:type=\"tns:OperationType\">" +
                "<OperationCode xsi:type=\"xsd:string\">?</OperationCode>" +
                "<OperationId xsi:type=\"xsd:string\">0</OperationId>" +
                "<DateTime xsi:type=\"xsd:string\">201503031630</DateTime>" +
                "<IdUser xsi:type=\"xsd:string\">1</IdUser>" +
                "<IMEI xsi:type=\"xsd:string\">355847057585809</IMEI>" +
                "<IMSI xsi:type=\"xsd:string\">8956023100065957322</IMSI>" +
                "</Operation>" +
                "<Service xsi:type=\"tns:ServiceCertifyDSLOut\">" +
                "<CertifyDSL xsi:type=\"tns:CertifyDSLOut\">" +
                "<Output xsi:type=\"tns:CertifyDSLOutData\">" +
                "<CertifyParameter xsi:type=\"tns:CertifyParameterType\">" +
                "<Name xsi:type=\"xsd:string\">Result</Name>" +
                "<Value xsi:type=\"xsd:string\"><![CDATA[<VPT><Request><Name>Area</Name><Value>2</Value><Name>Fono</Name><Value>22297267</Value><Name>Fecha</Name><Value>20150312</Value></Request><Parameters><InputParameters>\n" +
                "<Name>Community</Name><Value>public</Value><Name>Rack</Name><Value>1</Value><Name>Shelf</Name><Value>1</Value><Name>Slot</Name><Value>15</Value><Name>Port</Name><Value>4</Value><Name>Script</Name><Value>isam7342_serrviceflow.pl</Value><Name>Ip</Name><Value>10.101.8.11</Value><Name>Model</Name><Value>ISAM_7342_FTTU</Value></InputParameters>\n" +
                "<ElectricParameters>\n" +
                "<Name>ActualSpeed_Up</Name><Value>73880</Value><Name>ActualSpeed_Dw</Name><Value>OK</Value><Name>MaxSpeed_Up</Name><Value>lock</Value><Name>MaxSpeed_Dw</Name><Value>down</Value><Name>AttUp</Name><Value>ALCL00000000</Value><Name>AttDw</Name><Value></Value><Name>OcUp</Name><Value></Value><Name>OcDw</Name><Value></Value><Name>Vendor</Name><Value>ALCATEL</Value><Name>Dslam</Name><Value>APOQ_OLT_2</Value><Name>PortType</Name><Value>GPON</Value><Name>PortAdminStatus</Name><Value>up</Value><Name>PortOperStatus</Name><Value>44318</Value><Name>Model</Name><Value>ISAM_7342_FTTU</Value><Name>Profile</Name><Value>unlock</Value></ElectricParameters>\n" +
                "</Parameters></VPT>]]></Value>" +
                "<Code xsi:type=\"xsd:string\">0</Code>" +
                "<Description xsi:type=\"xsd:string\">OK</Description>" +
                "</CertifyParameter>" +
                "<Return xsi:type=\"tns:ReturnType\">" +
                "<Code xsi:type=\"xsd:string\">0</Code>" +
                "<Description xsi:type=\"xsd:string\">OK</Description>" +
                "</Return>" +
                "</Output>" +
                "</CertifyDSL>" +
                "</Service>" +
                "</ResponseCertifyDSL>" +
                "</ns1:CertifyDSLResponse>" +
                "</SOAP-ENV:Body>" +
                "</SOAP-ENV:Envelope>";
    }

}
