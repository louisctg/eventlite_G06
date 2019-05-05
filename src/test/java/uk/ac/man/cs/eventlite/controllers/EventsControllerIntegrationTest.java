package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class EventsControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	private HttpEntity<String> httpEntity;

	@Autowired
	private TestRestTemplate template;
	@Autowired
	private EventService eventService;

	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

		httpEntity = new HttpEntity<String>(headers);
	}

	@Test
	public void testGetAllEvents() {
		ResponseEntity<String> response = template.exchange("/events", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	
	@Test
	public void testDeleteEventAuthenticated() {	
		// Enable cookie in the template
		template = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
		
		// Create a new post header
		HttpHeaders postMethod = new HttpHeaders();
		postMethod.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		postMethod.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Set the get header
		httpEntity = new HttpEntity<>(postMethod);
		ResponseEntity<String> response = template.exchange("http://localhost:8080/sign-in", HttpMethod.GET, httpEntity, String.class);
		
		// Check the status
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		
		// Check CSRF token
		String responseBody = response.getBody();
		Pattern p = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
		Matcher m = p.matcher(responseBody);

		// Check the pattern of the CSRF token 
		assertThat(m.matches(), equalTo(true));
		
		// Get the CSRF token
		String token = m.group(1);

		MultiValueMap<String, String> loginDetail = new LinkedMultiValueMap<String,String>();
		loginDetail.add("_csrf", token);
		loginDetail.add("username", "Rob");
		loginDetail.add("password", "Haines");
		
		// Check the authentication 
		HttpEntity<MultiValueMap<String, String>> postBody = new HttpEntity<MultiValueMap<String,String>>(loginDetail,postMethod); 
		ResponseEntity<String> loginResponse = template.exchange("http://localhost:8080/sign-in", HttpMethod.POST, postBody, String.class);
		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.FOUND));
		
		// Check the Delete controller
		//getId
		int eventId = 0;
		List<Event> events = (List<Event>) eventService.findAll();
		if(events.size() > 0)
		{
			eventId = (int) events.get(0).getId();
		}
		
		response = template.exchange("http://localhost:8080/events/delete/7" , HttpMethod.GET, postBody, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FOUND));
		
	}

}
