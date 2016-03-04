package co.com.poncho.model;

public class Usuario {

	String nombre;
	int voto;
	int tipoVoto;
	boolean aceptado;

	public Usuario(String nombre) {
		super();
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getVoto() {
		return voto;
	}

	public void setVoto(int voto) {
		this.voto = voto;
	}

	public int getTipoVoto() {
		return tipoVoto;
	}

	public void setTipoVoto(int tipoVoto) {
		this.tipoVoto = tipoVoto;
	}

	public boolean isAceptado() {
		return aceptado;
	}

	public void setAceptado(boolean aceptado) {
		this.aceptado = aceptado;
	}

}
