

package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
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
import java.util.List;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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
	
	private String testMaxChar;
	
	private String name;
	private String roadname;
	private String postcode;
	private double latitude;
	private double longitude;

	
	@InjectMocks
	private VenuesController venuesController;
	
	@Before
	public void setup() {
		
		char[] c = new char[999];
		testMaxChar = new String(c);
		
		name = "Kilburn Building";
		roadname = "Oxford Rd, Manchester";
		postcode = "M13 9PL";
		longitude = -2.23212457975274;
		latitude = 53.4763057835875;
		
		
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}
	
	
	@Test
	public void testCreateVenueWithCorretData() throws Exception {
		
		
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("capacity","1000")
				.param("roadname","Kilburn Building")
				.param("postcode","M13 9PL")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/venues"))
		.andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("createVenue"))
		.andExpect(flash().attributeExists("success_message"));
	}
	
	@Test
	public void testCreateVenueWithNegativeCapacity() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("capacity","-1000")
				.param("roadname","Kilburn Building")
				.param("postcode","M13 9PL")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("venues/new"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("createVenue"));
	}
	
	@Test
	public void testCreateVenueWithNameTooLong() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name",testMaxChar)
				.param("capacity","1000")
				.param("roadname","Kilburn Building")
				.param("postcode","M13 9PL")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("venues/new"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("createVenue"));
	}
	
	@Test
	public void testCreateVenueWithAddressTooLong() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("capacity","1000")
				.param("roadname",testMaxChar)
				.param("postcode","M13 9PL")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("venues/new"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("createVenue"));
	}
	
	@Test
	public void testCreateVenueWithoutPostcode() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("capacity","1000")
				.param("roadname","Kilburn Building")
				.param("postcode", "")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("venues/new"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("createVenue"));
	}
	
	@Test
	public void testCreateVenueWithInvalidPostcode() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Test")
				.param("capacity","1000")
				.param("roadname","Kilburn Building")
				.param("postcode", "Mfadasd")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk()).andExpect(view().name("venues/new"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("createVenue"));
	}
	
	
	@Test
	public void testUpdateVenueWithCorrectData() throws Exception {
		Venue venue1 = new Venue();
		venueService.save(venue1);
		when(venueService.findOne(0)).thenReturn(venue1);
		
		mvc.perform(MockMvcRequestBuilders.put("/venues/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0")
				.param("name", "test")
				.param("roadname", "test")
				.param("postcode", "M13 9PL")
				.param("capacity", "1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound()).andExpect(content().string(""))
		.andExpect(view().name("redirect:/venues"))
		.andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateVenue")).andExpect(flash().attributeExists("success_message"));

	}
	
	@Test
	public void testUpdateVenueWithNegativeCapacity() throws Exception {
		Venue venue1 = new Venue();
		venueService.save(venue1);
		when(venueService.findOne(0)).thenReturn(venue1);
		
		mvc.perform(MockMvcRequestBuilders.put("/venues/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0")
				.param("name", "test")
				.param("roadname", "test")
				.param("postcode", "M13 9PL")
				.param("capacity", "-1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(""))
		.andExpect(view().name("venues/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateVenue"));

	}
	
	@Test
	public void testUpdateVenueWithNameTooLong() throws Exception {
		Venue venue1 = new Venue();
		venueService.save(venue1);
		when(venueService.findOne(0)).thenReturn(venue1);
		
		mvc.perform(MockMvcRequestBuilders.put("/venues/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0")
				.param("name", testMaxChar)
				.param("roadname", "test")
				.param("postcode", "M13 9PL")
				.param("capacity", "1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(""))
		.andExpect(view().name("venues/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateVenue"));

	}
	
	@Test
	public void testUpdateVenueWithAddressTooLong() throws Exception {
		Venue venue1 = new Venue();
		venueService.save(venue1);
		when(venueService.findOne(0)).thenReturn(venue1);
		
		mvc.perform(MockMvcRequestBuilders.put("/venues/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0")
				.param("name", "Test")
				.param("roadname", testMaxChar)
				.param("postcode", "M13 9PL")
				.param("capacity", "1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(""))
		.andExpect(view().name("venues/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateVenue"));

	}
	
	@Test
	public void testUpdateVenueWithoutPostCode() throws Exception {
		Venue venue1 = new Venue();
		venueService.save(venue1);
		when(venueService.findOne(0)).thenReturn(venue1);
		
		mvc.perform(MockMvcRequestBuilders.put("/venues/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0")
				.param("name", "Test")
				.param("roadname", "test")
				.param("postcode", "")
				.param("capacity", "1")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isOk())
		.andExpect(content().string(""))
		.andExpect(view().name("venues/update"))
		.andExpect(model().hasErrors())
		.andExpect(handler().methodName("updateVenue"));

	}
	
	@Test
	public void testRemoveVenueWithoutEvent() throws Exception {
		
		Venue venue1 = new Venue();
		venueService.save(venue1);
		
		when(venueService.findOne(0)).thenReturn(venue1);
		
		mvc.perform(MockMvcRequestBuilders.get("/venues/delete/0").accept(MediaType.TEXT_HTML))
		
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/venues"))
		.andExpect(handler().methodName("deleteVenue"));
	} 
	
	@Test
	public void testRemoveVenueWithOneOrMoreEvents() throws Exception {
		
		Venue venue1 = new Venue();
		Event event1 = new Event();
		
		venue1.addEvent(event1);
		venueService.save(venue1);
		
		when(venueService.findOne(0)).thenReturn(venue1);
		
		mvc.perform(MockMvcRequestBuilders.get("/venues/delete/0").accept(MediaType.TEXT_HTML))
		
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/venues"))
		.andExpect(handler().methodName("deleteVenue"));
	} 

	@Test
	public void testGetAllVenuesOrderedAlphabetically() throws Exception {

		when(venueService.findOne(0)).thenReturn(venue);

		mvc.perform(get("/venues/0").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/venue")).andExpect(handler().methodName("getVenue"));

		verify(venueService).findOne(0);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void testGetIndexWhenNoVenue() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());


		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verifyZeroInteractions(venue);
	}

	@Test
	public void testGetIndexWithVenues() throws Exception {
		Venue venue1 = new Venue();
		venueService.save(venue1);
		
		List<Venue> venues = (List<Venue>) venueService.findAll();
		
		when(venueService.findAll()).thenReturn(venues);

		
		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService).findAll();
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void testLinkToSearch() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/venues/search")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("venues/search"));
	}
	
	@Test
	public void testSearchVenueKey() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/venues/result?key=Venue")
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().isOk()).andExpect(view().name("venues/result"))
		.andExpect(handler().methodName("searchByKey"));
		
		verify(venueService).searchVenuesOrderedByNameAscending("Venue");
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void testGetVenueDetail() throws Exception {

		when(venueService.findOne(0)).thenReturn(venue);

		mvc.perform(get("/venues/0").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/venue")).andExpect(handler().methodName("getVenue"));

		verify(venueService).findOne(0);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void testCoordinatesAfterCreatingVenue() throws Exception {
		
		ArgumentCaptor<Venue> argument = ArgumentCaptor.forClass(Venue.class);
		
		mvc.perform(MockMvcRequestBuilders.post("/venues/new").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name","Kilburn Building")
				.param("capacity","1000")
				.param("roadname","Oxford Rd, Manchester")
				.param("postcode","M13 9PL")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(view().name("redirect:/venues"))
		.andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("createVenue"))
		.andExpect(flash().attributeExists("success_message"));
		
		verify(venueService).save(argument.capture());
		
		assertThat(name,equalTo(argument.getValue().getName()));
		assertThat(roadname,equalTo(argument.getValue().getRoadname()));
		assertThat(postcode,equalTo(argument.getValue().getPostcode()));
		assertThat(latitude,equalTo(argument.getValue().getLat()));
		assertThat(longitude,equalTo(argument.getValue().getLng()));
	}
	
	@Test
	public void testCoordinatesAfterUpdatingVenue() throws Exception {
		
		ArgumentCaptor<Venue> argument = ArgumentCaptor.forClass(Venue.class);
		
		Venue venue1 = new Venue();
		when(venueService.findOne(0)).thenReturn(venue1);
		
		mvc.perform(MockMvcRequestBuilders.put("/venues/0").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("id", "0")
				.param("name","Kilburn Building")
				.param("capacity","1000")
				.param("roadname","Oxford Rd, Manchester")
				.param("postcode","M13 9PL")
				.accept(MediaType.TEXT_HTML).with(csrf()))
		.andExpect(status().isFound()).andExpect(content().string(""))
		.andExpect(view().name("redirect:/venues"))
		.andExpect(model().hasNoErrors())
		.andExpect(handler().methodName("updateVenue")).andExpect(flash().attributeExists("success_message"));
		
		verify(venueService).save(argument.capture());
		
		assertThat(name,equalTo(argument.getValue().getName()));
		assertThat(roadname,equalTo(argument.getValue().getRoadname()));
		assertThat(postcode,equalTo(argument.getValue().getPostcode()));
		assertThat(latitude,equalTo(argument.getValue().getLat()));
		assertThat(longitude,equalTo(argument.getValue().getLng()));
	}
}
