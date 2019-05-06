package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class EventsControllerApiIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	private MockMvc mvc;
	private HttpEntity<String> httpEntity;
	@Autowired
	private EventService eventService;
	@Autowired
	private TestRestTemplate template;

	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		httpEntity = new HttpEntity<String>(headers);
	}

	@Test
	public void testGetAllEvents() {
		ResponseEntity<String> response = template.exchange("/api/events", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	@Test 
	public void testGetEvent() throws Exception 
	{
		// Check the Delete controller
		//getId
		long eventId = 0;
		List<Event> events = (List<Event>) eventService.findAll();
		//Event evenToTest;
		Iterable<Event> result = eventService.findAll();
		if(events.size() > 0)
		{
			for(Event eventToBeTested: result)
			{


							eventId = eventToBeTested.getId();
							System.out.println(">>>>>>" + eventId);
							break;
			}
		}
		ResponseEntity<String> response = template.exchange("/api/events/"+ eventId, HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	@Test 
	public void testGetVenue() throws Exception 
	{
		// Check the Delete controller
		//getId
		long eventId = 0;
		List<Event> events = (List<Event>) eventService.findAll();
		//Event evenToTest;
		Iterable<Event> result = eventService.findAll();
		if(events.size() > 0)
		{
			for(Event eventToBeTested: result)
			{


							eventId = eventToBeTested.getId();
							System.out.println(">>>>>>" + eventId);
							break;
			}
		}
		ResponseEntity<String> response = template.exchange("/api/events/"+ eventId + "/venue", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
}
