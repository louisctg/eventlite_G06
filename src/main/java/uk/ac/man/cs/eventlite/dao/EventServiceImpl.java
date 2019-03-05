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
		return eventRepository.findByDateAfterOrderByNameAscDateDescTimeDesc(new Date());
	}

	@Override
	public Iterable<Event> findPastEventsOrderedByNameAndDate() {
		return eventRepository.findByDateAfterOrderByNameAscDateAscTimeAsc(new Date());
	}
	
	
}
