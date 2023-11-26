package ru.ermolaev.services.subscriber.manager.controller.rest;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.ermolaev.services.subscriber.manager.rest.StreetController;
import ru.ermolaev.services.subscriber.manager.rest.dto.StreetDto;
import ru.ermolaev.services.subscriber.manager.service.StreetService;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

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

@DisplayName("Rest контроллер по работе с улицами ")
@Import(StreetController.class)
@WebMvcTest(StreetController.class)
class StreetControllerTest {

	private static final Long STREET_ID = 1L;

	@MockBean
	private StreetService streetService;

	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser
	@DisplayName("должен вернуть список всех улиц")
	void shouldReturnAllStreets() throws Exception {
		when(streetService.findAll()).thenReturn(List.of(DummyDataHelper.getDummyStreetDto(STREET_ID)));

		mvc.perform(get("/sub/v1/street").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(STREET_ID))
				.andExpect(jsonPath("$[0].name").isNotEmpty());

		verify(streetService, times(1)).findAll();
	}

	@Test
	@WithMockUser
	@DisplayName("должен вернуть улицу по ID")
	void shouldReturnStreetById() throws Exception {
		when(streetService.findOneById(STREET_ID)).thenReturn(DummyDataHelper.getDummyStreetDto(STREET_ID));

		mvc.perform(get("/sub/v1/street/{id}", STREET_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(STREET_ID))
				.andExpect(jsonPath("name").isNotEmpty());

		verify(streetService, times(1)).findOneById(STREET_ID);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен создать улицу и вернуть 200 статус")
	void shouldCreateStreetAndReturn200() throws Exception {
		StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto(STREET_ID);
		Gson gson = new Gson();
		String streetJson = gson.toJson(dummyStreetDto);

		when(streetService.create(dummyStreetDto)).thenReturn(dummyStreetDto);

		mvc.perform(post("/sub/v1/street")
						.contentType(MediaType.APPLICATION_JSON)
						.content(streetJson))
				.andExpect(status().isOk());

		verify(streetService, times(1)).create(dummyStreetDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен создать улицу и должен вернуть 403 статус")
	void shouldNotCreateStreetAndReturn403() throws Exception {
		StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto(STREET_ID);
		Gson gson = new Gson();
		String streetJson = gson.toJson(dummyStreetDto);

		when(streetService.create(dummyStreetDto)).thenReturn(dummyStreetDto);

		mvc.perform(post("/sub/v1/street")
						.contentType(MediaType.APPLICATION_JSON)
						.content(streetJson))
				.andExpect(status().isForbidden());

		verify(streetService, times(0)).create(dummyStreetDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен обновить улицу и вернуть 200 статус")
	void shouldUpdateStreetAndReturn200() throws Exception {
		StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto(STREET_ID);
		Gson gson = new Gson();
		String streetJson = gson.toJson(dummyStreetDto);

		when(streetService.update(dummyStreetDto)).thenReturn(dummyStreetDto);

		mvc.perform(patch("/sub/v1/street/{id}", STREET_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(streetJson))
				.andExpect(status().isOk());

		verify(streetService, times(1)).update(dummyStreetDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен обновить улицу и должен вернуть 403 статус")
	void shouldNotUpdateStreetAndReturn403() throws Exception {
		StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto(STREET_ID);
		Gson gson = new Gson();
		String streetJson = gson.toJson(dummyStreetDto);

		when(streetService.update(dummyStreetDto)).thenReturn(dummyStreetDto);

		mvc.perform(patch("/sub/v1/street/{id}", STREET_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(streetJson))
				.andExpect(status().isForbidden());

		verify(streetService, times(0)).update(dummyStreetDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен удалить улицу и вернуть 200 статус")
	void shouldDeleteStreetAndReturn200() throws Exception {
		doNothing().when(streetService).deleteById(STREET_ID);

		mvc.perform(delete("/sub/v1/street/{id}", STREET_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(streetService, times(1)).deleteById(STREET_ID);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен удалить улицу и должен вернуть 403 статус")
	void shouldNotDeleteStreetAndReturn403() throws Exception {
		doNothing().when(streetService).deleteById(STREET_ID);

		mvc.perform(delete("/sub/v1/street/{id}", STREET_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(streetService, times(0)).deleteById(STREET_ID);
	}

}
