package cl.inexcell.sistemadegestion;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cl.inexcell.sistemadegestion.objetos.ElementFormulario;
import cl.inexcell.sistemadegestion.objetos.Formulario;
import cl.inexcell.sistemadegestion.objetos.ParametrosFormulario;


public class FactActivity extends Activity implements View.OnClickListener {
    Context mContext;
    LinearLayout fatcLayout;
    TextView nombreTecnico;

    private Bitmap b = null;
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    final CharSequence[] opcionCaptura = {
            "Tomar Fotografía"
    };
    String name = Environment.getExternalStorageDirectory() + "/carnet.jpg"; //picture filename

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fatc);

        /** ASIGNACIONES */
        mContext = this;

        fatcLayout = (LinearLayout) findViewById(R.id.contenido_fatc);
        nombreTecnico = (TextView) findViewById(R.id.nombreTecnico);

        Obtener_Formulario task = new Obtener_Formulario(this);
        task.execute();
    }

    private void generarVista(Formulario f) {

        nombreTecnico.setText(f.getNombreTecnico());

        for (ElementFormulario ef : f.getElements()) {
            View buttonLayout = LayoutInflater.from(mContext).inflate(R.layout.layoutbuttontopologica, null, false);
            Button boton = (Button) buttonLayout.findViewById(R.id.button1);
            boton.setCompoundDrawablesWithIntrinsicBounds(ef.getDrawable(), 0, R.drawable.ic_bottom1, 0);
            boton.setText(ef.getValue());


            final View subContenido = LayoutInflater.from(mContext).inflate(R.layout.contenidolayout, null, false);

            View tableHeader = LayoutInflater.from(mContext).inflate(R.layout.tabheader, null, false);
            ((LinearLayout) subContenido).addView(tableHeader);


            View tableRow;

            for (ParametrosFormulario pf : ef.getParameters()) {
                tableRow = LayoutInflater.from(mContext).inflate(R.layout.tabrow, null, false);
                TextView material = (TextView) tableRow.findViewById(R.id.materialName);
                EditText cantidad = (EditText) tableRow.findViewById(R.id.cantField);

                material.setText(pf.getAtributo());
                cantidad.setHint(pf.getValue());
                cantidad.setEnabled(pf.getEnabled());

                ((LinearLayout) subContenido).addView(tableRow);
            }

            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (subContenido.getVisibility() == View.GONE)
                        subContenido.setVisibility(View.VISIBLE);
                    else
                        subContenido.setVisibility(View.GONE);
                }
            });
            fatcLayout.addView(buttonLayout);
            fatcLayout.addView(subContenido);


        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            /*case R.id.button_nuevoretiro:
                final View vista = LayoutInflater.from(this).inflate(R.layout.view_retiros, null, false);
                ((TextView) vista.findViewById(R.id.aparato)).setText("Aparato");
                ((TextView) vista.findViewById(R.id.serie)).setText("1234567890");
                layout4.addView(vista);
                break;*/
            /*case R.id.boton_firma:
                View lay = LayoutInflater.from(this).inflate(R.layout.view_signature, null, false);
                final SignaturePad signature = (SignaturePad) lay.findViewById(R.id.signature_pad);

                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setView(lay);
                b.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firma.setImageBitmap(signature.getSignatureBitmap());
                        firma.setVisibility(View.VISIBLE);
                        dialogInterface.dismiss();
                    }
                });
                b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                b.setNeutralButton("Borrar", null);
                final AlertDialog dialog = b.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface d) {

                        Button b = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                signature.clear();
                            }
                        });
                    }
                });

                dialog.show();
                break;*/
            default:
                break;
        }
    }

    public void shutdown(View v) {
        if (Principal.p != null)
            Principal.p.finish();
        finish();
    }

    /**
     * Boton Guardar Informacion *
     */
    public void guardarInformacion(View view) {


    }

    /**
     * Boton Camara *
     */
    public void capturarImagen(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escoja una Opcion:");
        builder.setIcon(R.drawable.ic_camera);
        builder.setItems(opcionCaptura, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                int code = TAKE_PICTURE;
                if (item == TAKE_PICTURE) {
                    Uri output = Uri.fromFile(new File(name));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                } else if (item == SELECT_PICTURE) {
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
        } else if (requestCode == SELECT_PICTURE) {
            Uri selectedImage = data.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                b = BitmapFactory.decodeStream(bis);

            } catch (FileNotFoundException e) {
            }
        }
        try {
            b = Bitmap.createScaledBitmap(b, 640, 480, true);
        } catch (Exception ex) {
        }


    }

    /**
     * Boton Volver *
     */
    public void volver(View view) {
        finish();

        // Vibrar al hacer click
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }

    /**
     * TODO: ASYNC
     */

    class Obtener_Formulario extends AsyncTask<String, String, Formulario> {
        Context tContext;

        public Obtener_Formulario(Context context) {
            tContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Formulario doInBackground(String... params) {
            try {
                Formulario parse = XMLParser.getForm(getResponse());
                return parse;

            } catch (Exception e) {
                Log.e("FactActivity", e.getMessage() + ":\n" + e.getCause());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Formulario formulario) {
            if (formulario != null) {

                if (formulario.getReturnCode() == 0)
                    generarVista(formulario);
                else {
                    Toast.makeText(mContext, formulario.getReturnDescription(), Toast.LENGTH_LONG).show();
                    FactActivity.this.finish();
                }

            }

        }

        private String getResponse() {
            return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"urn:Demo\">" +
                    "<SOAP-ENV:Body>" +
                    "<ns1:PostCertifyDSLResponse xmlns:ns1=\"urn:Demo\">" +
                    "<ResponsePostCertifyDSL xsi:type=\"tns:ResponsePostCertifyDSL\">" +
                    "<Operation xsi:type=\"tns:OperationType1\">" +
                    "<OperationCode xsi:type=\"xsd:string\">?</OperationCode>" +
                    "<OperationId xsi:type=\"xsd:string\">?</OperationId>" +
                    "<DateTime xsi:type=\"xsd:string\">?</DateTime>" +
                    "<IdUser xsi:type=\"xsd:string\">?</IdUser>" +
                    "<IMEI xsi:type=\"xsd:string\">353649052038772</IMEI>" +
                    "<IMSI xsi:type=\"xsd:string\">?</IMSI>" +
                    "<Telefono xsi:type=\"xsd:string\">2</Telefono>" +
                    "<Television xsi:type=\"xsd:string\">2</Television>" +
                    "<BandaAncha xsi:type=\"xsd:string\">1</BandaAncha>" +
                    "<NombreTecnico xsi:type=\"xsd:string\">CARRASCO ZURITA LUIS</NombreTecnico>" +
                    "</Operation>" +
                    "<Service xsi:type=\"tns:ServicePostCertifyDSLOut\">" +
                    "<PostCertifyDSL xsi:type=\"tns:PostCertifyDSLOut\">" +
                    "<Output xsi:type=\"tns:PostCertifyDSLOutData\">" +
                    "<Element xsi:type=\"tns:ElementType3\">" +
                    "<Id xsi:type=\"xsd:string\">0</Id>" +
                    "<Type xsi:type=\"xsd:string\">Broadband</Type>" +
                    "<Value xsi:type=\"xsd:string\">SERVICIO BANCHA ANCHA</Value>" +
                    "<Identification xsi:type=\"tns:IdentificationType3\">" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Int2p</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">JumperRoBl</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Spliter</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "</Identification>" +
                    "</Element>" +
                    "<Element xsi:type=\"tns:ElementType3\">" +
                    "<Id xsi:type=\"xsd:string\">1</Id>" +
                    "<Type xsi:type=\"xsd:string\">DigitalTelevision</Type>" +
                    "<Value xsi:type=\"xsd:string\">SERVICIO TELEVISION</Value>" +
                    "<Identification xsi:type=\"tns:IdentificationType3\">" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Antenna</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">CardTV</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Connector</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">LNB</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">RG6</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">DecosSerie</Attribute>" +
                    "<Value xsi:type=\"xsd:string\"/>" +
                    "<typeInput xsi:type=\"xsd:string\">label</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">text</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "<SeriesDecos xsi:type=\"tns:SeriesDecosType\">" +
                    "<Label xsi:type=\"xsd:string\">ECHOSTAR / DSB-626CL</Label>" +
                    "<SerieDeco xsi:type=\"xsd:string\">1629252403</SerieDeco>" +
                    "<SerieTarjeta xsi:type=\"xsd:string\">0324308971</SerieTarjeta>" +
                    "</SeriesDecos>" +
                    "<SeriesDecos xsi:type=\"tns:SeriesDecosType\">" +
                    "<Label xsi:type=\"xsd:string\">ECHOSTAR / SD-646</Label>" +
                    "<SerieDeco xsi:type=\"xsd:string\">1779258803</SerieDeco>" +
                    "<SerieTarjeta xsi:type=\"xsd:string\">0324308972</SerieTarjeta>" +
                    "</SeriesDecos>" +
                    "</Parameters>" +
                    "</Identification>" +
                    "</Element>" +
                    "<Element xsi:type=\"tns:ElementType3\">" +
                    "<Id xsi:type=\"xsd:string\">2</Id>" +
                    "<Type xsi:type=\"xsd:string\">Telephony</Type>" +
                    "<Value xsi:type=\"xsd:string\">SERVICIO TELEFONIA</Value>" +
                    "<Identification xsi:type=\"tns:IdentificationType3\">" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Acometida</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Int1p</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Jumper</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "</Identification>" +
                    "</Element>" +
                    "<Element xsi:type=\"tns:ElementType3\">" +
                    "<Id xsi:type=\"xsd:string\">3</Id>" +
                    "<Type xsi:type=\"xsd:string\">remove</Type>" +
                    "<Value xsi:type=\"xsd:string\">RETIROS</Value>" +
                    "<Identification xsi:type=\"tns:IdentificationType3\">" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Phone</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Modem</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Decos</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">others</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">numeric</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">false</Required>" +
                    "</Parameters>" +
                    "</Identification>" +
                    "</Element>" +
                    "<Element xsi:type=\"tns:ElementType3\">" +
                    "<Id xsi:type=\"xsd:string\">4</Id>" +
                    "<Type xsi:type=\"xsd:string\">ClosingData</Type>" +
                    "<Value xsi:type=\"xsd:string\">DATOS DE CIERRE</Value>" +
                    "<Identification xsi:type=\"tns:IdentificationType3\">" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Customer</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">text</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">true</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Rut</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">text</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">true</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">Email</Attribute>" +
                    "<Value xsi:type=\"xsd:string\">0</Value>" +
                    "<typeInput xsi:type=\"xsd:string\">Box</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">text</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">true</Required>" +
                    "</Parameters>" +
                    "<Parameters xsi:type=\"tns:ParametersType3\">" +
                    "<Attribute xsi:type=\"xsd:string\">PassportPhoto</Attribute>" +
                    "<Value xsi:type=\"xsd:string\"/>" +
                    "<typeInput xsi:type=\"xsd:string\">button</typeInput>" +
                    "<typeDataInput xsi:type=\"xsd:string\">text</typeDataInput>" +
                    "<Enabled xsi:type=\"xsd:string\">true</Enabled>" +
                    "<Required xsi:type=\"xsd:string\">true</Required>" +
                    "</Parameters>" +
                    "</Identification>" +
                    "</Element>" +
                    "<Return xsi:type=\"tns:ReturnType\">" +
                    "<Code xsi:type=\"xsd:string\">0</Code>" +
                    "<Description xsi:type=\"xsd:string\">Informacion enviada</Description>" +
                    "</Return>" +
                    "</Output>" +
                    "</PostCertifyDSL>" +
                    "</Service>" +
                    "</ResponsePostCertifyDSL>" +
                    "</ns1:PostCertifyDSLResponse>" +
                    "</SOAP-ENV:Body>" +
                    "</SOAP-ENV:Envelope>";
        }
    }

}
