package co.com.poncho.model;

import java.util.HashSet;
import java.util.Set;

public class Room {

	private String name;
	private Set<Usuario> users;
	private Usuario owner;
	private Set<Usuario> usersWithVote=new HashSet<>();

	public Room(String name, Usuario owner) {
		users = new HashSet<Usuario>();
		setName(name);
		setOwner(owner);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Usuario> getUsers() {
		return users;
	}

	public void setUsers(Set<Usuario> users) {
		if(users != null)
			this.users = users;
	}

	public void addUser(Usuario user){
		if(user != null){
			this.users.add(user);
		}
	}

	public void removeUser(Usuario user){
		if(user != null)
			this.users.remove(user);
	}
	
	public Usuario getOwner() {
		return owner;
	}
	
	public void setOwner(Usuario owner) {
		this.owner = owner;
	}
	
	public Set<Usuario> getUsersWithVote() {
		return usersWithVote;
	}
	
	public void addVote(Usuario user){
		usersWithVote.add(user);
	}
	
	public void resetRoom(){
		for (Usuario usuario : users) {
			usuario.setAceptado(false);
			usuario.setVoto(-1);
		}
		usersWithVote.clear();
	}
	
	public int getUserAccpted(){
		int numAprobaciones = 0;
		for (Usuario user : users) {
			if (user.isAceptado()) {
				numAprobaciones++;
			}
		}
		return numAprobaciones;
	}
	
}
