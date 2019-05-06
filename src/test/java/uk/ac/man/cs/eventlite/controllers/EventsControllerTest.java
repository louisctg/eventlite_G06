package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

//import hello.entities.Greeting;
import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class EventsControllerTest {

	private MockMvc mvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Mock
	private Event event;
	
	@Mock
	private Venue venue;
	
	private String testMaxChar;
	private String presentDay;	
	
	@Mock
	@Autowired
	private Twitter twitter;

	@Mock
	private EventService eventService;
	
	@Mock
	private VenueService venueService;

	@InjectMocks
	private EventsController eventsController;

	@Before
	public void setup() {
		
		char[] c = new char[999];
		testMaxChar = new String(c);
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date dateobj = new Date();
		
		presentDay = df.format(dateobj);
		
		
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventsController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}

	@Test
	public void testGetIndexWhenNoEvents() throws Exception {
		when(eventService.findFutureEventsOrderedByNameAndDate()).thenReturn(Collections.<Event> emptyList());
		when(eventService.findPastEventsOrderedByNameAndDate()).thenReturn(Collections.<Event> emptyList());


		mvc.perform(get("/events")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk())
		.andExpect(view().name("events/index"));

		verify(eventService).findFutureEventsOrderedByNameAndDate();
		verify(eventService).findPastEventsOrderedByNameAndDate();
		verifyZeroInteractions(event);
	}

	@Test
	public void testGetIndexWithEvents() throws Exception {
		
		when(eventService.findFutureEventsOrderedByNameAndDate()).thenReturn(Collections.<Event> singletonList(event));
		when(eventService.findPastEventsOrderedByNameAndDate()).thenReturn(Collections.<Event> singletonList(event));

		mvc.perform(get("/events")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk())
		.andExpect(view().name("events/index"))
		.andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findFutureEventsOrderedByNameAndDate();
		verify(eventService).findPastEventsOrderedByNameAndDate();
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testLinkToSearchEvent() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/events/search")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("events/search"));
	}
	
	@Test
	public void testSearchEventByName() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/events/result?key=Event")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("events/result"))
		.andExpect(handler().methodName("searchByKey"));
		
		verify(eventService).searchFutureEventsOrderedByNameAndDateAscending("Event");
		verify(eventService).searchPastEventsOrderedByNameAndDateDescending("Event");
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testGetEventDetail() throws Exception {
		
		when(eventService.findOne(1)).thenReturn(event);

		mvc.perform(MockMvcRequestBuilders.get("/events/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/event")).andExpect(handler().methodName("getEvent"));

		verify(eventService).findOne(1);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testCreateEventPage() throws Exception {
		//ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		mvc.perform(MockMvcRequestBuilders.get("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("events/new"))
		.andExpect(handler().methodName("newEvent"));

	}
	
	@Test
	public void testCreateNewEventWithCorrectDataWithAuthenticated() throws Exception {
		
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("date","2099-08-01")
				.param("time","20:54")
				.param("description", "Test")
				.param("venue.name", "Venue A")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(view().name("redirect:/events"))
		.andExpect(MockMvcResultMatchers.flash().attribute("message", "New event added."))
		.andExpect(status().isFound())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, never()).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);

	}
	
	@Test
	public void testCreateNewEventWithoutName() throws Exception {
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","")
				.param("date","2099-08-01")
				.param("time","20:54")
				.param("description", "Test")
				.param("venue.name", "Venue A")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasErrors())
		.andExpect(view().name("events/new"))
		.andExpect(status().isOk())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);

	}
	
	@Test
	public void testCreateNewEventWithoutDate() throws Exception {
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("date","")
				.param("time","20:54")
				.param("description", "Test")
				.param("venue.name", "Venue A")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasErrors())
		.andExpect(view().name("events/new"))
		.andExpect(status().isOk())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testCreateNewEventWithoutVenue() throws Exception {
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("date","2099-10-10")
				.param("time","20:54")
				.param("description", "Test")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasErrors())
		.andExpect(view().name("events/new"))
		.andExpect(status().isOk())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	
	@Test
	public void testCreateNewEventWithNameTooLong() throws Exception {
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name",testMaxChar)
				.param("date","2099-10-10")
				.param("time","20:54")
				.param("description", "Test")
				.param("venue.name", "Venue A")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasErrors())
		.andExpect(view().name("events/new"))
		.andExpect(status().isOk())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testCreateNewEventWithDateBeforePresentDay() throws Exception {
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("date","0000-08-01")
				.param("time","20:54")
				.param("description", "Test")
				.param("venue.name", "Venue A")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasErrors())
		.andExpect(view().name("events/new"))
		.andExpect(status().isOk())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testCreateNewEventWithDateOnPresentDay() throws Exception {
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("date",presentDay)
				.param("time","20:54")
				.param("description", "Test")
				.param("venue.name", "Venue A")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasErrors())
		.andExpect(view().name("events/new"))
		.andExpect(status().isOk())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testCreateNewEventWithDescriptionTooLong() throws Exception {
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("date","2020-08-01")
				.param("time","20:54")
				.param("description", testMaxChar)
				.param("venue.name", "Venue A")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasErrors())
		.andExpect(view().name("events/new"))
		.andExpect(status().isOk())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testCreateNewEventWithoutTime() throws Exception {
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","")
				.param("date","2020-08-01")
				.param("description", "Test")
				.param("venue.name", "Venue A")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasErrors())
		.andExpect(view().name("events/new"))
		.andExpect(status().isOk())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testCreateNewEventWithoutDescription() throws Exception {
	    when(eventService.save(event)).thenReturn(event);
	    when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
	    	
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","")
				.param("date","2020-08-01")
				.param("time","20:54")
				.param("venue.name", "Venue A")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(model().hasErrors())
		.andExpect(view().name("events/new"))
		.andExpect(status().isOk())
		.andExpect(handler().methodName("createEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testUpdateEventWithCorrectDataWithAuthenticated() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0")
				.param("name", "test")
				.param("date", "2020-01-01")
				.param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isFound())
		.andExpect(content().string(""))
		.andExpect(view().name("redirect:/events"))
		.andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateEvent"))
		.andExpect(flash().attributeExists("message"));
	}
	
	@Test
	public void testUpdateEventWithoutName() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.save(event)).thenReturn(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0")
				.param("date", "2020-01-01")
				.param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("events/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testUpdateEventWithoutDate() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.save(event)).thenReturn(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name","Test")
				.param("id", "0")
				.param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("events/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testUpdateEventWithoutVenue() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.save(event)).thenReturn(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", "Test")
				.param("id", "0")
				.param("date", "2020-01-01")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("events/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	
	@Test
	public void testUpdateEventWithNameTooLong() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.save(event)).thenReturn(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", testMaxChar)
				.param("id", "0")
				.param("date", "2020-01-01")
				.param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("events/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testUpdateEventWithDateBeforePresentDay() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.save(event)).thenReturn(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", "Test")
				.param("id", "0")
				.param("date", "0000-01-01")
				.param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("events/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testUpdateEventWithDateOnPresentDay() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.save(event)).thenReturn(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", "Test")
				.param("id", "0")
				.param("date", presentDay)
				.param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("events/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testUpdateEventWithDescriptionTooLong() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.save(event)).thenReturn(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", "Test")
				.param("id", "0")
				.param("date", "2020-01-01")
				.param("description", testMaxChar)
				.param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isOk())
		.andExpect(view().name("events/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateEvent"));
		
		verify(venueService, times(1)).findAll();
		
		verifyZeroInteractions(venue);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testUpdateEventWithoutTime() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.save(event)).thenReturn(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", "Test")
				.param("id", "0")
				.param("date", "2020-01-01")
				.param("venue.name", "venue1")
				.param("description", "Test")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isFound())
		.andExpect(content().string(""))
		.andExpect(view().name("redirect:/events"))
		.andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateEvent"))
		.andExpect(flash().attributeExists("message"));
		
		verifyZeroInteractions(venueService);
	}
	
	@Test
	public void testUpdateEventWithoutDescription() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.save(event)).thenReturn(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("name", "Test")
				.param("id", "0")
				.param("date", "2020-01-01")
				.param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML)
				.with(csrf()))
		.andExpect(status().isFound())
		.andExpect(content().string(""))
		.andExpect(view().name("redirect:/events"))
		.andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateEvent"))
		.andExpect(flash().attributeExists("message"));
		
		verifyZeroInteractions(venueService);
	}

	@Test
	public void testUpdateEventPage() throws Exception {

		mvc.perform(get("/events/update").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isBadRequest());

	}
	
	@Test
	public void testRemoveEvent() throws Exception {
		
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		
		mvc.perform(MockMvcRequestBuilders.get("/events/delete/0").accept(MediaType.TEXT_HTML))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/events"))
		.andExpect(MockMvcResultMatchers.flash().attribute("message", "Event removed."))
		.andExpect(handler().methodName("deleteEvent"));
		
		verify(eventService).delete(0);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void testTweetEventConnection() throws Exception {
		
		// When user not login twitter
		when(!twitter.isAuthorized()).thenReturn(false);
		
		Event e1 = new Event();
		eventService.save(e1);
		
		mvc.perform(MockMvcRequestBuilders.post("/events/0/tweet").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.with(csrf())
				.accept(MediaType.TEXT_HTML)
				.param("tweet","Hello"))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/connect/twitter"))
		.andExpect(handler().methodName("tweetEvent"));
	}
	
}
