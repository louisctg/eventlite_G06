package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.VenueService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class VenuesControllerApiIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	private HttpEntity<String> httpEntity;
	@Autowired
	private VenueService venueService;
	
	@Autowired
	private TestRestTemplate template;

	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		httpEntity = new HttpEntity<String>(headers);
	}

	@Test
	public void testGetAllVenues() {
		ResponseEntity<String> response = template.exchange("/api/venues", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	@Test
	public void testGetVenue() {
		long idVenue;
		if(venueService.findAll().iterator().hasNext())
		{
			idVenue = venueService.findAll().iterator().next().getId();
			ResponseEntity<String> response = template.exchange("/api/venues/" + idVenue, HttpMethod.GET, httpEntity, String.class);
		
			assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		}
	}
	@Test
	public void testGetEvent() {
		long idVenue;
		if(venueService.findAll().iterator().hasNext())
		{
			idVenue = venueService.findAll().iterator().next().getId();
			ResponseEntity<String> response = template.exchange("/api/venues/" + idVenue + "/events", HttpMethod.GET, httpEntity, String.class);
		
			assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		}
	}
	@Test
	public void testGetNext3Events() {
		long idVenue;
		if(venueService.findAll().iterator().hasNext())
		{
			idVenue = venueService.findAll().iterator().next().getId();
			ResponseEntity<String> response = template.exchange("/api/venues/" + idVenue + "/next3events", HttpMethod.GET, httpEntity, String.class);
		
			assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		}
	}
}
