package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();

	public Iterable<Event> findAll();
	
	public Event findOne(long id);
	
<<<<<<< Upstream, based on branch 'MVP_dev' of ssh://gitlab@gitlab.cs.man.ac.uk:22222/comp23412_2018/eventlite_G06.git
	public Event save(Event event);

	public void delete(long id);
=======
	public Iterable<Event> findAllAfterToday();
>>>>>>> df6f006 Allow updating only of the past events
}
