package uk.ac.man.cs.eventlite.dao;

import java.util.Iterator;
import java.io.InputStream;
import java.util.ArrayList;
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
		/*
		ArrayList<Event> events = new ArrayList<Event>();

		try {
			ObjectMapper mapper = new ObjectMapper();
			InputStream in = new ClassPathResource(DATA).getInputStream();

			events = mapper.readValue(in, mapper.getTypeFactory().constructCollectionType(List.class, Event.class));
		} catch (Exception e) {
			log.error("Exception while reading file '" + DATA + "': " + e);
			// If we can't read the file, then the event list is empty...
		}
		*/
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
		Iterable<Event> eventsAfterToday =   eventRepository.findByDateAfterOrderByDateAscTimeAscNameAsc(new Date());
		Iterable<Event> eventsToday =   eventRepository.findByDateEqualsAndTimeGreaterThanEqualOrderByDateAscTimeAscNameAsc(new Date(), new Date());
		
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
		Iterable<Event> eventsBeforeToday =   eventRepository.findByDateBeforeOrderByDateDescTimeDescNameAsc(new Date());
		Iterable<Event> eventsToday =   eventRepository.findByDateEqualsAndTimeLessThanOrderByDateDescTimeDescNameAsc(new Date(), new Date());
		
		List<Event> pastEvents = new ArrayList<Event>();
		for(Event event: eventsBeforeToday) {
			pastEvents.add(event);
		}
		for(Event event: eventsToday) {
			pastEvents.add(event);
		}
		
		return pastEvents;
	}
	
	@Override
	public Iterable<Event> searchFutureEventsOrderedByNameAndDateAscending(String name) {
		Iterable<Event> eventsAfterToday =  eventRepository.findByNameContainingIgnoreCaseAndDateAfterOrderByDateAscTimeAscNameAsc(name, new Date());
		Iterable<Event> todayEvents = eventRepository.findByNameContainingIgnoreCaseAndDateEqualsAndTimeGreaterThanEqualOrderByDateAscTimeAscNameAsc(name, new Date(), new Date());
		
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
		Iterable<Event> eventsBeforeToday =  eventRepository.findByNameContainingIgnoreCaseAndDateBeforeOrderByDateDescTimeDescNameAsc(name, new Date());
		Iterable<Event> todayEvents = eventRepository.findByNameContainingIgnoreCaseAndDateEqualsAndTimeLessThanOrderByDateDescTimeDescNameAsc(name, new Date(), new Date());
		
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
}
