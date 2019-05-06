package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

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

import javassist.bytecode.Descriptor.Iterator;
import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
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

	@Autowired
	private VenueService venueService;
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
		long eventId = 0;
		List<Event> events = (List<Event>) eventService.findAll();
		//Event evenToTest;
		Iterable<Event> result = eventService.findAll();
		if(events.size() > 0)
		{
			for(Event eventToBeTested: result)
			{

				if(eventToBeTested.getOrganiser().equals("Rob"))
				{
							eventId = eventToBeTested.getId();
							System.out.println(">>>>>>" + eventId);
							response = template.exchange("http://localhost:8080/events/delete/" + eventId, HttpMethod.GET, postBody, String.class);
							assertThat(response.getStatusCode(), equalTo(HttpStatus.FOUND));
							break;
				}
			}
		}
		
		
		
	}
//	@Test
//	public void testDeleteEventNoAuthenticated() {	
//		// Enable cookie in the template
//		template = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
//		
//		// Create a new post header
//		HttpHeaders postMethod = new HttpHeaders();
//		postMethod.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
//		postMethod.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//		// Set the get header
//		httpEntity = new HttpEntity<>(postMethod);
//		ResponseEntity<String> response = template.exchange("http://localhost:8080/sign-in", HttpMethod.GET, httpEntity, String.class);
//		
//		// Check the status
//		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//		
//		// Check CSRF token
//		String responseBody = response.getBody();
//		Pattern p = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
//		Matcher m = p.matcher(responseBody);
//
//		// Check the pattern of the CSRF token 
//		assertThat(m.matches(), equalTo(true));
//		
//		// Get the CSRF token
//		String token = m.group(1);
//
//		MultiValueMap<String, String> loginDetail = new LinkedMultiValueMap<String,String>();
//		loginDetail.add("_csrf", token);
//		loginDetail.add("username", "NoUser");
//		loginDetail.add("password", "NoPass");
//		
//		// Check the authentication 
//		HttpEntity<MultiValueMap<String, String>> postBody = new HttpEntity<MultiValueMap<String,String>>(loginDetail,postMethod); 
//		ResponseEntity<String> loginResponse = template.exchange("http://localhost:8080/sign-in", HttpMethod.POST, postBody, String.class);
//		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.MOVED_PERMANENTLY));
//		
////		// Check the Delete controller
////		//getId
////		int eventId = 0;
////		List<Event> events = (List<Event>) eventService.findAll();
////		if(events.size() > 0)
////		{
////			eventId = (int) events.get(0).getId();
////		}
////		
////		response = template.exchange("http://localhost:8080/events/delete/71", HttpMethod.GET, postBody, String.class);
////		assertThat(response.getStatusCode(), equalTo(HttpStatus.FOUND));
//		
//	}
	
	
	@Test
	public void testnewEventAuthenticated() {	
		String login = "http://localhost:8080/sign-in";
		String createEventPage = "http://localhost:8080/events/new";
		template = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
		
		// Create a new post header
		HttpHeaders getMethod = new HttpHeaders();
		getMethod.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

				
		// Create a new post header
		HttpHeaders postMethod = new HttpHeaders();
		postMethod.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		postMethod.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		//log into web page 
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
		
		String cookies = response.getHeaders().getFirst("Set-Cookie").split(";")[0];
		
		postMethod.set("Cookie", cookies);
		MultiValueMap<String, String> loginDetail = new LinkedMultiValueMap<>();
		loginDetail.add("_csrf", token);
		loginDetail.add("username", "Rob");
		loginDetail.add("password", "Haines");
		
		//send the request to the serve to log in
		// Check the authentication 
		HttpEntity<MultiValueMap<String, String>> postBody = new HttpEntity<MultiValueMap<String,String>>(loginDetail,postMethod); 
		ResponseEntity<String> loginResponse = template.exchange(login, HttpMethod.POST, postBody, String.class);
		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.FOUND));
		
		getMethod.set("Cookie", cookies);
		httpEntity = new HttpEntity<>(getMethod);
		response = template.exchange(login, HttpMethod.GET, httpEntity, String.class);
		
		// Check CSRF token
		responseBody = response.getBody();
		p = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
		m = p.matcher(responseBody);
		
		
		// Check the pattern of the CSRF token 
		assertThat(m.matches(), equalTo(true));
		
		// Get the CSRF token
		token = m.group(1);
		
		MultiValueMap<String, String> requestForCreatingEvent = new LinkedMultiValueMap<>();
		requestForCreatingEvent.add("_csrf", token);
		requestForCreatingEvent.add("name", "test");
		requestForCreatingEvent.add("date", "2021-01-01");
		requestForCreatingEvent.add("venue.id", ""+ venueService.findAll().iterator().next().getId());
		postBody = new HttpEntity<MultiValueMap<String,String>>(requestForCreatingEvent,postMethod); 
		ResponseEntity<String> sendInformation = template.exchange(createEventPage, HttpMethod.POST, postBody, String.class);
		assertThat(sendInformation.getStatusCode(), equalTo(HttpStatus.FOUND));
		
	}
	@Test
	public void testUpdateEventAuthenticated() {	
		Event eventToBeTested = null;
		//getId
		List<Event> events = (List<Event>) eventService.findAll();
		//Event evenToTest;
		Iterable<Event> result = eventService.findAll();
		if(events.size() > 0)
		{
			for(Event e : result)
			{
				if(e.getOrganiser().equals("Rob"))
						{
							eventToBeTested = e;
							break;
						}
			}
		}
		if(eventToBeTested != null)
		{
		eventToBeTested = eventService.findAll().iterator().next();
		String login = "http://localhost:8080/sign-in";
		String updateEventPage = "http://localhost:8080/events/update/" + eventToBeTested.getId();
		template = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
		
		System.out.println(">>>>>" + updateEventPage);
		// Create a new post header
		HttpHeaders getMethod = new HttpHeaders();
		getMethod.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

				
		// Create a new post header
		HttpHeaders postMethod = new HttpHeaders();
		postMethod.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		postMethod.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		//log into web page 
		httpEntity = new HttpEntity<>(getMethod);
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
		
		String cookies = response.getHeaders().getFirst("Set-Cookie").split(";")[0];
		
		postMethod.set("Cookie", cookies);
		MultiValueMap<String, String> loginDetail = new LinkedMultiValueMap<>();
		loginDetail.add("_csrf", token);
		loginDetail.add("username", "Rob");
		loginDetail.add("password", "Haines");
		
		//send the request to the serve to log in
		// Check the authentication 
		HttpEntity<MultiValueMap<String, String>> postBody = new HttpEntity<MultiValueMap<String,String>>(loginDetail,postMethod); 
		ResponseEntity<String> loginResponse = template.exchange(login, HttpMethod.POST, postBody, String.class);
		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.FOUND));
		
		getMethod.set("Cookie", cookies);
		httpEntity = new HttpEntity<>(getMethod);
		response = template.exchange(login, HttpMethod.GET, httpEntity, String.class);
		
		// Check CSRF token
		responseBody = response.getBody();
		p = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
		m = p.matcher(responseBody);
		
		
		// Check the pattern of the CSRF token 
		assertThat(m.matches(), equalTo(true));
		
		// Get the CSRF token
		token = m.group(1);
		
		MultiValueMap<String, String> requestForCreatingEvent = new LinkedMultiValueMap<>();
		requestForCreatingEvent.add("_csrf", token);
		requestForCreatingEvent.add("name", "test");
		requestForCreatingEvent.add("date", "2022-01-01");
		requestForCreatingEvent.add("time", "10:30");
		requestForCreatingEvent.add("venue.id", "" + eventToBeTested.getVenue().getId());
		requestForCreatingEvent.add("_csrf", token);
		postBody = new HttpEntity<MultiValueMap<String,String>>(requestForCreatingEvent,postMethod); 
		ResponseEntity<String> sendInformation = template.exchange(updateEventPage, HttpMethod.POST, postBody, String.class);
		assertThat(sendInformation.getStatusCode(), equalTo(HttpStatus.METHOD_NOT_ALLOWED));
		
		}
	}
	
	@Test
	public void testSearchEventAuthenticated() {	
		String login = "http://localhost:8080/sign-in";
		String createEventPage = "http://localhost:8080/events/new";
		template = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
		
		// Create a new post header
		HttpHeaders getMethod = new HttpHeaders();
		getMethod.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

				
		// Create a new post header
		HttpHeaders postMethod = new HttpHeaders();
		postMethod.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		postMethod.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		//log into web page 
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
		
		String cookies = response.getHeaders().getFirst("Set-Cookie").split(";")[0];
		
		postMethod.set("Cookie", cookies);
		MultiValueMap<String, String> loginDetail = new LinkedMultiValueMap<>();
		loginDetail.add("_csrf", token);
		loginDetail.add("username", "Rob");
		loginDetail.add("password", "Haines");
		
		//send the request to the serve to log in
		// Check the authentication 
		HttpEntity<MultiValueMap<String, String>> postBody = new HttpEntity<MultiValueMap<String,String>>(loginDetail,postMethod); 
		ResponseEntity<String> loginResponse = template.exchange(login, HttpMethod.POST, postBody, String.class);
		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.FOUND));
		
		getMethod.set("Cookie", cookies);
		httpEntity = new HttpEntity<>(getMethod);
		response = template.exchange(login, HttpMethod.GET, httpEntity, String.class);
		
		// Check CSRF token
		responseBody = response.getBody();
		p = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
		m = p.matcher(responseBody);
		
		
		// Check the pattern of the CSRF token 
		assertThat(m.matches(), equalTo(true));
		
		// Get the CSRF token
		token = m.group(1);
		
		MultiValueMap<String, String> requestForCreatingEvent = new LinkedMultiValueMap<>();
		requestForCreatingEvent.add("_csrf", token);
		requestForCreatingEvent.add("name", "testXD");
		requestForCreatingEvent.add("date", "2021-01-01");
		requestForCreatingEvent.add("venue.id", ""+ venueService.findAll().iterator().next().getId());
		postBody = new HttpEntity<MultiValueMap<String,String>>(requestForCreatingEvent,postMethod); 
		ResponseEntity<String> sendInformation = template.exchange(createEventPage, HttpMethod.POST, postBody, String.class);
		assertThat(sendInformation.getStatusCode(), equalTo(HttpStatus.FOUND));
		sendInformation = template.exchange("http://localhost:8080/events/result?key=testXD", HttpMethod.GET, postBody, String.class);
		
		String page = sendInformation.getBody();
		assertTrue(page.contains("testXD"));
		sendInformation = template.exchange("http://localhost:8080/events/result?key=NotestXD", HttpMethod.GET, postBody, String.class);
		page = sendInformation.getBody();
		assertFalse(page.contains("testXD"));
	}
	

}
