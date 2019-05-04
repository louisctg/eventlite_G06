package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static uk.ac.man.cs.eventlite.testutil.MessageConverterUtil.getMessageConverters;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.EventLite;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class HomeControllerApiTest {
	
	private MockMvc mvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@InjectMocks
	private HomeControllerApi homeController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(homeController).apply(springSecurity(springSecurityFilterChain))
				.setMessageConverters(getMessageConverters()).build();
	}
	
	@Test
	public void getAllHomeLinksTest() throws Exception
	{
		mvc.perform(MockMvcRequestBuilders.get("/api").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(handler().methodName("getAllHomeLinks"))
			.andExpect(jsonPath("$.length()", equalTo(1)))
			.andExpect(jsonPath("$._links.length()", equalTo(3)))
			.andExpect(jsonPath("$._links.events.href", endsWith("api/events")))
			.andExpect(jsonPath("$._links.venues.href", endsWith("api/venues")))
			.andExpect(jsonPath("$._links.profile.href", endsWith("api/profile")));
	}


}
