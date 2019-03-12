package uk.ac.man.cs.eventlite.config.data;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.entities.Event;

@Component
@Profile({ "default", "test" })
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (eventService.count() > 0 && venueService.count() > 0) {
			log.info("Database already populated. Skipping data initialization.");
			return;
		}
		
		Venue venue1 = new Venue("Royal Albert Hall", 5544, "London", "SW7 2AP");
		Venue venue2 = new Venue("Manchester Academy", 1000, "Manchester", "M14 4PX");

		// Build and save initial models here.
		venueService.save(venue1);
		venueService.save(venue2);
		
		Event newEvent1 = new Event();

		// Build and save initial models here.
		// Populate the event instance.
		Calendar cal = Calendar.getInstance();
		Date newDate = cal.getTime();
		Date newTime = cal.getTime();

		newEvent1.setName("Testing");
		newEvent1.setDate(newDate);
		newEvent1.setTime(newTime);
		newEvent1.setVenue(venue1);

		eventService.save(newEvent1);
		
		Event newEvent2 = new Event();
		newEvent2.setName("Tested");
		newEvent2.setDate(newDate);
		newEvent2.setTime(newTime);
		newEvent2.setVenue(venue2);

		eventService.save(newEvent2);
	}
}
