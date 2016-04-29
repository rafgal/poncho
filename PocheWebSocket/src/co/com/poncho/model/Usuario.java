package co.com.poncho.model;

import javax.websocket.Session;

import com.google.gson.Gson;

public class Usuario {

	private String nombre;
	private float voto=-1;
	private int tipoVoto;
	private boolean aceptado;
	private Room room;
	private Session session;
	
	public Usuario(String nombre, Session session) {
		this.nombre = nombre;
		this.session = session;
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
	
	public Room getRoom() {
		return room;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
}
