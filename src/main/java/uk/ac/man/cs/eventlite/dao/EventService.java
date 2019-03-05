package uk.ac.man.cs.eventlite.dao;

import java.util.Date;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public Event findOne(long id);
	
	public Event save(Event event);

	public void delete(long id);

	public Iterable<Event> findAllAfterToday();
	
	public Iterable<Event> findFutureEventsOrderedByNameAndDate();
	
	public Iterable<Event> findPastEventsOrderedByNameAndDate();

	public Iterable<Event> searchFutureEventsOrderedByNameAndDateAscending(String name);
	
	public Iterable<Event> searchPastEventsOrderedByNameAndDateDescending(String name);
}
