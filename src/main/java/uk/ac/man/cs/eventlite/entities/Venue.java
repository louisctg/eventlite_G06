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

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Entity
@Table(name = "Venue")
public class Venue {

	public static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoibWFyY2VsNDF4ZCIsImEiOiJjanRmcGE2bjEwZzFxM3lycm90bXB6cndnIn0.sYaAHPeiuJ5ivfyBE8sCAQ";
	
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
	
	private double lat;
	private double lng;

	public Venue() {
	}
	
	public Venue(String requiredName, int requiredCapacity, String requiredAddress, 
		      String requiredPostcode) {
		name = requiredName;
		capacity = requiredCapacity;
		address = requiredAddress;
		postcode = requiredPostcode;
		setCoordinates(address, postcode);
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
	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public void setCoordinates(String address, String postcode)
	{
		MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
										.accessToken(MAPBOX_ACCESS_TOKEN)
										.query(address + ", " + postcode)
										.build();

		mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
			@Override
			public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
				List<CarmenFeature> results = response.body().features();
				if(results.size() > 0) {
					setLat(results.get(0).center().latitude());
					setLng(results.get(0).center().longitude());
				}
				else // set to central Manchester
				{
					setLat(-2.235);
					setLng(53.46);
				}
			}
			
			@Override
			public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
				// set to central Manchester
				setLat(-2.235);
				setLng(53.46);
			}
		});

	}
}
