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
	
	public Iterable<Event> findNext3UpcomingEvents();

	public Iterable<Event> findUpcomingEventsWithVenue(long id);
	
	public Iterable<Event> findNext3UpcomingEventsWithVenue(long id);
	
	public Iterable<Event> futureEventsOrganiser(Iterable<Event> events, String organiserName);
	public Iterable<Event> pastEventsOrganiser(Iterable<Event> events, String organiserName);
}
