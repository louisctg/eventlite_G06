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
	Iterable<Event> findByDateAfterOrderByNameAscDateDescTimeDesc(Date date);
	
	Iterable<Event> findByDateEqualsAndTimeGreaterThanEqualOrderByDateAscTimeAscNameAsc(Date date, Date time);
	// used for future events
	Iterable<Event> findByDateBeforeOrderByNameAscDateAscTimeAsc(Date date);
	
	Iterable<Event> findByDateEqualsAndTimeLessThanOrderByDateDescTimeDescNameAsc(Date date, Date time);
}
