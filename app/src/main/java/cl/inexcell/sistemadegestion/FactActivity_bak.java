package cl.inexcell.sistemadegestion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class FactActivity_bak extends Activity implements View.OnClickListener {
    Context mContext;
    public static Activity fatc;
    ArrayList<String> decos, datos_cliente;
    LinearLayout layout1, layout2, layout3, layout4, layout5;
    EditText nombre_cliente, rut_cliente, mail_cliente;
    Button button1, button2, button3, button4, button5, bFirma, verFirma, verCarnet;
    CheckBox isEmail;

    AlertDialog.Builder dialog_preview;
    View preview_view;
    ImageView IVpreview;

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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fatc_bak);


        decos = getIntent().getExtras().getStringArrayList("DECOS");
        datos_cliente = getIntent().getExtras().getStringArrayList("CLIENTE");

        layout1 = (LinearLayout) findViewById(R.id.contenido_banda_ancha);
        layout2 = (LinearLayout) findViewById(R.id.contenido_television);
        layout3 = (LinearLayout) findViewById(R.id.contenido_telefonia);
        layout4 = (LinearLayout) findViewById(R.id.retiros_layout);
        layout5 = (LinearLayout) findViewById(R.id.closing_layout);
        //firma = (ImageView)findViewById(R.id.firma);
        nombre_cliente = (EditText) findViewById(R.id.nombre_cliente);
        rut_cliente = (EditText) findViewById(R.id.rut_cliente);
        mail_cliente = (EditText) findViewById(R.id.mail_cliente);
        isEmail = (CheckBox) findViewById(R.id.isEmail);
        isEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mail_cliente.setVisibility(View.VISIBLE);
                }else
                    mail_cliente.setVisibility(View.GONE);
            }
        });


        String[] clienteString = datos_cliente.get(0).split("&");
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        bFirma = (Button) findViewById(R.id.boton_firma);

        verFirma = (Button) findViewById(R.id.button_verfirma);
        verCarnet = (Button) findViewById(R.id.button_vercarnet);


        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        bFirma.setOnClickListener(this);

        nombre_cliente.setText(clienteString[1].split(";")[1]);
        rut_cliente.setText(clienteString[0].split(";")[1]);

        mostrar_decos();

        dialog_preview = new AlertDialog.Builder(this);
        dialog_preview.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        preview_view = LayoutInflater.from(this).inflate(R.layout.view_preview, null, false);
        IVpreview = (ImageView) preview_view.findViewById(R.id.preview);
    }

    private void mostrar_decos() {

        for (String d : decos) {
            String[] datos = d.split("&");
            for (int i = 0; i < datos.length; i++) {
                View vista = LayoutInflater.from(this).inflate(R.layout.layouttextotexto, null, false);
                String[] lineas = datos[i].split(";");
                ((TextView) vista.findViewById(R.id.textView1)).setText(lineas[0]);
                ((TextView) vista.findViewById(R.id.textView2)).setText(lineas[1]);
                if (i == 0) {
                    vista.setBackgroundResource(R.color.celeste);
                }
                layout2.addView(vista);
            }
        }
    }

    public void ver_firma(View v) {
        dialog_preview = new AlertDialog.Builder(this);
        dialog_preview.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        preview_view = LayoutInflater.from(this).inflate(R.layout.view_preview, null, false);
        IVpreview = (ImageView) preview_view.findViewById(R.id.preview);
        IVpreview.setImageBitmap(firma);
        dialog_preview.setView(preview_view);
        dialog_preview.setTitle("Vista Previa Firma");
        dialog_preview.show();
    }

    public void ver_carnet(View v) {
        dialog_preview = new AlertDialog.Builder(this);
        dialog_preview.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        preview_view = LayoutInflater.from(this).inflate(R.layout.view_preview, null, false);
        IVpreview = (ImageView) preview_view.findViewById(R.id.preview);
        IVpreview.setImageBitmap(b);
        dialog_preview.setView(preview_view);
        dialog_preview.setTitle("Vista Previa Carnet");
        dialog_preview.show();

    }

    public void ingresar_retiro(View v) {
        View content = LayoutInflater.from(this).inflate(R.layout.view_ingresar_retiro, null, false);
        Spinner spinner = (Spinner)content.findViewById(R.id.retiro_elementos);
        final LinearLayout decos = (LinearLayout)content.findViewById(R.id.layout_deco);
        final LinearLayout general = (LinearLayout)content.findViewById(R.id.layout_general);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 2){
                    decos.setVisibility(View.VISIBLE);
                    general.setVisibility(View.GONE);
                }else{
                    general.setVisibility(View.VISIBLE);
                    decos.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        new AlertDialog.Builder(this)
                .setView(content)
                .setTitle("Datos del Retiro")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button1:
                if (layout1.getVisibility() == View.GONE) {
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                } else {
                    layout1.setVisibility(View.GONE);
                }
                break;
            case R.id.button2:
                if (layout2.getVisibility() == View.GONE) {
                    layout2.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                } else {
                    layout2.setVisibility(View.GONE);
                }
                break;
            case R.id.button3:
                if (layout3.getVisibility() == View.GONE) {
                    layout3.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                } else {
                    layout3.setVisibility(View.GONE);
                }
                break;
            case R.id.button4:
                if (layout4.getVisibility() == View.GONE) {
                    layout4.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                } else {
                    layout4.setVisibility(View.GONE);
                }
                break;
            case R.id.button5:
                if (layout5.getVisibility() == View.GONE) {
                    layout5.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                } else {
                    layout5.setVisibility(View.GONE);
                }
                break;
            case R.id.boton_firma:
                View lay = LayoutInflater.from(this).inflate(R.layout.view_signature, null, false);
                final SignaturePad signature = (SignaturePad) lay.findViewById(R.id.signature_pad);

                AlertDialog.Builder b = new AlertDialog.Builder(this);
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
                break;
            default:
                break;
        }
    }

    public void shutdown(View v) {
        if (Principal.p != null)
            Principal.p.finish();
        if (VistaTopologica.topo != null)
            VistaTopologica.topo.finish();
        finish();
    }

    /**
     * Boton Guardar Informacion *
     */
    public void guardarInformacion(View view) {
        Intent n = new Intent(this, ActividadPDF.class);
        startActivity(n);

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
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
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
            b = Bitmap.createScaledBitmap(b, b.getWidth() * 2, b.getHeight() * 2, true);
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


}
