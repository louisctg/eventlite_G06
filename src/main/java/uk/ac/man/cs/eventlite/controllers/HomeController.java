package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;

@Controller
public class HomeController {
	
	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model)
	{
		model.addAttribute("events", eventService.findNext3UpcomingEvents());
		model.addAttribute("venues", venueService.findTop3VenuesWithMostEvents());
		return "index";
	}
}
