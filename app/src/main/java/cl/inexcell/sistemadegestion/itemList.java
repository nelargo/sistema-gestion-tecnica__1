package cl.inexcell.sistemadegestion;

public class itemList {
	 private String title;
	 private String description;
	 private int mensaje = -1;
	 
	public itemList(String title, String description) {
	    super();
	    this.setTitle(title);
	    this.setDescription(description);
	}
	
	public itemList(String title, String description, int mensaje) {
	    super();
	    this.setTitle(title);
	    this.setDescription(description);
	    this.setMensaje(mensaje);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMensaje() {
		return mensaje;
	}

	public void setMensaje(int mensaje) {
		this.mensaje = mensaje;
	}
}
