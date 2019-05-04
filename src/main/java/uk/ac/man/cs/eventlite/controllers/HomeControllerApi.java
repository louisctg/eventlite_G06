package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RestController
@RequestMapping(value="/api", produces = { MediaType.APPLICATION_JSON_VALUE,  MediaTypes.HAL_JSON_VALUE})
public class HomeControllerApi {
	
	@RequestMapping(method= RequestMethod.GET)
	public Resource<Home> getAllHomeLinks(){
		Link profileLink = linkTo(methodOn(HomeControllerApi.class).getAllHomeLinks()).slash("profile").withRel("profile");
		Link eventsLink = linkTo(EventsControllerApi.class).withRel("events");
		Link venuesLink = linkTo(VenuesControllerApi.class).withRel("venues");
		
		return new Resource<Home>(new Home(), venuesLink, eventsLink, profileLink);
	}
	
	@JsonIgnoreProperties
	public class Home{
		
	}
	
}
