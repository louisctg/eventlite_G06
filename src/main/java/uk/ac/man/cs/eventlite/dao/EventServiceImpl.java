package uk.ac.man.cs.eventlite.dao;

import java.util.Iterator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.man.cs.eventlite.entities.Event;

@Service
public class EventServiceImpl implements EventService {

	private final static Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

	private final static String DATA = "data/events.json";

	@Autowired
	private EventRepository eventRepository;
	
	@Override
	public long count() {
		return eventRepository.count();
	}

	@Override
	public Iterable<Event> findAll() {
		return eventRepository.findAllByOrderByDateAscTimeAsc();
	}
	
	@Override
	public Iterable<Event> findAllAfterToday() {
		// give as argument the current time given by default Date constructor
		return eventRepository.findByDateAfterOrderByDateAscTimeAsc(new Date());
	}

	@Override
	public Event save(Event event) {
		return eventRepository.save(event);	
	}
	
	@Override
	public Event findOne(long id) {
		return eventRepository.findOne(id);
	}
	
	@Override
	public void delete(long id) {
		eventRepository.delete(id);
	}

	@Override
	public Iterable<Event> findFutureEventsOrderedByNameAndDate() {
		Iterable<Event> eventsAfterToday =   eventRepository.findByDateAfterOrderByDateAscNameAsc(new Date());
		Iterable<Event> eventsToday =   eventRepository.findByDateEqualsAndTimeGreaterThanEqualOrderByDateAscNameAsc(new Date(), new Date());
		
		List<Event> futureEvents = new ArrayList<Event>();
		for(Event event: eventsToday) {
			futureEvents.add(event);
		}
		for(Event event: eventsAfterToday) {
			futureEvents.add(event);
		}
		
		return futureEvents;
	}

	@Override
	public Iterable<Event> findPastEventsOrderedByNameAndDate() {
		Iterable<Event> eventsBeforeToday =   eventRepository.findByDateBeforeOrderByDateDescNameAsc(new Date());
		Iterable<Event> eventsToday =   eventRepository.findByDateEqualsAndTimeLessThanOrderByDateDescNameAsc(new Date(), new Date());
		
		List<Event> pastEvents = new ArrayList<Event>();
		for(Event event: eventsToday) {
			pastEvents.add(event);
		}
		for(Event event: eventsBeforeToday) {
			pastEvents.add(event);
		}
		
		return pastEvents;
	}
	
	@Override
	public Iterable<Event> searchFutureEventsOrderedByNameAndDateAscending(String name) {
		Iterable<Event> eventsAfterToday =  eventRepository.findByNameContainingIgnoreCaseAndDateAfterOrderByDateAscNameAsc(name, new Date());
		Iterable<Event> todayEvents = eventRepository.findByNameContainingIgnoreCaseAndDateEqualsAndTimeGreaterThanEqualOrderByDateAscNameAsc(name, new Date(), new Date());
		
		List<Event> futureEvents = new ArrayList<Event>();
		
		for(Event event: todayEvents) {
			futureEvents.add(event);
		}
		
		for(Event event: eventsAfterToday) {
			futureEvents.add(event);
		}
		
		return futureEvents;
	}

	@Override
	public Iterable<Event> searchPastEventsOrderedByNameAndDateDescending(String name) {
		Iterable<Event> eventsBeforeToday =  eventRepository.findByNameContainingIgnoreCaseAndDateBeforeOrderByDateDescNameAsc(name, new Date());
		Iterable<Event> todayEvents = eventRepository.findByNameContainingIgnoreCaseAndDateEqualsAndTimeLessThanOrderByDateDescNameAsc(name, new Date(), new Date());
		
		List<Event> pastEvents = new ArrayList<Event>();
		
		for(Event event: todayEvents) {
			pastEvents.add(event);
		}
		
		for(Event event: eventsBeforeToday) {
			pastEvents.add(event);
		}
		
		return pastEvents;	
	}

	@Override
	public Iterable<Event> findNext3UpcomingEvents() {
		Iterable<Event> top3Today = eventRepository.findTop3ByDateEqualsAndTimeAfterOrderByDateAscNameAsc(new Date(), new Date());
		Iterable<Event> top3AfterToday = eventRepository.findTop3ByDateAfterOrderByDateAscNameAsc(new Date());
		
		List<Event> incomingEvents = new ArrayList<Event>();
		
		for(Event event: top3Today) {
			if(incomingEvents.size()  < 3)
				incomingEvents.add(event);
		}
		
		for(Event event: top3AfterToday) {
			if(incomingEvents.size()  < 3)
				incomingEvents.add(event);
		}
		
		return incomingEvents;
	}
	
	public Iterable<Event> findNext3UpcomingEventsWithVenue(long id){
		Iterable<Event> incomingEvents = eventRepository.findByVenueId(id);
		List<Event> next3Events = new ArrayList<Event>();
		
		for(Event event: incomingEvents) {
			if(next3Events.size()  < 3)
				next3Events.add(event);
		}
		
		return next3Events;
	}

  @Override
  public Iterable<Event> findUpcomingEventsWithVenue(long id) {
	  Iterable<Event> eventsAfterToday = eventRepository.findByDateAfterOrderByDateAscNameAsc(new Date());
		Iterable<Event> eventsToday = eventRepository.findByDateEqualsAndTimeGreaterThanEqualOrderByDateAscNameAsc(new Date(), new Date());
		
		List<Event> futureEvents = new ArrayList<Event>();
		for(Event event: eventsToday) {
			if(event.getVenue().getId() == id)
				futureEvents.add(event);
		}
		for(Event event: eventsAfterToday) {
			if(event.getVenue().getId() == id)
				futureEvents.add(event);
		}
		
		return futureEvents;
  }
  @Override
  public Iterable<Event> futureEventsOrganiser(Iterable<Event> events, String organiserName)
  {
	List<Event> incomingEvents = new ArrayList<Event>();
	for(Event event: events) {
		// System.out.println("event:"+ event);
		if(event.getOrganiser() != null)
		if(event.getOrganiser().equals(organiserName))
			incomingEvents.add(event);
	}
	return incomingEvents;  
  }
  @Override
  public Iterable<Event> pastEventsOrganiser(Iterable<Event> events, String organiserName)
  {
		List<Event> incomingEvents = new ArrayList<Event>();
		for(Event event: events) {
			if(event.getOrganiser() != null)
			if(event.getOrganiser().equals(organiserName))
				incomingEvents.add(event);
		}
		return incomingEvents;  
  }

}
