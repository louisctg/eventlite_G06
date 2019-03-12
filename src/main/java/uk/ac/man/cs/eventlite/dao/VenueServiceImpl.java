package uk.ac.man.cs.eventlite.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class VenueServiceImpl implements VenueService {
	
	@Autowired
	private VenueRepository venueRepository;
	
	@Autowired
	private EventService eventService;

	@Override
	public long count() {
		return venueRepository.count();
	}

	@Override
	public Iterable<Venue> findAll() {
		return venueRepository.findAll();
	}
	
	@Override
	public void save(Venue venue) {
		//update the database with a new venue
		venueRepository.save(venue);
	}

	@Override
	public Iterable<Venue> findTop3VenuesWithMostEvents() {
		List<Venue> venues = new ArrayList<Venue>();
		venueRepository.findAll().forEach(venue -> venues.add(venue));
		
		List<Event> futureEvents = new ArrayList<Event>();
		eventService.findFutureEventsOrderedByNameAndDate().forEach(event -> futureEvents.add(event));
		
		Collections.sort(venues, new Comparator<Venue>() {
			public int compare(Venue v1, Venue v2) {
				final long v1FutureEvents = v1.getEvents().stream().filter(event -> futureEvents.contains(event)).count();
				final long v2FutureEvents = v2.getEvents().stream().filter(event -> futureEvents.contains(event)).count();
				return (int) ((int) v2FutureEvents - v1FutureEvents);
			}
		});

		List<Venue> top3Venues = new ArrayList<Venue>();
		
		for(Venue venue: venues) {
			if(top3Venues.size() < 3)
			{
				if(venue.getEvents().stream().filter(event -> futureEvents.contains(event)).count() > 0)
					top3Venues.add(venue);
				
			}
			else break;
		}
		
		return top3Venues;
	}

}
