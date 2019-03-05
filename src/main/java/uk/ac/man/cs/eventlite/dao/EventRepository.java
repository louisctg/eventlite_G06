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
	
	// used for previous events
	Iterable<Event> findByDateAfterOrderByDateAscTimeAscNameAsc(Date date);
	
	Iterable<Event> findByDateEqualsAndTimeGreaterThanEqualOrderByDateAscTimeAscNameAsc(Date date, Date time);
	// used for future events
	Iterable<Event> findByDateBeforeOrderByDateDescTimeDescNameAsc(Date date);
	
	Iterable<Event> findByDateEqualsAndTimeLessThanOrderByDateDescTimeDescNameAsc(Date date, Date time);

	Iterable<Event> findByNameContainingIgnoreCaseAndDateAfterOrderByDateAscTimeAscNameAsc(String name, Date date);
	Iterable<Event> findByNameContainingIgnoreCaseAndDateEqualsAndTimeGreaterThanEqualOrderByDateAscTimeAscNameAsc(String name, Date date, Date time);
	
	Iterable<Event> findByNameContainingIgnoreCaseAndDateBeforeOrderByDateDescTimeDescNameAsc(String name, Date date);
	Iterable<Event> findByNameContainingIgnoreCaseAndDateEqualsAndTimeLessThanOrderByDateDescTimeDescNameAsc(String name, Date date, Date time);
}
