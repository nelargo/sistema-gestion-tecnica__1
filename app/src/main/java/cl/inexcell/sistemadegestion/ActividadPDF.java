package cl.inexcell.sistemadegestion;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.joanzapata.pdfview.PDFView;

import java.io.File;

/**
 * Created by Felipes on 17-06-2015.
 */
public class ActividadPDF extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pdfview);

        PDFView pdfView = (PDFView)findViewById(R.id.pdfview);
        File archivo = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.pdf");
        if(archivo.exists()) {
            pdfView.fromFile(archivo)
                    .showMinimap(false)
                    .enableSwipe(true)
                    .load();
        }else{
            Toast.makeText(this,"El archivo pdf no existe.",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void shutdown(View v){
        if(Principal.p != null)
            Principal.p.finish();
        if(FactActivity_bak.fatc != null)
            FactActivity_bak.fatc.finish();
        if(VistaTopologica.topo!=null)
            VistaTopologica.topo.finish();
        finish();
    }

    /** Boton Volver **/
    public void volver(View view) {
        finish();

        // Vibrar al hacer click
        Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }
}
