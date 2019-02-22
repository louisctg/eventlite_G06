package uk.ac.man.cs.eventlite.dao;

import java.util.Iterator;
import java.io.InputStream;
import java.util.ArrayList;
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
	public Event findOne(long id) {
		return eventRepository.findOne(id);
	}

	@Override
	public Event save(Event event) {
		return eventRepository.save(event);	
	}
}
