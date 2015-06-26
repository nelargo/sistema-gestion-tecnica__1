package cl.inexcell.sistemadegestion.objetos;

import java.util.ArrayList;

public class FormularioEnvio {
    String Type;
    ArrayList<ParametrosEnvioForm> parametros;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public ArrayList<ParametrosEnvioForm> getParametros() {
        return parametros;
    }

    public void setParametros(ArrayList<ParametrosEnvioForm> parametros) {
        this.parametros = parametros;
    }
}
