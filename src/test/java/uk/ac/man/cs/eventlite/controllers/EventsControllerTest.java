package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
	private EventService eventService;
	
	@Mock
	private VenueService venueService;

	@InjectMocks
	private EventsController eventsController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventsController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findFutureEventsOrderedByNameAndDate()).thenReturn(Collections.<Event> emptyList());
		when(eventService.findPastEventsOrderedByNameAndDate()).thenReturn(Collections.<Event> emptyList());


		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findFutureEventsOrderedByNameAndDate();
		verify(eventService).findPastEventsOrderedByNameAndDate();
		verifyZeroInteractions(event);
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		when(eventService.findFutureEventsOrderedByNameAndDate()).thenReturn(Collections.<Event> singletonList(event));
		when(eventService.findPastEventsOrderedByNameAndDate()).thenReturn(Collections.<Event> singletonList(event));

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findFutureEventsOrderedByNameAndDate();
		verify(eventService).findPastEventsOrderedByNameAndDate();
		verifyZeroInteractions(event);
	}
	
	@Test
	public void getIndexEvent() throws Exception {
		
		when(eventService.findOne(1)).thenReturn(event);

		mvc.perform(MockMvcRequestBuilders.get("/events/1").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/event")).andExpect(handler().methodName("getEvent"));

		verify(eventService).findOne(1);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void linkToSearchEvent() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/events/search")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("events/search"));
	}
	
	@Test
	public void removeEvent() throws Exception {
		
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		mvc.perform(MockMvcRequestBuilders.get("/events/delete/0").accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
		.andExpect(view().name("redirect:/events")).andExpect(handler().methodName("deleteEvent"));
		verify(eventService).delete(0);
		verifyZeroInteractions(event);
	}
	
	@Test
	public void searchEvent() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/events/result?key=Event")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("events/result"))
		.andExpect(handler().methodName("searchByKey"));
		
		verify(eventService).searchFutureEventsOrderedByNameAndDateAscending("Event");
		verify(eventService).searchPastEventsOrderedByNameAndDateDescending("Event");
		verifyZeroInteractions(event);
	}

	
	@Test
	public void createNewEvent() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("events/new"))
		.andExpect(handler().methodName("newEvent"));
	}
	
	@Test
	public void postEvent() throws Exception {
		//ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		mvc.perform(MockMvcRequestBuilders.post("/events/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0").param("name", "test").param("date", "2020-01-01").param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
				.andExpect(status().isFound()).andExpect(content().string(""))
				.andExpect(view().name("redirect:/events")).andExpect(model().hasNoErrors())
				.andExpect(handler().methodName("createEvent")).andExpect(flash().attributeExists("ok_message"));

	}
	
	@Test
	public void updateEvent() throws Exception {
		Event event1 = new Event();
		eventService.save(event1);
		when(eventService.findOne(0)).thenReturn(event1);
		mvc.perform(MockMvcRequestBuilders.put("/events/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0").param("name", "test").param("date", "2020-01-01").param("venue.name", "venue1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound()).andExpect(content().string(""))
		.andExpect(view().name("redirect:/events")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateEvent")).andExpect(flash().attributeExists("ok_message"));

	}
}
