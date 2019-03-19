package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public long count();

	public Iterable<Venue> findAll();
	
	public Iterable<Venue> findAllInAlphabeticalOrder();
	
	public Venue findOne(long id);
	
	public void save(Venue venue);
	
	public Iterable<Venue> findTop3VenuesWithMostEvents();
	
	public Iterable<Venue> searchVenuesOrderedByNameAscending(String name);
}
