package cl.inexcell.sistemadegestion;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import cl.inexcell.sistemadegestion.objetos.Deco;
import cl.inexcell.sistemadegestion.objetos.ElementFormulario;
import cl.inexcell.sistemadegestion.objetos.Formulario;
import cl.inexcell.sistemadegestion.objetos.FormularioEnvio;
import cl.inexcell.sistemadegestion.objetos.ParametrosEnvioForm;
import cl.inexcell.sistemadegestion.objetos.ParametrosFormulario;


public class FactActivity extends Activity implements View.OnClickListener {
    Context mContext;
    LinearLayout fatcLayout;
    TextView nombreTecnico;
    Formulario formularioFinal;
    String Phone;
    Boolean isEmail = true;

    int positionInsert = 0;

    ArrayList<FormularioEnvio> formularioEnvio;

    ArrayList<Integer> ids_botones, ids_contenidos, ids_campos;
    ArrayList<EditText> editTextsBA, editTextsTelef, editTextsTelev, editTextsSeriesDeco, editTextsSeriesTarjeta, editTextsSeries, editTextsCierre;
    ArrayList<TextView> TextsBA, TextsTelef, TextsTelev, TextsDeco, TextsSeries, TextsCierre;
    ArrayList<String> editTextsRetiro, TextsRetiro;

    AlertDialog.Builder dialog_preview;
    View preview_view;
    ImageView IVpreview;
    Button verCarnet;
    Bundle uno = new Bundle();

    private Bitmap b = null;
    private Bitmap firma = null;
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

        Phone = getIntent().getExtras().getString("PHONE");

        fatcLayout = (LinearLayout) findViewById(R.id.contenido_fatc);
        nombreTecnico = (TextView) findViewById(R.id.nombreTecnico);

        editTextsBA = new ArrayList<>();
        editTextsTelef = new ArrayList<>();
        editTextsTelev = new ArrayList<>();
        editTextsSeries = new ArrayList<>();
        editTextsSeriesDeco = new ArrayList<>();
        editTextsSeriesTarjeta = new ArrayList<>();
        editTextsCierre = new ArrayList<>();
        TextsBA = new ArrayList<>();
        TextsTelef = new ArrayList<>();
        TextsTelev = new ArrayList<>();
        TextsSeries = new ArrayList<>();
        TextsDeco = new ArrayList<>();
        TextsCierre = new ArrayList<>();
        editTextsRetiro = new ArrayList<>();
        TextsRetiro = new ArrayList<>();
        Pair<String, String> par = new Pair<>("uno", "dos");


        Obtener_Formulario task = new Obtener_Formulario(this);
        task.execute();
    }

    private void generarVista(Formulario f) {
        formularioFinal = f;
        ids_botones = new ArrayList<>();
        ids_contenidos = new ArrayList<>();
        ids_campos = new ArrayList<>();

        nombreTecnico.setText(f.getNombreTecnico());

        for (ElementFormulario ef : f.getElements()) {
            View buttonLayout = LayoutInflater.from(mContext).inflate(R.layout.layoutbuttontopologica, null, false);
            Button boton = (Button) buttonLayout.findViewById(R.id.button1);
            boton.setCompoundDrawablesWithIntrinsicBounds(ef.getDrawable(), 0, R.drawable.ic_bottom1, 0);
            boton.setText(ef.getValue());
            boton.setId(str2int("boton" + ef.getType() + ef.getValue()));


            final View subContenido = LayoutInflater.from(mContext).inflate(R.layout.contenidolayout, null, false);
            subContenido.setVisibility(View.GONE);
            subContenido.setId(str2int("contenido" + ef.getType() + ef.getValue()));

            if (ef.getType().compareTo("Broadband") == 0 || ef.getType().compareTo("Telephony") == 0 || ef.getType().compareTo("DigitalTelevision") == 0) {
                View tableHeader1 = LayoutInflater.from(mContext).inflate(R.layout.tabheader, null, false);
                ((LinearLayout) subContenido).addView(tableHeader1);
            } else if (ef.getType().compareTo("remove") == 0) {
                LinearLayout agregar = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.layoutbuttonadd, null, false);
                Button add = (Button) agregar.findViewById(R.id.buttonAdd);
                add.setText("Ingresar Retiro");
                final View tableHeader2 = LayoutInflater.from(mContext).inflate(R.layout.tabheader_retiro, null, false);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final View content = LayoutInflater.from(mContext).inflate(R.layout.view_ingresar_retiro, null, false);
                        final Spinner spinner = (Spinner) content.findViewById(R.id.retiro_elementos);
                        final LinearLayout decos = (LinearLayout) content.findViewById(R.id.layout_deco);
                        final LinearLayout general = (LinearLayout) content.findViewById(R.id.layout_general);



                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                if (position == 2) {
                                    decos.setVisibility(View.VISIBLE);
                                    general.setVisibility(View.GONE);

                                } else {
                                    general.setVisibility(View.VISIBLE);
                                    decos.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        final String[] elementos = {"Telefono", "Modem", "Deco", "Otros"};
                        new AlertDialog.Builder(mContext)
                                .setView(content)
                                .setTitle("Datos del Retiro")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final int lastInster = TextsRetiro.size();
                                        if (spinner.getSelectedItemPosition() == 2) {

                                            final View uno = LayoutInflater.from(mContext).inflate(R.layout.tabrow_retiro_deco, null, false);

                                            EditText serie1 = (EditText) content.findViewById(R.id.editText13);
                                            EditText serie2 = (EditText) content.findViewById(R.id.editText14);

                                            ImageButton borrar = (ImageButton) uno.findViewById(R.id.button_erase);
                                            borrar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ((ViewManager) subContenido).removeView(uno);
                                                    Log.d("remove", ""+lastInster);
                                                    editTextsRetiro.remove(lastInster);
                                                    TextsRetiro.remove(lastInster);
                                                }
                                            });



                                            TextView elemento = (TextView) uno.findViewById(R.id.elemento);
                                            if (serie1.getText() != null && serie2.getText() != null) {
                                                EditText serieDeco = (EditText) uno.findViewById(R.id.edit_deco);
                                                EditText serieTarjeta = (EditText) uno.findViewById(R.id.edit_tarjeta);
                                                elemento.setText(elementos[spinner.getSelectedItemPosition()]);
                                                serieDeco.setText(serie1.getText());
                                                serieTarjeta.setText(serie2.getText());
                                                ((LinearLayout) subContenido).addView(uno);


                                                editTextsRetiro.add(serie1.getText() + ";" + serie2.getText());
                                                TextsRetiro.add(elementos[spinner.getSelectedItemPosition()]);
                                                Log.d("positionInsert",""+positionInsert);
                                                Log.d("TextRetiro.size()", TextsRetiro.size()+"");
                                                positionInsert++;
                                                tableHeader2.setVisibility(View.VISIBLE);
                                            } else {
                                                Toast.makeText(mContext, "Ingrese ambos números de serie.", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            final View uno = LayoutInflater.from(mContext).inflate(R.layout.tabrow_retiro, null, false);
                                            EditText serie = (EditText) content.findViewById(R.id.editText15);
                                            TextView elemento = (TextView) uno.findViewById(R.id.elemento);

                                            ImageButton borrar = (ImageButton) uno.findViewById(R.id.button_erase);
                                            borrar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ((ViewManager) subContenido).removeView(uno);
                                                    Log.d("remove", ""+lastInster);
                                                    editTextsRetiro.remove(lastInster);
                                                    TextsRetiro.remove(lastInster);
                                                }
                                            });

                                            if (serie.getText() != null) {
                                                EditText serieVista = (EditText) uno.findViewById(R.id.edit_retiro);
                                                elemento.setText(elementos[spinner.getSelectedItemPosition()]);
                                                serieVista.setText(serie.getText());
                                                ((LinearLayout) subContenido).addView(uno);


                                                editTextsRetiro.add(serie.getText().toString());
                                                TextsRetiro.add(elementos[spinner.getSelectedItemPosition()]);

                                                Log.d("",""+positionInsert);
                                                Log.d("TextRetiro.size()", TextsRetiro.size()+"");
                                                positionInsert++;
                                                tableHeader2.setVisibility(View.VISIBLE);
                                            } else {
                                                Toast.makeText(mContext, "Ingrese el número de serie.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                });
                ((LinearLayout) subContenido).addView(agregar);

                tableHeader2.setVisibility(View.GONE);
                ((LinearLayout) subContenido).addView(tableHeader2);
            }


            for (ParametrosFormulario pf : ef.getParameters()) {
                switch (ef.getType()) {
                    case "Broadband":
                        View tableRow1 = LayoutInflater.from(mContext).inflate(R.layout.tabrow, null, false);
                        TextView material1 = (TextView) tableRow1.findViewById(R.id.materialName);
                        EditText cantidad1 = (EditText) tableRow1.findViewById(R.id.cantField);

                        editTextsBA.add(cantidad1);
                        TextsBA.add(material1);

                        material1.setText(pf.getAtributo());
                        cantidad1.setHint(pf.getValue());
                        cantidad1.setEnabled(pf.getEnabled());

                        ((LinearLayout) subContenido).addView(tableRow1);
                        break;
                    case "Telephony":
                        View tableRow2 = LayoutInflater.from(mContext).inflate(R.layout.tabrow, null, false);
                        TextView material2 = (TextView) tableRow2.findViewById(R.id.materialName);
                        EditText cantidad2 = (EditText) tableRow2.findViewById(R.id.cantField);

                        editTextsTelef.add(cantidad2);
                        TextsTelef.add(material2);

                        material2.setText(pf.getAtributo());
                        cantidad2.setHint(pf.getValue());
                        cantidad2.setEnabled(pf.getEnabled());

                        ((LinearLayout) subContenido).addView(tableRow2);
                        break;
                    case "DigitalTelevision":
                        if (pf.getAtributo().compareTo("DecosSerie") == 0) {
                            for (Deco deco : pf.getDecos()) {
                                String[] cabeceras = {"DECO:", "SERIE DECO", "SERIE TARJETA"};
                                String[] datos = {deco.getLabel(), deco.getSerieDeco(), deco.getSerieTarjeta()};
                                for (int i = 0; i < 3; i++) {
                                    View vista = LayoutInflater.from(this).inflate(R.layout.layouttextotexto, null, false);
                                    ((TextView) vista.findViewById(R.id.textView1)).setText(cabeceras[i]);
                                    ((TextView) vista.findViewById(R.id.textView2)).setText(datos[i]);
                                    if (i == 0) {
                                        vista.setBackgroundResource(R.color.celeste);
                                    }
                                    ((LinearLayout) subContenido).addView(vista);
                                }
                            }
                        } else {
                            View tableRow3 = LayoutInflater.from(mContext).inflate(R.layout.tabrow, null, false);
                            TextView material3 = (TextView) tableRow3.findViewById(R.id.materialName);
                            EditText cantidad3 = (EditText) tableRow3.findViewById(R.id.cantField);

                            editTextsTelev.add(cantidad3);
                            TextsTelev.add(material3);

                            material3.setText(pf.getAtributo());
                            cantidad3.setHint(pf.getValue());
                            cantidad3.setEnabled(pf.getEnabled());

                            ((LinearLayout) subContenido).addView(tableRow3);
                        }
                        break;
                    case "remove":
                        break;
                    case "ClosingData":
                        View linea;
                        switch (pf.getAtributo()) {
                            case "Customer":
                                linea = LayoutInflater.from(mContext).inflate(R.layout.view_texto_campo, null, false);
                                TextView title = (TextView) linea.findViewById(R.id.title);
                                EditText campo = (EditText) linea.findViewById(R.id.campo);

                                editTextsCierre.add(campo);
                                TextsCierre.add(title);

                                if (pf.getValue().compareTo("0") != 0)
                                    campo.setHint(pf.getValue());
                                title.setText(pf.getAtributo());
                                campo.setId(str2int("cierrecampo" + pf.getAtributo() + pf.getTypeInput() + pf.getTypeDataInput()));
                                ids_campos.add(campo.getId());
                                ((LinearLayout) subContenido).addView(linea);
                                break;
                            case "Rut":
                                linea = LayoutInflater.from(mContext).inflate(R.layout.view_texto_campo, null, false);
                                TextView title1 = (TextView) linea.findViewById(R.id.title);
                                EditText campo1 = (EditText) linea.findViewById(R.id.campo);

                                editTextsCierre.add(campo1);
                                TextsCierre.add(title1);

                                if (pf.getValue().compareTo("0") != 0)
                                    campo1.setHint(pf.getValue());
                                title1.setText(pf.getAtributo());
                                campo1.setId(str2int("cierrecampo" + pf.getAtributo() + pf.getTypeInput() + pf.getTypeDataInput()));
                                ids_campos.add(campo1.getId());

                                ((LinearLayout) subContenido).addView(linea);
                                break;
                            case "Email":
                                linea = LayoutInflater.from(mContext).inflate(R.layout.view_texto_campo_rut, null, false);
                                CheckBox title2 = (CheckBox) linea.findViewById(R.id.isEmail);
                                final EditText campo2 = (EditText) linea.findViewById(R.id.campo);
                                editTextsCierre.add(campo2);
                                if (pf.getValue().compareTo("0") != 0)
                                    campo2.setHint(pf.getValue());
                                title2.setText(pf.getAtributo());
                                campo2.setId(str2int("cierrecampo" + pf.getAtributo() + pf.getTypeInput() + pf.getTypeDataInput()));

                                title2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        isEmail = isChecked;
                                        if (isChecked) {
                                            campo2.setVisibility(View.VISIBLE);
                                        } else
                                            campo2.setVisibility(View.GONE);
                                    }
                                });

                                ids_campos.add(campo2.getId());
                                TextView title3 = new TextView(mContext);
                                title3.setText("Email");
                                TextsCierre.add(title3);

                                ((LinearLayout) subContenido).addView(linea);
                                break;
                            case "PassportPhoto":
                                View botoneras = LayoutInflater.from(this).inflate(R.layout.view_firmafoto, null, false);
                                Button firmar = (Button) botoneras.findViewById(R.id.boton_firma);
                                final Button verFirma = (Button) botoneras.findViewById(R.id.button_verfirma);
                                verCarnet = (Button) botoneras.findViewById(R.id.button_vercarnet);

                                firmar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View lay = LayoutInflater.from(mContext).inflate(R.layout.view_signature, null, false);
                                        final SignaturePad signature = (SignaturePad) lay.findViewById(R.id.signature_pad);

                                        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
                                        b.setView(lay);
                                        b.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                firma = signature.getSignatureBitmap();
                                                verFirma.setEnabled(true);
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
                                    }
                                });

                                verFirma.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog_preview = new AlertDialog.Builder(mContext);
                                        dialog_preview.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        preview_view = LayoutInflater.from(mContext).inflate(R.layout.view_preview, null, false);
                                        IVpreview = (ImageView) preview_view.findViewById(R.id.preview);
                                        IVpreview.setImageBitmap(firma);
                                        dialog_preview.setView(preview_view);
                                        dialog_preview.setTitle("Vista Previa Firma");
                                        dialog_preview.show();
                                    }
                                });

                                verCarnet.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog_preview = new AlertDialog.Builder(mContext);
                                        dialog_preview.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        preview_view = LayoutInflater.from(mContext).inflate(R.layout.view_preview, null, false);
                                        IVpreview = (ImageView) preview_view.findViewById(R.id.preview);
                                        IVpreview.setImageBitmap(b);
                                        dialog_preview.setView(preview_view);
                                        dialog_preview.setTitle("Vista Previa Carnet");
                                        dialog_preview.show();
                                    }
                                });

                                ((LinearLayout) subContenido).addView(botoneras);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }


            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    ArrayList<Button> bs = new ArrayList<>();
                    ArrayList<View> ls = new ArrayList<>();

                    int posicion = ids_botones.indexOf(arg0.getId());
                    for (int i = 0; i < ids_botones.size(); i++) {
                        if (i != posicion) {
                            Button b = (Button) fatcLayout.findViewById(ids_botones.get(i));
                            View v = fatcLayout.findViewById(ids_contenidos.get(i));
                            bs.add(b);
                            ls.add(v);
                        }
                    }

                    if (subContenido.getVisibility() == View.GONE) {
                        subContenido.setVisibility(View.VISIBLE);
                        for (int i = 0; i < bs.size(); i++) {
                            ls.get(i).setVisibility(View.GONE);
                        }
                    } else {
                        subContenido.setVisibility(View.GONE);
                    }
                }
            });

            fatcLayout.addView(buttonLayout);
            fatcLayout.addView(subContenido);

            ids_contenidos.add(subContenido.getId());
            ids_botones.add(boton.getId());

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
     * TODO * Boton Guardar Informacion *
     */
    public void guardarInformacion(View view) {
        /*Intent n = new Intent(this, ActividadPDF.class);
        startActivity(n);
        */
        formularioEnvio = new ArrayList<>();
        FormularioEnvio form = new FormularioEnvio();
        ArrayList<ParametrosEnvioForm> parametrosEnvioForms;

        Boolean isOK = true;
        for (ElementFormulario element : formularioFinal.getElements()) {
            if(!isOK)
                break;
            parametrosEnvioForms = new ArrayList<>();
            ArrayList<ParametrosFormulario> parametros = element.getParameters();
            for (int i = 0; i < parametros.size(); i++) {
                if(!isOK)
                    break;
                ParametrosEnvioForm p = new ParametrosEnvioForm();
                switch (element.getType()) {
                    case "Broadband":
                        if (editTextsBA.get(i).getText().toString().compareTo("") != 0) {
                            p.setAttribute(TextsBA.get(i).getText().toString());
                            p.setValue(editTextsBA.get(i).getText().toString());
                            parametrosEnvioForms.add(p);
                            Log.d("PRUEBA", p.getAttribute() + ": " + p.getValue());
                        }else{
                            if(parametros.get(i).getRequired()){
                                Toast.makeText(mContext, "El parametro "+TextsBA.get(i).getText()+" es obligatorio", Toast.LENGTH_LONG).show();
                                isOK=false;
                            }
                        }
                        break;
                    case "Telephony":
                        if (editTextsTelef.get(i).getText().toString().compareTo("") != 0) {
                            p.setAttribute(TextsTelef.get(i).getText().toString());
                            p.setValue(editTextsTelef.get(i).getText().toString());
                            parametrosEnvioForms.add(p);
                            Log.d("PRUEBA", p.getAttribute() + ": " + p.getValue());
                        }else{
                            if(parametros.get(i).getRequired()){
                                Toast.makeText(mContext, "El parametro "+TextsTelef.get(i).getText()+" es obligatorio", Toast.LENGTH_LONG).show();
                                isOK=false;
                            }
                        }
                        break;
                    case "DigitalTelevision":
                        if(parametros.get(i).getAtributo().compareTo("DecosSerie")==0)
                            break;
                        if (editTextsTelev.get(i).getText().toString().compareTo("") != 0) {
                            p.setAttribute(TextsTelev.get(i).getText().toString());
                            p.setValue(editTextsTelev.get(i).getText().toString());
                            parametrosEnvioForms.add(p);
                            Log.d("PRUEBA", p.getAttribute() + ": " + p.getValue());
                        }else{
                            if(parametros.get(i).getRequired()){
                                Toast.makeText(mContext, "El parametro "+TextsTelev.get(i).getText()+" es obligatorio", Toast.LENGTH_LONG).show();
                                isOK = false;
                            }
                        }
                        break;
                    case "ClosingData":
                        if(parametros.get(i).getAtributo().compareTo("PassportPhoto")==0){
                            if(b!= null && firma != null){
                                p.setAttribute("Signature");
                                p.setValue(Funciones.encodeTobase64(firma));
                                parametrosEnvioForms.add(p);
                                p = new ParametrosEnvioForm();
                                p.setAttribute("License");
                                p.setValue(Funciones.encodeTobase64(b));
                                parametrosEnvioForms.add(p);
                            }else{
                                Toast.makeText(mContext, "Debe registrar la Firma y una fotografía de la Cédula de Identidad", Toast.LENGTH_LONG).show();
                                isOK=false;
                            }
                        }else if (editTextsCierre.get(i).getText().toString().compareTo("") != 0) {

                            if(parametros.get(i).getAtributo().compareTo("Email")!=0) {
                                p.setAttribute(TextsCierre.get(i).getText().toString());
                                p.setValue(editTextsCierre.get(i).getText().toString());
                                parametrosEnvioForms.add(p);
                                Log.d("PRUEBA", p.getAttribute() + ": " + p.getValue());
                            }else{
                                if(isEmail){
                                    p.setAttribute(TextsCierre.get(i).getText().toString());
                                    p.setValue(editTextsCierre.get(i).getText().toString());
                                    parametrosEnvioForms.add(p);
                                    Log.d("PRUEBA", p.getAttribute() + ": " + p.getValue());
                                }
                            }
                        }else{
                            if(parametros.get(i).getRequired()){
                                if(parametros.get(i).getAtributo().compareTo("Email")!=0) {
                                    Toast.makeText(mContext, "El parametro " + TextsCierre.get(i).getText() + " es obligatorio", Toast.LENGTH_LONG).show();
                                    isOK=false;
                                }else{
                                    if(isEmail) {
                                        Toast.makeText(mContext, "El parametro " + TextsCierre.get(i).getText() + " es obligatorio", Toast.LENGTH_LONG).show();
                                        isOK=false;
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            if(element.getType().compareTo("remove")==0) {
                parametrosEnvioForms = new ArrayList<>();
                ParametrosEnvioForm p = new ParametrosEnvioForm();
                for (int k = 0; k < TextsRetiro.size(); k++) {
                    Log.d("", TextsRetiro.get(k) + " - " + editTextsRetiro.get(k));
                    p.setAttribute(TextsRetiro.get(k));
                    p.setValue(editTextsRetiro.get(k));
                    parametrosEnvioForms.add(p);
                    Log.d("PRUEBA", p.getAttribute() + ": " + p.getValue());
                }
                form.setType(element.getType());
                form.setParametros(parametrosEnvioForms);
                formularioEnvio.add(form);
            }else {
                form.setType(element.getType());
                form.setParametros(parametrosEnvioForms);
                formularioEnvio.add(form);
            }
        }

        Enviar u = new Enviar(mContext);
        u.execute();
    }

    /**
     * Boton Camara *
     */
    public void capturarImagen(View v) {

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
            verCarnet.setEnabled(true);
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
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = telephonyManager.getDeviceId();
                String IMSI = telephonyManager.getSimSerialNumber();
                //String request = SoapRequestMovistar.postCertifyDSL(Phone,IMEI,IMSI, "?","?");
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

    class Enviar extends AsyncTask<String, String, String> {
        Context eContext;
        ProgressDialog dialog;

        Enviar(Context eContext) {
            this.eContext = eContext;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(eContext);
            dialog.setMessage("Enviando Formulario...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = telephonyManager.getDeviceId();
                String IMSI = telephonyManager.getSimSerialNumber();
                String request = SoapRequestMovistar.guardarFact(Phone, IMEI, IMSI, "?", "?", formularioEnvio);
                ArrayList<String> parse = XMLParser.getReturnCode(request);
                return "";
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String formulario) {
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    //TODO: str2int
    public static int str2int(String a) {
        char[] b = a.toCharArray();
        int r = 0;
        int i = 1;
        for (char c : b) {
            r += Character.getNumericValue(c) * i;
            i += 10;
        }
        return r;
    }

}
