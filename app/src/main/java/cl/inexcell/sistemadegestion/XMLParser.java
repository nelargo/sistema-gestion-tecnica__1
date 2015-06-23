package cl.inexcell.sistemadegestion;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import cl.inexcell.sistemadegestion.objetos.Deco;
import cl.inexcell.sistemadegestion.objetos.ElementFormulario;
import cl.inexcell.sistemadegestion.objetos.Formulario;
import cl.inexcell.sistemadegestion.objetos.MapMarker;
import cl.inexcell.sistemadegestion.objetos.ParametrosFormulario;

public class XMLParser {
	
	/*
	 * Parser Return Code
	 */
	
	public static ArrayList<String> getReturnCode(String xml) throws ParserConfigurationException, 
	SAXException, IOException, XPathExpressionException
	{
		ArrayList<String> models = new ArrayList<String>();
        		
        String xmlRecords = xml;
        
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));
        
        Document doc = db.parse(is);
        NodeList nodes = doc.getElementsByTagName("Return");

    	for (int i = 0; i < nodes.getLength(); i++)
        {
                Element element = (Element) nodes.item(i);
                NodeList name = element.getChildNodes();
                
                for(int j=0; j< name.getLength(); j++){
                	Element line1 = (Element) name.item(j);
                	models.add(getCharacterDataFromElement(line1));
                }
        }

        return models;
	}

    public static String getOperationId(String xml) throws ParserConfigurationException,
	SAXException, IOException, XPathExpressionException
	{

        String xmlRecords = xml;

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        Element operationId = (Element)doc.getElementsByTagName("OperationId").item(0);

        return getCharacterDataFromElement(operationId);
	}

	/*
	 * XML-008: Informar 3G
	 */
	
	public static ArrayList<String> setNotificacion3G(String xml) throws ParserConfigurationException, 
	SAXException, IOException
	{
		ArrayList<String> res = new ArrayList<String>();
		
		String xmlRecords = xml;
		
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlRecords));
		
		Document doc = db.parse(is);
		NodeList nodes = doc.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes().item(1)
						.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes(); //Llegamos al tag Return
		
		
		for(int i = 0; i< nodes.getLength(); i++){
			NodeList elemento = nodes.item(i).getChildNodes();
			res.add(elemento.item(0).getNodeValue());
		}
			
		return res;
	}
	
	/*
	 * XML-009: Localizacion de Averia
	 */
	
	public static ArrayList<String> setLocation(String xml) throws ParserConfigurationException, 
	SAXException, IOException
	{
		ArrayList<String> res = new ArrayList<String>();
		
		String xmlRecords = xml;
		
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlRecords));
		
		Document doc = db.parse(is);
		NodeList nodes = doc.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes().item(1)
						.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes().item(0)
						.getChildNodes(); //Llegamos al tag Return
		
		
		for(int i = 0; i< nodes.getLength(); i++){
			NodeList elemento = nodes.item(i).getChildNodes();
			res.add(elemento.item(0).getNodeValue());
		}
			
		return res;
	}
	
	/*
	 * XML-010: Nodos de planta externa vecinos
	 */
	
	public static ArrayList<String> getNeighborNode(String xml) throws ParserConfigurationException, 
														SAXException, IOException
	{
		ArrayList<String> res = new ArrayList<String>();
		
		String xmlRecords = xml;

	    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    InputSource is = new InputSource();
	    is.setCharacterStream(new StringReader(xmlRecords));

	    Document doc = db.parse(is);
	    NodeList nodes = doc.getChildNodes().item(0)
				.getChildNodes().item(0)
				.getChildNodes().item(0)
				.getChildNodes().item(0)
				.getChildNodes().item(1)
				.getChildNodes().item(0)
				.getChildNodes().item(0)
				.getChildNodes();
	    
	   int tamano = nodes.getLength();
	   NodeList Return = nodes.item(tamano-1).getChildNodes();
	   String code = Return.item(0).getChildNodes().item(0).getNodeValue();
	   String linea = "";
	   /** GUARDAMOS EL CODE Y DESCRIPTION DEL RESULT **/
		for (int i = 0; i < Return.getLength(); i++) 
	    {
			if(i>0)
				linea+=";";
	    	linea+=Return.item(i).getChildNodes().item(0).getNodeValue();	
	    	
	    }
		res.add(linea);
		linea = "";
	    
		/** SI CODIGO ES "0" GUARDAMOS EL RESTO DE NODOS.*/
	    if(Integer.valueOf(code)==0 || Integer.valueOf(code)==48){
		    for (int i = 0; i < tamano-1; i++) 
		    {
		    	NodeList planta = nodes.item(i).getChildNodes();
		    	int n_datos = planta.getLength();
		    	for(int j = 0; j < n_datos; j++){
		    		NodeList dato = planta.item(j).getChildNodes();
		    		int not_empty = dato.getLength();
		    		if(j>0)
						linea+=";";
		    		
		    		if(not_empty == 0){
		    			linea+= "--";
		    		}
		    		else{		    			
			    		if(j==2){
			    			linea+=dato.item(0).getChildNodes().item(0).getNodeValue();
			    			linea+=";";
			    			linea+=dato.item(1).getChildNodes().item(0).getNodeValue();
			    		}
			    		else if(j==6){
			    			if(dato.item(0).getChildNodes().getLength() == 0){
				    			linea+= "--;--;--";
				    		}
			    			else{
				    			linea+=dato.item(0).getChildNodes().item(0).getNodeValue();
				    			linea+=";";
				    			linea+=dato.item(1).getChildNodes().item(0).getNodeValue();
				    			linea+=";";
				    			linea+=dato.item(2).getChildNodes().item(0).getNodeValue();
			    			}
			    		}
			    		else
			    			linea+=dato.item(0).getNodeValue();
		    		}
		    	}
		    	res.add(linea);
		    	linea = "";
		    }
	    }
	    
		return res;
	}
	
	public static ArrayList<String> getCertification(String xml) throws ParserConfigurationException, 
	SAXException, IOException
	{
		ArrayList<String> res = new ArrayList<String>();
		
		String xmlRecords = xml;
		
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlRecords));
		
		Document doc = db.parse(is);
		
		NodeList nodes = doc.getElementsByTagName("CertifyParameter");
		int tamano = nodes.getLength();
		Node Return = doc.getElementsByTagName("Return").item(0);
		String code = "";
		String linea = "";
		/** GUARDAMOS EL CODE Y DESCRIPTION DEL RESULT **/
		for (int i = 0; i < Return.getChildNodes().getLength(); i++) 
		{
			Element tmp = (Element)Return.getChildNodes().item(i);
			if(i>0)
				linea+=";";
			if(i==0)
				code =getCharacterDataFromElement(tmp);
			linea+=getCharacterDataFromElement(tmp);	
		
		}
		res.add(linea);
		linea = "";
		
		/** SI CODIGO ES "0" GUARDAMOS EL RESTO DE NODOS.*/
		if(code.equals("0")){
			for (int i = 0; i < tamano; i++) 
			{
				NodeList planta = nodes.item(i).getChildNodes();
				int n_datos = planta.getLength();
				for(int j = 0; j < n_datos; j++){
					NodeList dato = planta.item(j).getChildNodes();
					int not_empty = dato.getLength();
					if(j>0)
						linea+=";";
					
					if(not_empty == 0){
						linea+= "--";
					}
					else{		    			
						linea+=dato.item(0).getNodeValue();
					}
			}
			res.add(linea);
			linea = "";
		}
	}

	return res;
}

	
	public static ArrayList<ArrayList<String>> getDamage(String xml) throws ParserConfigurationException,
	SAXException, IOException, XPathExpressionException
	{
        ArrayList<ArrayList<String>> response = new ArrayList<>();
		ArrayList<String> models;
        
        String xmlRecords = xml;
        
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));
        
        Document doc = db.parse(is);
        NodeList nodeListType = doc.getElementsByTagName("TypeDamage");
        NodeList nodeListClasification = doc.getElementsByTagName("Classification");
        NodeList nodeListElement = doc.getElementsByTagName("Element");
        NodeList nodeListAfectation = doc.getElementsByTagName("Affectation");

        if(nodeListClasification != null && nodeListClasification.getLength() > 0){
            models = new ArrayList<>();
            models.add("CLASSIFICATION");
            for(int i=0; i<nodeListClasification.getLength(); i++){
                Element node = (Element) nodeListClasification.item(i);
                String dato = getCharacterDataFromElement(node);
                models.add(dato);
            }
            response.add(models);
        }
        if(nodeListAfectation != null && nodeListAfectation.getLength() > 0){
            models = new ArrayList<>();
            models.add("AFFECTATION");
            for(int i=0; i<nodeListAfectation.getLength(); i++){
                Element node = (Element) nodeListAfectation.item(i);
                String dato = getCharacterDataFromElement(node);
                models.add(dato);
            }
            response.add(models);
        }
        if(nodeListElement != null && nodeListElement.getLength() > 0){
            models = new ArrayList<>();
            models.add("ELEMENT");
            for(int i=0; i<nodeListElement.getLength(); i++){
                Element node = (Element) nodeListElement.item(i);
                String dato = getCharacterDataFromElement(node);
                models.add(dato);
            }
            response.add(models);
        }
        if(nodeListType != null && nodeListType.getLength() > 0){
            models = new ArrayList<>();
            models.add("TYPE");
            for(int i=0; i<nodeListType.getLength(); i++){
                Element node = (Element) nodeListType.item(i);
                String dato = getCharacterDataFromElement(node);
                models.add(dato);
            }
            response.add(models);
        }

        return response;
        //return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
	}
	




    public static ArrayList<Bundle> getResourcesNew(String xml) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException
    {
        ArrayList<Bundle> models = new ArrayList<Bundle>();
        ArrayList<String> arreglo;
        Bundle elemento ;
        String xmlRecords = xml;

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        NodeList nodes = doc.getElementsByTagName("Element");
        int n = nodes.getLength();

        for (int i = 0; i < n; i++) {
            Element element = (Element) nodes.item(i);

            Element id 			    = (Element) element.getElementsByTagName("Id").item(0);
            Element type 		    = (Element)element.getElementsByTagName("Type").item(0);
            Element value		    = (Element)element.getElementsByTagName("Value").item(0);
            NodeList identifications  = element.getElementsByTagName("Identification");
            NodeList subelement     = element.getElementsByTagName("SubElement");

            elemento 			= new Bundle();
            String elementId = getCharacterDataFromElement(id);
            elemento.putString("ID", getCharacterDataFromElement(id));
            elemento.putString("TYPE", getCharacterDataFromElement(type));
            elemento.putString("VALUE", getCharacterDataFromElement(value));

            if(identifications != null && identifications.getLength() > 0 && !elementId.equals("-1")){
                ArrayList<ArrayList<String>> idents;
                arreglo = new ArrayList<String>();
                for(int k = 0; k < identifications.getLength(); k++) {
                    NodeList parameters = identifications.item(k).getChildNodes();

                    String deco="";
                    for (int j = 0; j < parameters.getLength(); j++) {
                        Node param = parameters.item(j);
                        Element attr = (Element) param.getChildNodes().item(0);
                        Element val = (Element) param.getChildNodes().item(1);
                        if(j > 0) deco+=";";
                        deco += getCharacterDataFromElement(attr) + "&" + getCharacterDataFromElement(val);

                    }
                    arreglo.add(deco);

                }
                elemento.putStringArrayList("IDENTIFICATION", arreglo);
            }


            if(subelement != null && subelement.getLength() > 0) {
                arreglo = new ArrayList<String>();

                for (int k = 0; k < subelement.getLength(); k++) {

                    String dats = "";
                    NodeList parameters = ((Element) subelement.item(k)).getElementsByTagName("Parameters");

                    for (int j = 0; j < parameters.getLength(); j++) {

                        if(j == 3)
                            continue;
                        Node param = parameters.item(j);
                        Element attr = (Element) param.getChildNodes().item(0);
                        Element val = (Element) param.getChildNodes().item(1);

                        String par = getCharacterDataFromElement(attr)+"/"+getCharacterDataFromElement(val);

                        if(j != 0)
                            dats+= ";"+par;
                        else
                            dats+=par;


                    }
                    arreglo.add(dats);
                }


                elemento.putStringArrayList("SUBELEMENT", arreglo);
            }
            models.add(elemento);
        }
        return models;
    }


    public static ArrayList<String> getCertificationPar(String xml) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException
    {
        ArrayList<String> models = new ArrayList<String>();

        String xmlRecords = xml;
        Log.w("PARSER", xmlRecords);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        NodeList nodes = doc.getElementsByTagName("Parameters").item(0).getChildNodes();
        Log.w("PARSER", nodes.getLength()+" Parametros");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element ele = (Element)nodes.item(i);

            NodeList parameters = nodes.item(i).getChildNodes();
            NodeList names = ((Element) nodes.item(i)).getElementsByTagName("Name");
            NodeList values = ((Element) nodes.item(i)).getElementsByTagName("Value");
            String nombreGrupo = ele.getTagName();
            for (int j = 0; j < parameters.getLength()/2; j++) {
                Element nombre = (Element) names.item(j);
                Element valor = (Element) values.item(j);

                String N = getCharacterDataFromElement(nombre);
                String V = getCharacterDataFromElement(valor);
                Log.w("PARSER",N+": "+V);
                if(V.equals(""))
                    V = "--";
                nombreGrupo += "&"+N+";"+V;
            }
            models.add(nombreGrupo);
        }


        return models;
        //return cpe.elementAt(1).toString(); // Mostrar elemento 1 del Vector
    }

    public static ArrayList<MapMarker> getMapMarkers(String xml) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException
    {
        ArrayList<MapMarker> marks = new ArrayList<MapMarker>();
        MapMarker mapMarker;
        String xmlRecords = xml;

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlRecords));

        Document doc = db.parse(is);
        NodeList markers = doc.getElementsByTagName("Markers").item(0).getChildNodes();

            for (int i = 0; i<markers.getLength();i++) {
                NodeList marker = markers.item(i).getChildNodes();
                NodeList GPS = marker.item(0).getChildNodes();

                String Lat = getCharacterDataFromElement((Element)GPS.item(0));
                String Lng = getCharacterDataFromElement((Element) GPS.item(1));

                String Name = getCharacterDataFromElement((Element)marker.item(1));
                String Desc = getCharacterDataFromElement((Element)marker.item(2));

                Log.w("PARSER", Lat+";"+Lng+";"+Name+";"+Desc);
                mapMarker = new MapMarker(Double.parseDouble(Lat),Double.parseDouble(Lng),Name,Desc);

                marks.add(mapMarker);
            }


        return marks;
    }

    public static Formulario getForm(String xml) throws ParserConfigurationException,
            SAXException, IOException, XPathExpressionException
    {
        Formulario formulario = new Formulario();
        ArrayList<ElementFormulario> Elementos;
        ArrayList<ParametrosFormulario> Parametros;
        ArrayList<Deco> Decos;

        ElementFormulario elemento;
        ParametrosFormulario parametro;

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));

        Document doc = db.parse(is);
        NodeList Return = doc.getElementsByTagName("Return").item(0).getChildNodes();
        NodeList elementNode = doc.getElementsByTagName("Element");

        /*Operation Section*/
        formulario.setOperationCode(getCharacterDataFromElement((Element) doc.getElementsByTagName("OperationCode").item(0)));
        formulario.setOperationId(getCharacterDataFromElement((Element) doc.getElementsByTagName("OperationId").item(0)));
        formulario.setDateTime(getCharacterDataFromElement((Element) doc.getElementsByTagName("DateTime").item(0)));
        formulario.setIdUser(getCharacterDataFromElement((Element) doc.getElementsByTagName("IdUser").item(0)));
        formulario.setIMEI(getCharacterDataFromElement((Element) doc.getElementsByTagName("IMEI").item(0)));
        formulario.setIMSI(getCharacterDataFromElement((Element) doc.getElementsByTagName("IMSI").item(0)));
        formulario.setTelefono(getCharacterDataFromElement((Element) doc.getElementsByTagName("Telefono").item(0)));
        formulario.setTelevision(getCharacterDataFromElement((Element) doc.getElementsByTagName("Television").item(0)));
        formulario.setBandaAncha(getCharacterDataFromElement((Element) doc.getElementsByTagName("BandaAncha").item(0)));
        formulario.setNombreTecnico(getCharacterDataFromElement((Element) doc.getElementsByTagName("NombreTecnico").item(0)));

        /*Return Section */
        formulario.setReturnCode(Return.item(0).getChildNodes().item(0).getNodeValue());
        formulario.setReturnDescription(Return.item(1).getChildNodes().item(0).getNodeValue());

        if(formulario.getReturnCode() == 0){
            Elementos = new ArrayList<>();
            for(int i = 0; i < elementNode.getLength(); i ++){
                elemento = new ElementFormulario();
                Node elementId = elementNode.item(i).getChildNodes().item(0);
                Node elementType = elementNode.item(i).getChildNodes().item(1);
                Node elementValue = elementNode.item(i).getChildNodes().item(2);
                elemento.setId(elementId.getFirstChild().getTextContent());
                elemento.setType(elementType.getFirstChild().getTextContent());
                elemento.setValue(elementValue.getFirstChild().getTextContent());

                NodeList params = elementNode.item(i).getChildNodes().item(3).getChildNodes();
                Parametros = new ArrayList<>();

                for(int j = 0; j < params.getLength(); j++){
                    parametro = new ParametrosFormulario();
                    Node Atributo = ((Element)params.item(j)).getElementsByTagName("Attribute").item(0);
                    Node Valor = ((Element)params.item(j)).getElementsByTagName("Value").item(0);
                    Node TipoEntrada = ((Element)params.item(j)).getElementsByTagName("typeInput").item(0);
                    Node TipoData = ((Element)params.item(j)).getElementsByTagName("typeDataInput").item(0);
                    Node Habilitado = ((Element)params.item(j)).getElementsByTagName("Enabled").item(0);
                    Node Requerido = ((Element)params.item(j)).getElementsByTagName("Required").item(0);



                    parametro.setAtributo(Atributo.getFirstChild().getTextContent());
                    parametro.setTypeInput(TipoEntrada.getFirstChild().getTextContent());
                    parametro.setTypeDataInput(TipoData.getFirstChild().getTextContent());
                    parametro.setEnabled(Habilitado.getFirstChild().getTextContent());
                    parametro.setRequired(Requerido.getFirstChild().getTextContent());

                    if(elementType.getFirstChild().getTextContent().compareTo("DigitalTelevision")==0 &&
                            Atributo.getFirstChild().getTextContent().compareTo("DecosSerie")==0){

                        NodeList SeriesDeco = ((Element)params.item(j)).getElementsByTagName("SeriesDecos");
                        Decos = new ArrayList<>();
                        for(int k = 0; k<SeriesDeco.getLength(); k++){
                            Deco d = new Deco();
                            d.setLabel(SeriesDeco.item(k).getChildNodes().item(0).getFirstChild().getTextContent());
                            d.setSerieDeco(SeriesDeco.item(k).getChildNodes().item(1).getFirstChild().getTextContent());
                            d.setSerieTarjeta(SeriesDeco.item(k).getChildNodes().item(2).getFirstChild().getTextContent());
                            Decos.add(d);

                        }
                        parametro.setDecos(Decos);
                        parametro.setValue("");
                        Parametros.add(parametro);
                    }else{
                        parametro.setDecos(null);

                        if(Atributo.getFirstChild().getTextContent().compareTo("PassportPhoto")==0)
                            parametro.setValue("");
                        else
                            parametro.setValue(Valor.getFirstChild().getTextContent());
                        Parametros.add(parametro);
                    }

                }
                elemento.setParameters(Parametros);
                Elementos.add(elemento);
            }
            formulario.setElements(Elementos);

        }

        return formulario;
    }
/*
	 * Generico para todas las consultas
	 */

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if(e == null) return "";
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

}
