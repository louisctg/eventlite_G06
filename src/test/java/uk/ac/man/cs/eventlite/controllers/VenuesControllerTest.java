

package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
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

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.config.Security;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueRepository;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class VenuesControllerTest {

	private MockMvc mvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Mock
	private Venue venue;
	
	@Mock
	private VenueService venueService;

	@Mock
	private EventService eventService;

	@InjectMocks
	private VenuesController venuesController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}

	@Test
	public void getIndexVenue() throws Exception {

		when(venueService.findOne(0)).thenReturn(venue);

		mvc.perform(get("/venues/0").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/venue")).andExpect(handler().methodName("getVenue"));

		verify(venueService).findOne(0);
		verifyZeroInteractions(venue);
	}
	
		
	@Test
	public void getRemoveVenue() throws Exception {
		
		Venue venue1 = new Venue();
		venueService.save(venue1);
		when(venueService.findOne(0)).thenReturn(venue1);
		mvc.perform(MockMvcRequestBuilders.get("/venues/delete/0").accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
		.andExpect(view().name("redirect:/venues")).andExpect(handler().methodName("deleteVenue"));
		verify(venueService).delete(0);
		verifyZeroInteractions(venue);
	} 

	@Test
	public void getNewVenue() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("venues/new"))
		.andExpect(handler().methodName("newVenue"));
	}
	
	@Test
	public void linkToSearchVenue() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/venues/search")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("venues/search"));
	}
	
	@Test
	public void searchVenue() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/venues/result?key=Venue")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("venues/result"))
		.andExpect(handler().methodName("searchByKey"));
		
		verify(venueService).searchVenuesOrderedByNameAscending("Venue");
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void updateVenue() throws Exception {
		Venue venue1 = new Venue();
		venueService.save(venue1);
		when(venueService.findOne(0)).thenReturn(venue1);
		mvc.perform(MockMvcRequestBuilders.put("/venues/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0").param("name", "test").param("roadname", "test").param("postcode", "M13 9PL").param("capacity", "1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound()).andExpect(content().string(""))
		.andExpect(view().name("redirect:/venues")).andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateVenue")).andExpect(flash().attributeExists("ok_message"));

	}
}
