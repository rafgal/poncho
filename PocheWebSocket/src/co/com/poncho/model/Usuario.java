package co.com.poncho.model;

import com.google.gson.Gson;

public class Usuario {

	String nombre;
	float voto=-1;
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

	public float getVoto() {
		return voto;
	}

	public void setVoto(float voto) {
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

	public String getEstado(){
		return new Gson().toJson(this);
	}
}
