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

		// Build and save initial models here.
		Venue venue1 = new Venue("Royal Albert Hall", 5544, "Kensington Gore, Kensington, London", "SW7 2AP");
		Venue venue2 = new Venue("Manchester Academy", 1000, "Manchester Academy, Manchester", "M13 9PR");
		Venue venueA = new Venue("Venue A", 15, "Kilburn Building, University of Manchester, Manchester", "M13 9PL");
		Venue venueB = new Venue("Venue B", 10, "Roscoe Building, Manchester", "M13 9PY");

		venueService.save(venue1);
		venueService.save(venue2);
		venueService.save(venueA);
		venueService.save(venueB);
		
		Calendar cal = Calendar.getInstance();
		
		Event alpha = new Event();
		alpha.setName("Event Alpha");
		cal.add(Calendar.HOUR_OF_DAY, 4);
		alpha.setDate(cal.getTime());
		alpha.setTime(cal.getTime());
		alpha.setVenue(venue2);
		alpha.setOrganiser("Caroline");
		
		eventService.save(alpha);
		
		Event beta = new Event();
		beta.setName("Event Beta");
		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 2);
		beta.setDate(cal.getTime());
		beta.setTime(cal.getTime());
		beta.setVenue(venue1);
		beta.setOrganiser("Rob");

		
		eventService.save(beta);
		
		Event apple = new Event();
		apple.setName("Event Apple");
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		apple.setDate(cal.getTime());
		apple.setTime(cal.getTime());
		apple.setVenue(venueA);
		apple.setOrganiser("Markel");
		
		eventService.save(apple);
		
		Event former = new Event();
		former.setName("Event Former");
		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -2);
		former.setDate(cal.getTime());
		former.setTime(cal.getTime());
		former.setVenue(venue2);
		former.setOrganiser("Caroline");
		
		eventService.save(former);
		
		Event previous = new Event();
		previous.setName("Event Previous");
		cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -4);
		previous.setDate(cal.getTime());
		previous.setTime(cal.getTime());
		previous.setVenue(venue1);
		previous.setOrganiser("Rob");
		
		eventService.save(previous);
		
		Event past = new Event();
		past.setName("Event Past");
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		past.setDate(cal.getTime());
		past.setTime(cal.getTime());
		past.setVenue(venue1);
		past.setOrganiser("Markel");
		
		eventService.save(past);
	}
}
