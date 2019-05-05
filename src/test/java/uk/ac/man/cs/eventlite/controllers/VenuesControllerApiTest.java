package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.man.cs.eventlite.testutil.MessageConverterUtil.getMessageConverters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


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
public class VenuesControllerApiTest {

	private MockMvc mvc;

	@Mock
	private Venue venue;
	
	@Mock
	private Event event;
	
	@Autowired
	private Filter springSecurityFilterChain;

	@Mock
	private VenueService venueService;
	
	@Mock
	private EventService eventService;

	@InjectMocks
	private VenuesControllerApi venuesController;
	
	@InjectMocks
	private EventsControllerApi eventsController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).apply(springSecurity(springSecurityFilterChain))
				.setMessageConverters(getMessageConverters()).build();
	}

	@Test
	public void getIndexWhenNoVenues() throws Exception {

		when(venueService.findAllInAlphabeticalOrder()).thenReturn(Collections.<Venue> emptyList());

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues"))
				.andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")));

		verify(venueService).findAllInAlphabeticalOrder();
	}

	@Test
	public void getIndexWithVenues() throws Exception {
		Venue newVenue = new Venue();
		when(venueService.findAllInAlphabeticalOrder()).thenReturn(Collections.<Venue> singletonList(newVenue));
		
		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues"))
				.andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
				.andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)))
				.andExpect(jsonPath("$._embedded.venues[0]._links.events.href", not(empty())))
				.andExpect(jsonPath("$._embedded.venues[0]._links.events.href", endsWith("venues/0/events")))
				.andExpect(jsonPath("$._embedded.venues[0]._links.next3events.href", not(empty())))
				.andExpect(jsonPath("$._embedded.venues[0]._links.next3events.href", endsWith("venues/0/next3events")));
		
		verify(venueService).findAllInAlphabeticalOrder();
		
	}
	
	@Test
	public void getVenueTest() throws Exception {
		Venue newVenue = new Venue();
		newVenue.setId(0);
		newVenue.setName("test");
		newVenue.setCapacity(1000);
		newVenue.setRoadname("test");
		newVenue.setPostcode("test");
		when(venueService.findOne(0)).thenReturn(newVenue);
		
		mvc.perform(get("/api/venues/0").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("getVenue"))
				.andExpect(jsonPath("$.length()", equalTo(8)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0")))
				.andExpect(jsonPath("$.name", not(empty())))
				.andExpect(jsonPath("$.capacity", not(empty())))
				.andExpect(jsonPath("$.roadname", not(empty())))
				.andExpect(jsonPath("$.postcode", not(empty())))
				.andExpect(jsonPath("$.lat", not(empty())))
				.andExpect(jsonPath("$.lng", not(empty())))
				.andExpect(jsonPath("$._links.self.href", not(empty())))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues/0")))
				.andExpect(jsonPath("$._links.venue.href", not(empty())))
				.andExpect(jsonPath("$._links.venue.href", endsWith("/api/venues/0")))
				.andExpect(jsonPath("$._links.events.href", not(empty())))
				.andExpect(jsonPath("$._links.events.href", endsWith("venues/0/events")))
				.andExpect(jsonPath("$._links.next3events.href", not(empty())))
				.andExpect(jsonPath("$._links.next3events.href", endsWith("venues/0/next3events")));
		
		verify(venueService).findOne(0);
		
	}


}
