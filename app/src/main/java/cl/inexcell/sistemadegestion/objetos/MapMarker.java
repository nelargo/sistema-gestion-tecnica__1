package cl.inexcell.sistemadegestion.objetos;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Felipe on 12/03/2015.
 */
public class MapMarker {
    private LatLng locate;
    private String Name;
    private String Description;

    public MapMarker(Double latitude, Double longitude, String name, String description) {
        super();
        locate = new LatLng(latitude,longitude);
        Name = name;
        Description = description;
    }

    public LatLng getLocate() {
        return locate;
    }

    public void setLocate(LatLng locate) {
        this.locate = locate;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
