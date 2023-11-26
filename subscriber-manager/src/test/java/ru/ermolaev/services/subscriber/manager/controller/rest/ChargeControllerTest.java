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
import ru.ermolaev.services.subscriber.manager.rest.ChargeController;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeDto;
import ru.ermolaev.services.subscriber.manager.service.ChargeService;
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

@DisplayName("Rest контроллер по работе с начислениями ")
@Import(ChargeController.class)
@WebMvcTest(ChargeController.class)
class ChargeControllerTest {

	private static final Long CHARGE_ID = 1L;

	private static final Long SUBSCRIBER_ID = 1L;

	@MockBean
	private ChargeService chargeService;

	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser
	@DisplayName("должен вернуть список всех начислений для абонента")
	void shouldReturnAllChargesForSubscriber() throws Exception {
		when(chargeService.findAllBySubscriberId(SUBSCRIBER_ID)).thenReturn(List.of(DummyDataHelper.getDummyChargeDto(SUBSCRIBER_ID)));

		mvc.perform(get("/sub/v1/charge/subscriber/{id}", SUBSCRIBER_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(CHARGE_ID))
				.andExpect(jsonPath("$[0].chargeDate").isNotEmpty())
				.andExpect(jsonPath("$[0].amount").isNotEmpty())
				.andExpect(jsonPath("$[0].period").isNotEmpty())
				.andExpect(jsonPath("$[0].comment").isNotEmpty());

		verify(chargeService, times(1)).findAllBySubscriberId(SUBSCRIBER_ID);
	}

	@Test
	@WithMockUser
	@DisplayName("должен вернуть начисление по ID")
	void shouldReturnChargeById() throws Exception {
		when(chargeService.findOneById(CHARGE_ID)).thenReturn(DummyDataHelper.getDummyChargeDto(CHARGE_ID));

		mvc.perform(get("/sub/v1/charge/{id}", CHARGE_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(CHARGE_ID))
				.andExpect(jsonPath("chargeDate").isNotEmpty())
				.andExpect(jsonPath("amount").isNotEmpty())
				.andExpect(jsonPath("period").isNotEmpty())
				.andExpect(jsonPath("comment").isNotEmpty());

		verify(chargeService, times(1)).findOneById(CHARGE_ID);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен создать начисление и вернуть 200 статус")
	void shouldCreateChargeAndReturn200() throws Exception {
		ChargeDto dummyChargeDto = DummyDataHelper.getDummyChargeDto(CHARGE_ID);
		dummyChargeDto.setChargeTarget(DummyDataHelper.getDummyChargeTargetDto());
		dummyChargeDto.setSubscriberId(SUBSCRIBER_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String chargeJson = gson.toJson(dummyChargeDto);

		when(chargeService.create(dummyChargeDto)).thenReturn(dummyChargeDto);

		mvc.perform(post("/sub/v1/charge")
						.contentType(MediaType.APPLICATION_JSON)
						.content(chargeJson))
				.andExpect(status().isOk());

		verify(chargeService, times(1)).create(dummyChargeDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен создать начисление и должен вернуть 403 статус")
	void shouldNotCreateChargeAndReturn403() throws Exception {
		ChargeDto dummyChargeDto = DummyDataHelper.getDummyChargeDto(CHARGE_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String chargeJson = gson.toJson(dummyChargeDto);

		when(chargeService.create(dummyChargeDto)).thenReturn(dummyChargeDto);

		mvc.perform(post("/sub/v1/charge")
						.contentType(MediaType.APPLICATION_JSON)
						.content(chargeJson))
				.andExpect(status().isForbidden());

		verify(chargeService, times(0)).create(dummyChargeDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен обновить начисление и вернуть 200 статус")
	void shouldUpdateChargeAndReturn200() throws Exception {
		ChargeDto dummyChargeDto = DummyDataHelper.getDummyChargeDto(CHARGE_ID);
		dummyChargeDto.setChargeTarget(DummyDataHelper.getDummyChargeTargetDto());
		dummyChargeDto.setSubscriberId(SUBSCRIBER_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String chargeJson = gson.toJson(dummyChargeDto);

		when(chargeService.update(dummyChargeDto)).thenReturn(dummyChargeDto);

		mvc.perform(patch("/sub/v1/charge/{id}", CHARGE_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(chargeJson))
				.andExpect(status().isOk());

		verify(chargeService, times(1)).update(dummyChargeDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен обновить начисление и должен вернуть 403 статус")
	void shouldNotUpdateChargeAndReturn403() throws Exception {
		ChargeDto dummyChargeDto = DummyDataHelper.getDummyChargeDto(CHARGE_ID);
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new GsonLocalDateAdapter())
				.create();
		String chargeJson = gson.toJson(dummyChargeDto);

		when(chargeService.update(dummyChargeDto)).thenReturn(dummyChargeDto);

		mvc.perform(patch("/sub/v1/charge/{id}", CHARGE_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(chargeJson))
				.andExpect(status().isForbidden());

		verify(chargeService, times(0)).update(dummyChargeDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен удалить начисление и вернуть 200 статус")
	void shouldDeleteChargeAndReturn200() throws Exception {
		doNothing().when(chargeService).deleteById(CHARGE_ID);

		mvc.perform(delete("/sub/v1/charge/{id}", CHARGE_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(chargeService, times(1)).deleteById(CHARGE_ID);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен удалить начисление и должен вернуть 403 статус")
	void shouldNotDeleteChargeAndReturn403() throws Exception {
		doNothing().when(chargeService).deleteById(CHARGE_ID);

		mvc.perform(delete("/sub/v1/charge/{id}", CHARGE_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(chargeService, times(0)).deleteById(CHARGE_ID);
	}

}
