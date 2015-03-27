package cl.inexcell.sistemadegestion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import cl.inexcell.sistemadegestion.daemon.MyLocationListener;
import cl.inexcell.sistemadegestion.objetos.MapMarker;

public class Plantas_Externas extends FragmentActivity implements GoogleMap.OnMapClickListener,GoogleMap.OnMapLoadedCallback{
    private GoogleMap mapa;

	private final String TAG = "Plantas_Externas";
    MyLocationListener gps;
    List<Address> matches;
    Geocoder geoCoder;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Activity sin parte superior
		requestWindowFeature(Window.FEATURE_NO_TITLE);				
		setContentView(R.layout.activity_plantas_externas);

        gps = new MyLocationListener(this);
        geoCoder = new Geocoder(this);

        mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment)).getMap();
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.setMyLocationEnabled(true);
        //mapa.setOnMapClickListener(this);
        mapa.getUiSettings().setMyLocationButtonEnabled(true);
        mapa.getUiSettings().setCompassEnabled(true);
        mapa.getUiSettings().setZoomControlsEnabled(true);

        mapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.map_marker_info_view, null);
                TextView Titulo = (TextView)v.findViewById(R.id.infoWindow_Titulo);
                TextView Description = (TextView)v.findViewById(R.id.infoWindow_descripcion);

                Titulo.setText(marker.getTitle());
                Description.setText(marker.getSnippet());
                return v;
            }
        });
        mapa.setOnMapLoadedCallback(this);
        if(mapa != null){
            if(mapa.getMyLocation() != null) {
                mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mapa.getMyLocation().getLatitude(), mapa.getMyLocation().getLongitude()), 15));
            }
            else{
                try {
                    mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(), gps.getLongitude()), 15));
                }catch(Exception e){Log.e(TAG,e.getMessage());}
            }
        }
    }
	
	public void shutdown(View v){
		Principal.p.finish();
		finish();
	}

    public void volver(View view){
        this.finish();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    public void buscar(View v){
        mapa.clear();
        if(mapa.getMyLocation() != null) {
            try {
                matches = geoCoder.getFromLocation(mapa.getMyLocation().getLatitude(), mapa.getMyLocation().getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                matches = geoCoder.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(matches != null){
            Search_Points bm = new Search_Points(matches,this);
            bm.execute();
        }
        else
            Toast.makeText(this,"No se puede acceder a su ubicación, intente más tarde",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLoaded() {

    }

    private class Search_Points extends AsyncTask<String, Integer, ArrayList<MapMarker>> {

        private Context mContext;
        private ProgressDialog dialog;
        Boolean isCancel = false;
        String comuna;
        String region;

        public Search_Points(List<Address> locate, Context mContext) {
            super();
            this.comuna = locate.get(0).getAddressLine(1);
            this.region = locate.get(0).getAddressLine(2);
            this.mContext = mContext;
        }

        @Override
        protected ArrayList<MapMarker> doInBackground(String... params) {
            ArrayList<MapMarker> respuesta;

            try{
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = telephonyManager.getDeviceId();
                String IMSI =  telephonyManager.getSimSerialNumber();

                String output = SoapRequestMovistar.getMapMarkers(IMEI,IMSI,comuna,region);
                //String output = dummy();
                if(output != null)
                    respuesta = XMLParser.getMapMarkers(output);
                else
                    respuesta = null;

            }catch (Exception e){
                e.printStackTrace();
                respuesta = null;
            }
            return respuesta;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            this.dialog.setMessage("Buscando en lugares cercanos...");
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(getApplicationContext(), "Operación Interrumpida.", Toast.LENGTH_SHORT).show();
                    isCancel = true;
                }
            });
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<MapMarker> puntos) {
            super.onPostExecute(puntos);

            if(isCancel)return;

            if(puntos != null) {
                for (MapMarker point : puntos) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(point.getLocate())
                            .title(point.getName())
                            .snippet(point.getDescription());
                    mapa.addMarker(markerOptions);
                }
            }

            if(dialog.isShowing())
                dialog.dismiss();

        }
    }

}
