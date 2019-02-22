package uk.ac.man.cs.eventlite.entities;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue
	private long id;
	
	@NotNull(message = "Please enter a date")
	@Future(message = "Only the future is valid")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	private Date date;

	@NotNull(message = "Please enter a date")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "HH:mm")
	@Temporal(TemporalType.TIME)
	private Date time;
	
	@NotEmpty(message = "The event cannot be empty.")
	@Size(max = 30, message = "The event must have 30 characters or less.")
	private String name;
	@ManyToOne(targetEntity = Venue.class)
	@NotNull(message = "Please select one")
	private Venue venue;

	public Event() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}
}
