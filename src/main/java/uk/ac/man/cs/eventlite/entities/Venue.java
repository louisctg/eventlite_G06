package uk.ac.man.cs.eventlite.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Venue")
public class Venue {

	@Id
	@GeneratedValue
	private long id;


	@NotBlank(message = "Please enter an actual name")
	@Size(max = 255, message = "The venue name must have <256 characters.")
	private String name;

	@PositiveIntegerConstraint
	private Integer capacity;
	
	// NOTE: SQL has limit of 255
	@NotBlank(message = "Please enter an address")
	@Size(max = 300, message = "The address must be 300 characters or less.")
	private String address;
	
	@PostCodeConstraint
	@NotBlank(message = "Please enter a postcode")
	private String postcode;

	@OneToMany(targetEntity = Event.class, mappedBy = "venue", cascade = CascadeType.MERGE, orphanRemoval = true)
	private List<Event> events = new ArrayList<>();

	public Venue() {
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

	public Integer getCapacity() {
		return this.capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
	public void addEvent(Event event) {
		this.events.add(event);
	}
		
	public String getAddress() {
		return this.address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getPostcode() {
		return this.postcode;
	}
	
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
}
