package cl.inexcell.sistemadegestion.objetos;

import java.util.ArrayList;

/**
 * Created by Felipes on 17-06-2015.
 */
public class Formulario {
    String operationCode;
    String operationId;
    String dateTime;
    String idUser;
    String IMEI;
    String IMSI;
    String telefono;
    String television;
    String bandaAncha;
    String nombreTecnico;
    ArrayList<ElementFormulario> elements;
    int returnCode;
    String returnDescription;

    public Formulario(){};


    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = Integer.valueOf(returnCode);
    }

    public String getReturnDescription() {
        return returnDescription;
    }

    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }


    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTelevision() {
        return television;
    }

    public void setTelevision(String television) {
        this.television = television;
    }

    public String getBandaAncha() {
        return bandaAncha;
    }

    public void setBandaAncha(String bandaAncha) {
        this.bandaAncha = bandaAncha;
    }

    public String getNombreTecnico() {
        return nombreTecnico;
    }

    public void setNombreTecnico(String nombreTecnico) {
        this.nombreTecnico = nombreTecnico;
    }

    public ArrayList<ElementFormulario> getElements() {
        return elements;
    }

    public void setElements(ArrayList<ElementFormulario> elements) {
        this.elements = elements;
    }
}
