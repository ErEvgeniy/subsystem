package ru.ermolaev.services.subscriber.manager.controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.ermolaev.services.subscriber.manager.rest.SubscriberController;
import ru.ermolaev.services.subscriber.manager.rest.dto.SubscriberDto;
import ru.ermolaev.services.subscriber.manager.service.SubscriberService;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;
import ru.ermolaev.services.subscriber.manager.util.GsonLocalDateAdapter;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Rest контроллер по работе с абонентами ")
@Import(SubscriberController.class)
@WebMvcTest(SubscriberController.class)
class SubscriberControllerTest {

	private static final Long SUBSCRIBER_ID = 1L;

	@MockBean
	private SubscriberService subscriberService;

	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser
	@DisplayName("должен вернуть список всех абонентов")
	void shouldReturnAllSubscribers() throws Exception {
		SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto(SUBSCRIBER_ID);
		dummySubscriberDto.setCity(DummyDataHelper.getDummyCityDto());
		dummySubscriberDto.setStreet(DummyDataHelper.getDummyStreetDto());
		dummySubscriberDto.setBalance(0F);
		dummySubscriberDto.setConnectionDate(LocalDate.now().minusDays(1));

		when(subscriberService.findAll()).thenReturn(List.of(dummySubscriberDto));

		mvc.perform(get("/sub/v1/subscriber", SUBSCRIBER_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(SUBSCRIBER_ID))
				.andExpect(jsonPath("$[0].firstname").isNotEmpty())
				.andExpect(jsonPath("$[0].patronymic").isNotEmpty())
				.andExpect(jsonPath("$[0].lastname").isNotEmpty())
				.andExpect(jsonPath("$[0].contractNumber").isNotEmpty())
				.andExpect(jsonPath("$[0].accountNumber").isNotEmpty())
				.andExpect(jsonPath("$[0].city").isNotEmpty())
				.andExpect(jsonPath("$[0].street").isNotEmpty())
				.andExpect(jsonPath("$[0].house").isNotEmpty())
				.andExpect(jsonPath("$[0].flat").isNotEmpty())
				.andExpect(jsonPath("$[0].phoneNumber").isNotEmpty())
				.andExpect(jsonPath("$[0].email").isNotEmpty())
				.andExpect(jsonPath("$[0].balance").isNotEmpty())
				.andExpect(jsonPath("$[0].isActive").isNotEmpty())
				.andExpect(jsonPath("$[0].connectionDate").isNotEmpty());

		verify(subscriberService, times(1)).findAll();
	}

	@Test
	@WithMockUser
	@DisplayName("должен вернуть абонента по ID")
	void shouldReturnSubscriberById() throws Exception {
		SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto(SUBSCRIBER_ID);
		dummySubscriberDto.setCity(DummyDataHelper.getDummyCityDto());
		dummySubscriberDto.setStreet(DummyDataHelper.getDummyStreetDto());
		dummySubscriberDto.setBalance(0F);
		dummySubscriberDto.setConnectionDate(LocalDate.now().minusDays(1));

		when(subscriberService.findOneById(SUBSCRIBER_ID)).thenReturn(dummySubscriberDto);

		mvc.perform(get("/sub/v1/subscriber/{id}", SUBSCRIBER_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(SUBSCRIBER_ID))
				.andExpect(jsonPath("firstname").isNotEmpty())
				.andExpect(jsonPath("patronymic").isNotEmpty())
				.andExpect(jsonPath("lastname").isNotEmpty())
				.andExpect(jsonPath("contractNumber").isNotEmpty())
				.andExpect(jsonPath("accountNumber").isNotEmpty())
				.andExpect(jsonPath("city.name").isNotEmpty())
				.andExpect(jsonPath("street.name").isNotEmpty())
				.andExpect(jsonPath("house").isNotEmpty())
				.andExpect(jsonPath("flat").isNotEmpty())
				.andExpect(jsonPath("phoneNumber").isNotEmpty())
				.andExpect(jsonPath("email").isNotEmpty())
				.andExpect(jsonPath("balance").isNotEmpty())
				.andExpect(jsonPath("isActive").isNotEmpty())
				.andExpect(jsonPath("connectionDate").isNotEmpty());

		verify(subscriberService, times(1)).findOneById(SUBSCRIBER_ID);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен создать абонента и вернуть 200 статус")
	void shouldCreateSubscriberAndReturn200() throws Exception {
		SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto(SUBSCRIBER_ID);
		dummySubscriberDto.setBalance(0F);
		dummySubscriberDto.setConnectionDate(LocalDate.now().minusDays(1));
		dummySubscriberDto.setCity(DummyDataHelper.getDummyCityDto());
		dummySubscriberDto.setStreet(DummyDataHelper.getDummyStreetDto());
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String subscriberJson = gson.toJson(dummySubscriberDto);

		when(subscriberService.create(dummySubscriberDto)).thenReturn(dummySubscriberDto);

		mvc.perform(post("/sub/v1/subscriber")
						.contentType(MediaType.APPLICATION_JSON)
						.content(subscriberJson))
				.andExpect(status().isOk());

		verify(subscriberService, times(1)).create(dummySubscriberDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен создать абонента и должен вернуть 403 статус")
	void shouldNotCreateSubscriberAndReturn403() throws Exception {
		SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto(SUBSCRIBER_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String subscriberJson = gson.toJson(dummySubscriberDto);

		when(subscriberService.create(dummySubscriberDto)).thenReturn(dummySubscriberDto);

		mvc.perform(post("/sub/v1/subscriber")
						.contentType(MediaType.APPLICATION_JSON)
						.content(subscriberJson))
				.andExpect(status().isForbidden());

		verify(subscriberService, times(0)).create(dummySubscriberDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен обновить абонента и вернуть 200 статус")
	void shouldUpdateSubscriberAndReturn200() throws Exception {
		SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto(SUBSCRIBER_ID);
		dummySubscriberDto.setBalance(0F);
		dummySubscriberDto.setConnectionDate(LocalDate.now().minusDays(1));
		dummySubscriberDto.setCity(DummyDataHelper.getDummyCityDto());
		dummySubscriberDto.setStreet(DummyDataHelper.getDummyStreetDto());
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String subscriberJson = gson.toJson(dummySubscriberDto);

		when(subscriberService.update(dummySubscriberDto)).thenReturn(dummySubscriberDto);

		mvc.perform(patch("/sub/v1/subscriber/{id}", SUBSCRIBER_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(subscriberJson))
				.andExpect(status().isOk());

		verify(subscriberService, times(1)).update(dummySubscriberDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен обновить абонента и должен вернуть 403 статус")
	void shouldNotUpdateSubscriberAndReturn403() throws Exception {
		SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto(SUBSCRIBER_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String subscriberJson = gson.toJson(dummySubscriberDto);

		when(subscriberService.update(dummySubscriberDto)).thenReturn(dummySubscriberDto);

		mvc.perform(patch("/sub/v1/subscriber/{id}", SUBSCRIBER_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(subscriberJson))
				.andExpect(status().isForbidden());

		verify(subscriberService, times(0)).update(dummySubscriberDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен удалить абонента и вернуть 200 статус")
	void shouldDeleteSubscriberAndReturn200() throws Exception {
		doNothing().when(subscriberService).deleteById(SUBSCRIBER_ID);

		mvc.perform(delete("/sub/v1/subscriber/{id}", SUBSCRIBER_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(subscriberService, times(1)).deleteById(SUBSCRIBER_ID);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен удалить абонента и должен вернуть 403 статус")
	void shouldNotDeleteSubscriberAndReturn403() throws Exception {
		doNothing().when(subscriberService).deleteById(SUBSCRIBER_ID);

		mvc.perform(delete("/sub/v1/subscriber/{id}", SUBSCRIBER_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(subscriberService, times(0)).deleteById(SUBSCRIBER_ID);
	}

}
