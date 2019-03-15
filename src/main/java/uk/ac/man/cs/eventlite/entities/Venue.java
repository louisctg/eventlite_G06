package uk.ac.man.cs.eventlite.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;


@Entity
@Table(name = "Venue")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

	@Size(max = 255, message = "The venue name must have <256 characters.")
	private String name;

	@PositiveIntegerConstraint
	private Integer capacity;
	
	@Size(max = 299, message = "Road name must have <300 characters.")
	private String roadname;
	
	@PostCodeConstraint
	private String postcode;
	
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
	
	public Venue(int requiredId, String requiredName, int requiredCapacity, String requiredRoadname, String requiredPostcode) {
		id = requiredId;
		name = requiredName;
		capacity = requiredCapacity;
		roadname = requiredRoadname;
		postcode = requiredPostcode;
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
	
	public String getAddress() {
		return roadname+' '+postcode;
	}

	public void setAddress(String roadname) {
		this.roadname = roadname;
	}
	
	public void getAddress(String postcode) {
		this.postcode = postcode;
	}
}
