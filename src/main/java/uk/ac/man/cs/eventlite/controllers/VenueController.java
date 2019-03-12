package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
import uk.ac.man.cs.eventlite.dao.VenueRepository;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller
@RequestMapping(value = "/venues", produces = { MediaType.TEXT_HTML_VALUE })
public class VenueController {

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAllVenues(Model model) {

		model.addAttribute("venues", venueService.findAll());

		return "venues/index";
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
	public String updateEvent(@RequestBody @Valid @ModelAttribute Venue venue, BindingResult errors,  @PathVariable long id, Model model, RedirectAttributes redirAttrs)
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
	
}
