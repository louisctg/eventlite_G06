package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class VenuesControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	private HttpEntity<String> httpEntity;

	@Autowired
	private TestRestTemplate template;
	@Autowired
	private VenueService venueService;
	
	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

		httpEntity = new HttpEntity<String>(headers);
	}

	@Test
	public void testGetAllVenuesPage() {
		ResponseEntity<String> response = template.exchange("/venues", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		
		// Test the search result is same as find all
		List<Venue> venues = (List<Venue>) venueService.findAll();
		
		String body = response.getBody();
		
		for(int i=0; i<venues.size(); i++)
		{
			assertTrue(body.contains(venues.get(i).getName()));
		}
		
	}
	
	@Test
	public void testGetAddNewVenuePage() {
		
		ResponseEntity<String> response = template.exchange("/venues/new", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	
	@Test
	public void testPostAddNewVenueWithSensibleData() {

		String login = "http://localhost:8080/sign-in";
		String createEventPage = "http://localhost:8080/venues/new";
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
		requestForCreatingEvent.add("name", "Test Venue");
		requestForCreatingEvent.add("capacity", "10000");
		requestForCreatingEvent.add("roadname", "MyHouse");
		requestForCreatingEvent.add("postcode", "M1 5EA");
		requestForCreatingEvent.add("lat", "10");
		requestForCreatingEvent.add("lng", "10");
		postBody = new HttpEntity<MultiValueMap<String,String>>(requestForCreatingEvent,postMethod); 
		ResponseEntity<String> sendInformation = template.exchange(createEventPage, HttpMethod.POST, postBody, String.class);
		assertThat(sendInformation.getStatusCode(), equalTo(HttpStatus.FOUND));
		
		
	}
	
	@Test
	public void testPostAddNewVenueBadData() {
		String login = "http://localhost:8080/sign-in";
		String createEventPage = "http://localhost:8080/venues/new";
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
		requestForCreatingEvent.add("name", "::::");
		requestForCreatingEvent.add("capacity", "::::");
		requestForCreatingEvent.add("roadname", "::::");
		requestForCreatingEvent.add("postcode", "::::");
		requestForCreatingEvent.add("lat", "::::");
		requestForCreatingEvent.add("lng", "::::");
		postBody = new HttpEntity<MultiValueMap<String,String>>(requestForCreatingEvent,postMethod); 
		ResponseEntity<String> sendInformation = template.exchange(createEventPage, HttpMethod.POST, postBody, String.class);
		assertThat(sendInformation.getStatusCode(), equalTo(HttpStatus.OK));
	}
	
	
	@Test
	public void testGetSearchVenuePage() {
		
		ResponseEntity<String> response = template.exchange("/venues/search", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	

	
	@Test
	public void testGetSearchResultEqualToKey() {
		
		// Test the search result is same as the venue service
		List<Venue> venues = (List<Venue>) venueService.searchVenuesOrderedByNameAscending("Venue");
		
		
		ResponseEntity<String> response = template.exchange("/venues/result?key=Venue", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		
		String body = response.getBody();
		
		for(int i=0; i<venues.size(); i++)
		{
			assertTrue(body.contains(venues.get(i).getName()));
		}
		
	}
	

	
}
