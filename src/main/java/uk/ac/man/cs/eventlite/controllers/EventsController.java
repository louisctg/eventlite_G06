package uk.ac.man.cs.eventlite.controllers;

import javax.inject.Inject;
import javax.validation.Valid;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueRepository;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	private Twitter twitter;
	private ConnectionRepository connectionRepository;
	
	@Inject
	public EventsController(Twitter twitter, ConnectionRepository connectionRepository) {
		this.twitter = twitter;
		this.connectionRepository = connectionRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model, Principal principal) {

		//model.addAttribute("events", eventService.findAll());
		Iterable<Event> futureEvents  =  eventService.findFutureEventsOrderedByNameAndDate();
		Iterable<Event> pastEvents  =  eventService.findPastEventsOrderedByNameAndDate();
		if(principal != null)
		{
		Iterable<Event> futureEventsOrganiser  =  eventService.futureEventsOrganiser(futureEvents, principal.getName());
		Iterable<Event> pastEventsOrganiser  =  eventService.pastEventsOrganiser(pastEvents, principal.getName());
		model.addAttribute("future_events_organiser", futureEventsOrganiser);
		model.addAttribute("past_events_organiser", pastEventsOrganiser);
		model.addAttribute("future_events", futureEvents);
		//the following line will be useful for the Show the location of one specific event on a map
		model.addAttribute("past_events", pastEvents);
		}
		else
		{
		model.addAttribute("future_events", futureEvents);
		model.addAttribute("past_events", pastEvents);
		}


		return "events/index";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getEvent(@PathVariable("id") long id,
			@RequestParam(value = "name", required = false, defaultValue = "Testing") String name, Model model) {
		Event event = eventService.findOne(id);
		model.addAttribute("event", event);
		return "events/event";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String linkToSearch() {
		return "events/search";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newEvent(Model model) {
		if (!model.containsAttribute("event")) {
			model.addAttribute("event", new Event());
			//we need to add the venues so we can extract them 
			//as references when we want to create a new venue
			model.addAttribute("venues", venueService.findAll());
		}

		return "events/new";
	}

	@RequestMapping(method = RequestMethod.POST, value="/new", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createEvent(@RequestBody @Valid @ModelAttribute Event event ,
			BindingResult errors, Model model, RedirectAttributes redirectAttrs, Principal principal) {

		if (errors.hasErrors()) {
			model.addAttribute("event", event);
			//when we failed to supply a correct  value for the fields in the html new 
			//the webpage will create an error and we send the same, with the same creation of event
			//and at the same time we will need to re-send the venues 
			//maybe we can send the same attribute send as we did with event but I am not sure if that will work
			model.addAttribute("time", event.getTime());
			
			model.addAttribute("venues", venueService.findAll());
			return "events/new";
		}
		
		event.setOrganiser(principal.getName());
		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "New event added.");
		
		return "redirect:/events";
	}

	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String getEventToUpdate(Model model, @PathVariable long id)
	{
		if(!model.containsAttribute("event")) {
			model.addAttribute("event", eventService.findOne(id));
			model.addAttribute("venues", venueService.findAll());
		}
		
		return "events/update";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String updateEvent(@RequestBody @Valid @ModelAttribute Event event, BindingResult errors,  @PathVariable long id, Model model, RedirectAttributes redirAttrs)
	{
		if(errors.hasErrors()) {
			model.addAttribute("event", event);
			model.addAttribute("venues", venueService.findAll());
			
			return "events/update";
		}

		event.setId(id);
		eventService.save(event);
		redirAttrs.addFlashAttribute("ok_message", "Event updated.");
				
		return "redirect:/events";
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String deleteEvent(@PathVariable("id") long id)
	{
		eventService.delete(id);
		
		return "redirect:/events";
	}

	
	@RequestMapping(value = "/result", method = RequestMethod.GET)
	public String searchByKey(@RequestParam(value = "key")String key, Model model) {
		
		model.addAttribute("future_events", eventService.searchFutureEventsOrderedByNameAndDateAscending(key));
		model.addAttribute("past_events", eventService.searchPastEventsOrderedByNameAndDateDescending(key));

		return "events/result";
	}
	
	@RequestMapping(value = "/{id}/tweet", method = RequestMethod.POST)
	public String tweetEvent(@PathVariable long id, @RequestParam(value = "tweet", required = true) String tweet, Model model, RedirectAttributes attributes)
	{
		if (!twitter.isAuthorized()) {
            return "redirect:/connect/twitter";
        }
		
		Event event = eventService.findOne(id);
		tweet += "\n" + event.getName() + " #eventlite #g06 #SoftEng #UoM";
		
		try
		{
			twitter.timelineOperations().updateStatus(tweet);
			attributes.addFlashAttribute("message", "Your tweet: " + tweet + " was posted");
		}
		catch(Exception e)
		{
			attributes.addFlashAttribute("error", "Something went wrong: " + e.getMessage());
		}
		
		return "redirect:/events/" + id;
	}
	
}
