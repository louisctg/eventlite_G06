package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {

		model.addAttribute("events", eventService.findAll());

		return "events/index";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getEvent(@PathVariable("id") long id,
			@RequestParam(value = "name", required = false, defaultValue = "Testing") String name, Model model) {

		Event event = eventService.findOne(id);
		model.addAttribute("event", event);

		return "events/event";
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

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createEvent(@RequestBody @Valid @ModelAttribute Event event ,
			BindingResult errors, Model model, RedirectAttributes redirectAttrs) {

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
		
		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "New greeting added.");
		
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
	public String updateEvent(@RequestBody @Valid @ModelAttribute Event event, @PathVariable long id, BindingResult errors, Model model, RedirectAttributes redirAttrs)
	{
		if(errors.hasErrors()) {
			model.addAttribute("event", event);
			return "events/update";
		}

		event.setId(id);
		eventService.save(event);
		redirAttrs.addFlashAttribute("ok_message", "Event updated.");
				
		return "redirect:/events";
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable("id") long id)
	{
		eventService.delete(id);
		
		return "redirect:/events";
	}
}
