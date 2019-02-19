package uk.ac.man.cs.eventlite.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Venue")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

	private String name;

	private int capacity;
	
	@OneToMany(targetEntity = Event.class,cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Event> events = new ArrayList<>();

	public Venue() {
	}
	
	public Venue(String requiredName, int requiredCapacity) {
		name = requiredName;
		capacity = requiredCapacity;
	}
	
	public Venue(int requiredId, String requiredName, int requiredCapacity) {
		id = requiredId;
		name = requiredName;
		capacity = requiredCapacity;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}
