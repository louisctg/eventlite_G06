package uk.ac.man.cs.eventlite.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String linkToSearch() {


		return "events/search";
	}

	
	@RequestMapping(value = "/result", method = RequestMethod.GET)
	public String searchByKey(@RequestParam(value = "key")String key, Model model) {
		
		List<Event> res = new ArrayList<>();
		for(Event e : eventService.findAll()){
			if (e.getName().contains(key)){
				res.add(e);
			}
		}

		model.addAttribute("events", res);
		
		return "events/result";
	}
	
}
