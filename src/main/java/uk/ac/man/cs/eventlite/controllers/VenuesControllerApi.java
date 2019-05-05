package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RestController
@RequestMapping(value = "/api/venues", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class VenuesControllerApi {

	@Autowired
	private VenueService venueService;
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private EventsControllerApi eventController;

	@RequestMapping(method = RequestMethod.GET)
	public Resources<Resource<Venue>> getAllVenues() {

		return venueToResource(venueService.findAllInAlphabeticalOrder());
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Resource<Venue> getVenue(@PathVariable("id") long id) {
		Venue venue = venueService.findOne(id);
		
		return venueToResource(venue);
	}
	
	@RequestMapping(value = "/{id}/next3events", method = RequestMethod.GET)
	public Resources<Resource<Event>> nextThreeEvents(@PathVariable("id") long id){
		Iterable<Event> events = eventService.findNext3UpcomingEventsWithVenue(id);
		
		return eventController.eventToResource(events);
	}

	private Resource<Venue> venueToResource(Venue venue) {
		Link selfLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).withSelfRel();
		Link venueLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).withRel("venue");
		Link eventLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).slash("events").withRel("events");
		Link next3eventLink = linkTo(VenuesControllerApi.class).slash(venue.getId()).slash("next3events").withRel("next3events");
		return new Resource<Venue>(venue, selfLink, venueLink, eventLink, next3eventLink);
	}

	private Resources<Resource<Venue>> venueToResource(Iterable<Venue> venues) {
		Link selfLink = linkTo(methodOn(VenuesControllerApi.class).getAllVenues()).withSelfRel();
		Link profileLink = linkTo(methodOn(HomeControllerApi.class).getAllHomeLinks()).slash("profile").slash("venues").withRel("profile");
		List<Resource<Venue>> resources = new ArrayList<Resource<Venue>>();
		if(venues != null)
		for (Venue venue : venues) {
			resources.add(venueToResource(venue));
		}
//		else{
//			resources.add(profileLink);
//		}

		return new Resources<Resource<Venue>>(resources, selfLink, profileLink);
	}

}
