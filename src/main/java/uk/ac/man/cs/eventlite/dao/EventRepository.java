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
	
	Iterable<Event> findByNameContainingIgnoreCaseAndDateAfterOrderByNameAscDateAscTimeAsc(String name, Date date);
	
	Iterable<Event> findByNameContainingIgnoreCaseAndDateAfterOrderByNameAscDateDescTimeDesc(String name, Date date);
}
