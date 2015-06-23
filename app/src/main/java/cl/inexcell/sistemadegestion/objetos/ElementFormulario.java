package cl.inexcell.sistemadegestion.objetos;

import java.util.ArrayList;

import cl.inexcell.sistemadegestion.R;

/**
 * Created by Felipes on 17-06-2015.
 */
public class ElementFormulario {
    int id;
    String type;
    String value;
    ArrayList<ParametrosFormulario> parameters;
    int drawable;

    public ElementFormulario(){}

    private int getDrawable(String type){
        switch (type){
            case "Broadband":
                return R.drawable.vt_baf1;
            case "DigitalTelevision":
                return R.drawable.vt_dth2;
            case "Telephony":
                return R.drawable.vt_stb1;
            case "remove":
                return R.drawable.vt_planta1;
            case "ClosingData":
                return R.drawable.cc_direct1;
            default:
                return -1;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.valueOf(id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.drawable = getDrawable(type);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<ParametrosFormulario> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<ParametrosFormulario> parameters) {
        this.parameters = parameters;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
