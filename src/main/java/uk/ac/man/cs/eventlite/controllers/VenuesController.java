package uk.ac.man.cs.eventlite.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;



@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenuesController {
	
	@Autowired
	private VenueService venueService;
	
	@Autowired
	private EventService eventService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String getAllVenues(Model model) {

		model.addAttribute("venues", venueService.findAllInAlphabeticalOrder());

		return "venues/index";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getVenue(@PathVariable("id") long id, Model model) {
	  Venue venue = venueService.findOne(id);
	  model.addAttribute("venue", venue);
	  model.addAttribute("upcoming_events", eventService.findUpcomingEventsWithVenue(id));
	  
	  return "venues/venue";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newVenue(Model model) {
		if (!model.containsAttribute("venues")) {
			model.addAttribute("venue", new Venue());
		}

		return "venues/new";
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/new", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createVenue(@RequestBody @Valid @ModelAttribute(name="venue") Venue venue,
			BindingResult errors, Model model, RedirectAttributes redirectAttrs) {

		if (errors.hasErrors()) {
			
			model.addAttribute("venue", venue);
			return "venues/new";
		}
		venue.setCoordinates(venue.getAddress(), venue.getPostcode());
		venueService.save(venue);
		redirectAttrs.addFlashAttribute("ok_message", "New venue added.");

		return "redirect:/venues";
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String linkToSearch() {
		return "venues/search";
	}
	
	@RequestMapping(value = "/result", method = RequestMethod.GET)
	public String searchByKey(@RequestParam(value = "key")String key, Model model) {
		
		model.addAttribute("venues", venueService.searchVenuesOrderedByNameAscending(key));

		return "venues/result";
	}
	
	@RequestMapping(value = "update/{id}", method = RequestMethod.GET)
	public String getVenueToUpdate(Model model, @PathVariable long id)
	{
		if(!model.containsAttribute("venue")) {
			model.addAttribute("venue", venueService.findOne(id));
		}

		return "venues/update";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String updateVenue(@RequestBody @Valid @ModelAttribute Venue venue, BindingResult errors,  @PathVariable long id, Model model, RedirectAttributes redirAttrs)
	{
		if(errors.hasErrors()) {
			model.addAttribute("venue", venue);

			return "venues/update";
		}

		venue.setId(id);
		venueService.save(venue);
		redirAttrs.addFlashAttribute("ok_message", "Venue updated.");

		return "redirect:/venues";
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String deleteVenue(@PathVariable("id") long id, RedirectAttributes attributes)
	{
		try{
			
			// Get the event list of the venue
			List<Event> events = (List<Event>) eventService.findUpcomingEventsWithVenue(id);			
			
			if(events.size() == 0)
			{
				venueService.delete(id);
			}
			else
			{
				attributes.addFlashAttribute("message", "This venue has other upcoming events. You shouldn't remove this!");
			}
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return "redirect:/venues";
		
		
	}
}