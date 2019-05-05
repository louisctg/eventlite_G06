package uk.ac.man.cs.eventlite.dao;

import java.util.Date;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventRepository extends CrudRepository<Event, Long>{

	/**
	 * Sorts events by date and then time, earliest first
	 * 
	 * @return the sorted list of events
	 */
	Iterable<Event> findAllByOrderByDateAscTimeAsc();
	
	Iterable<Event> findByDateAfterOrderByDateAscTimeAsc(Date date);
	
	// used for future events on events index page
	Iterable<Event> findByDateAfterOrderByDateAscNameAsc(Date date); // after today
	Iterable<Event> findByDateEqualsAndTimeGreaterThanEqualOrderByDateAscNameAsc(Date date, Date time); // today, after current time
	
	// used for previous events on events index page
	Iterable<Event> findByDateBeforeOrderByDateDescNameAsc(Date date); // before today
	Iterable<Event> findByDateEqualsAndTimeLessThanOrderByDateDescNameAsc(Date date, Date time); // today, before current time

	// used for future events on events search page
	Iterable<Event> findByNameContainingIgnoreCaseAndDateAfterOrderByDateAscNameAsc(String name, Date date); // after today
	Iterable<Event> findByNameContainingIgnoreCaseAndDateEqualsAndTimeGreaterThanEqualOrderByDateAscNameAsc(String name, Date date, Date time); // today, after current time
	
	// used for previous evnets on events search page
	Iterable<Event> findByNameContainingIgnoreCaseAndDateBeforeOrderByDateDescNameAsc(String name, Date date); // before today
	Iterable<Event> findByNameContainingIgnoreCaseAndDateEqualsAndTimeLessThanOrderByDateDescNameAsc(String name, Date date, Date time); // today, before current time
	
	// used for next 3 incoming events
	Iterable<Event> findTop3ByDateAfterOrderByDateAscNameAsc(Date date); // after today
	Iterable<Event> findTop3ByDateEqualsAndTimeAfterOrderByDateAscNameAsc(Date date, Date time); // today, after current time

	// used for upcoming events of the particular venue
	Iterable<Event> findByVenueId(long venueId);
}
