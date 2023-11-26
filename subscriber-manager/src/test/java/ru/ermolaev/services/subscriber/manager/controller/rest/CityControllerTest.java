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
import ru.ermolaev.services.subscriber.manager.rest.CityController;
import ru.ermolaev.services.subscriber.manager.rest.dto.CityDto;
import ru.ermolaev.services.subscriber.manager.service.CityService;
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

@DisplayName("Rest контроллер по работе с городами ")
@Import(CityController.class)
@WebMvcTest(CityController.class)
class CityControllerTest {

	private static final Long CITY_ID = 1L;

	@MockBean
	private CityService cityService;

	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser
	@DisplayName("должен вернуть список всех городов")
	void shouldReturnAllCities() throws Exception {
		when(cityService.findAll()).thenReturn(List.of(DummyDataHelper.getDummyCityDto(CITY_ID)));

		mvc.perform(get("/sub/v1/city").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(CITY_ID))
				.andExpect(jsonPath("$[0].name").isNotEmpty());

		verify(cityService, times(1)).findAll();
	}

	@Test
	@WithMockUser
	@DisplayName("должен вернуть город по ID")
	void shouldReturnCityById() throws Exception {
		when(cityService.findOneById(CITY_ID)).thenReturn(DummyDataHelper.getDummyCityDto(CITY_ID));

		mvc.perform(get("/sub/v1/city/{id}", CITY_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(CITY_ID))
				.andExpect(jsonPath("name").isNotEmpty());

		verify(cityService, times(1)).findOneById(CITY_ID);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен создать город и вернуть 200 статус")
	void shouldCreateCityAndReturn200() throws Exception {
		CityDto dummyCityDto = DummyDataHelper.getDummyCityDto(CITY_ID);
		Gson gson = new Gson();
		String cityJson = gson.toJson(dummyCityDto);

		when(cityService.create(dummyCityDto)).thenReturn(dummyCityDto);

		mvc.perform(post("/sub/v1/city")
						.contentType(MediaType.APPLICATION_JSON)
						.content(cityJson))
				.andExpect(status().isOk());

		verify(cityService, times(1)).create(dummyCityDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен создать город и должен вернуть 403 статус")
	void shouldNotCreateCityAndReturn403() throws Exception {
		CityDto dummyCityDto = DummyDataHelper.getDummyCityDto(CITY_ID);
		Gson gson = new Gson();
		String cityJson = gson.toJson(dummyCityDto);

		when(cityService.create(dummyCityDto)).thenReturn(dummyCityDto);

		mvc.perform(post("/sub/v1/city")
						.contentType(MediaType.APPLICATION_JSON)
						.content(cityJson))
				.andExpect(status().isForbidden());

		verify(cityService, times(0)).create(dummyCityDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен обновить город и вернуть 200 статус")
	void shouldUpdateCityAndReturn200() throws Exception {
		CityDto dummyCityDto = DummyDataHelper.getDummyCityDto(CITY_ID);
		Gson gson = new Gson();
		String cityJson = gson.toJson(dummyCityDto);

		when(cityService.update(dummyCityDto)).thenReturn(dummyCityDto);

		mvc.perform(patch("/sub/v1/city/{id}", CITY_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(cityJson))
				.andExpect(status().isOk());

		verify(cityService, times(1)).update(dummyCityDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен обновить город и должен вернуть 403 статус")
	void shouldNotUpdateCityAndReturn403() throws Exception {
		CityDto dummyCityDto = DummyDataHelper.getDummyCityDto(CITY_ID);
		Gson gson = new Gson();
		String cityJson = gson.toJson(dummyCityDto);

		when(cityService.update(dummyCityDto)).thenReturn(dummyCityDto);

		mvc.perform(patch("/sub/v1/city/{id}", CITY_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(cityJson))
				.andExpect(status().isForbidden());

		verify(cityService, times(0)).update(dummyCityDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен удалить город и вернуть 200 статус")
	void shouldDeleteCityAndReturn200() throws Exception {
		doNothing().when(cityService).deleteById(CITY_ID);

		mvc.perform(delete("/sub/v1/city/{id}", CITY_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(cityService, times(1)).deleteById(CITY_ID);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен удалить город и должен вернуть 403 статус")
	void shouldNotDeleteCityAndReturn403() throws Exception {
		doNothing().when(cityService).deleteById(CITY_ID);

		mvc.perform(delete("/sub/v1/city/{id}", CITY_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(cityService, times(0)).deleteById(CITY_ID);
	}

}
