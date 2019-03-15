package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.Collections;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class HomeControllerTest {
	
	private MockMvc mvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Mock
	private Event event;
	
	@Mock
	private Venue venue;
	
	@Mock
	private EventService eventService;
	
	@Mock
	private VenueService venueService;
	
	@InjectMocks
	private HomeController homeController;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(homeController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}

	@Test
	public void testHomepageNoEventsNoVenues() throws Exception {
		when(eventService.findNext3UpcomingEvents()).thenReturn(Collections.<Event> emptyList());
		when(venueService.findTop3VenuesWithMostEvents()).thenReturn(Collections.<Venue> emptyList());

		mvc.perform(get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
			.andExpect(view().name("index")).andExpect(handler().methodName("index"));
		
		verify(eventService).findNext3UpcomingEvents();
		verify(venueService).findTop3VenuesWithMostEvents();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void testHomepageOneEventOneVenue() throws Exception {
		when(eventService.findNext3UpcomingEvents()).thenReturn(Collections.<Event> singletonList(event));
		when(venueService.findTop3VenuesWithMostEvents()).thenReturn(Collections.<Venue> singletonList(venue));
		
		mvc.perform(get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("index")).andExpect(handler().methodName("index"));

		verify(eventService).findNext3UpcomingEvents();
		verify(venueService).findTop3VenuesWithMostEvents();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
}
