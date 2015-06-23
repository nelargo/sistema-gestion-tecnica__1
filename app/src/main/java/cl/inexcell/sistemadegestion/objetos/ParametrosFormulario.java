package cl.inexcell.sistemadegestion.objetos;

import java.util.ArrayList;

public class ParametrosFormulario {
    String atributo;
    String value;
    String typeInput;
    String typeDataInput;
    Boolean enabled;
    Boolean required;
    ArrayList<Deco> decos;

    public ParametrosFormulario(){}

    public ArrayList<Deco> getDecos() {
        return decos;
    }

    public void setDecos(ArrayList<Deco> decos) {
        this.decos = decos;
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTypeInput() {
        return typeInput;
    }

    public void setTypeInput(String typeInput) {
        this.typeInput = typeInput;
    }

    public String getTypeDataInput() {
        return typeDataInput;
    }

    public void setTypeDataInput(String typeDataInput) {
        this.typeDataInput = typeDataInput;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        if(enabled.compareTo("true")==0)
            this.enabled = true;
        else if(enabled.compareTo("false")==0)
            this.enabled = false;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(String required) {
        if(required.compareTo("true")==0)
            this.required = true;
        else if(required.compareTo("false")==0)
            this.required = false;
    }
}
