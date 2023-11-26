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
import ru.ermolaev.services.subscriber.manager.rest.ChargeTargetController;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeTargetDto;
import ru.ermolaev.services.subscriber.manager.service.ChargeTargetService;
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

@DisplayName("Rest контроллер по работе с целями списаний ")
@Import(ChargeTargetController.class)
@WebMvcTest(ChargeTargetController.class)
class ChargeTargetControllerTest {

	private static final Long CHARGE_TARGET_ID = 1L;

	@MockBean
	private ChargeTargetService chargeTargetService;

	@Autowired
	private MockMvc mvc;

	@Test
	@WithMockUser
	@DisplayName("должен вернуть список всех целей списания")
	void shouldReturnAllChargeTargets() throws Exception {
		when(chargeTargetService.findAll()).thenReturn(List.of(DummyDataHelper.getDummyChargeTargetDto(CHARGE_TARGET_ID)));

		mvc.perform(get("/sub/v1/charge-target").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(CHARGE_TARGET_ID))
				.andExpect(jsonPath("$[0].name").isNotEmpty());

		verify(chargeTargetService, times(1)).findAll();
	}

	@Test
	@WithMockUser
	@DisplayName("должен вернуть цель списания по ID")
	void shouldReturnChargeTargetById() throws Exception {
		when(chargeTargetService.findOneById(CHARGE_TARGET_ID)).thenReturn(DummyDataHelper.getDummyChargeTargetDto(CHARGE_TARGET_ID));

		mvc.perform(get("/sub/v1/charge-target/{id}", CHARGE_TARGET_ID).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(CHARGE_TARGET_ID))
				.andExpect(jsonPath("name").isNotEmpty());

		verify(chargeTargetService, times(1)).findOneById(CHARGE_TARGET_ID);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен создать цель списания и вернуть 200 статус")
	void shouldCreateChargeTargetAndReturn200() throws Exception {
		ChargeTargetDto dummyChargeTargetDto = DummyDataHelper.getDummyChargeTargetDto(CHARGE_TARGET_ID);
		Gson gson = new Gson();
		String chargeTargetJson = gson.toJson(dummyChargeTargetDto);

		when(chargeTargetService.create(dummyChargeTargetDto)).thenReturn(dummyChargeTargetDto);

		mvc.perform(post("/sub/v1/charge-target")
						.contentType(MediaType.APPLICATION_JSON)
						.content(chargeTargetJson))
				.andExpect(status().isOk());

		verify(chargeTargetService, times(1)).create(dummyChargeTargetDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен создать цель списания и должен вернуть 403 статус")
	void shouldNotCreateChargeTargetAndReturn403() throws Exception {
		ChargeTargetDto dummyChargeTargetDto = DummyDataHelper.getDummyChargeTargetDto(CHARGE_TARGET_ID);
		Gson gson = new Gson();
		String chargeTargetJson = gson.toJson(dummyChargeTargetDto);

		when(chargeTargetService.create(dummyChargeTargetDto)).thenReturn(dummyChargeTargetDto);

		mvc.perform(post("/sub/v1/charge-target")
						.contentType(MediaType.APPLICATION_JSON)
						.content(chargeTargetJson))
				.andExpect(status().isForbidden());

		verify(chargeTargetService, times(0)).create(dummyChargeTargetDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен обновить цель списания и вернуть 200 статус")
	void shouldUpdateChargeTargetAndReturn200() throws Exception {
		ChargeTargetDto dummyChargeTargetDto = DummyDataHelper.getDummyChargeTargetDto(CHARGE_TARGET_ID);
		Gson gson = new Gson();
		String chargeTargetJson = gson.toJson(dummyChargeTargetDto);

		when(chargeTargetService.update(dummyChargeTargetDto)).thenReturn(dummyChargeTargetDto);

		mvc.perform(patch("/sub/v1/charge-target/{id}", CHARGE_TARGET_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(chargeTargetJson))
				.andExpect(status().isOk());

		verify(chargeTargetService, times(1)).update(dummyChargeTargetDto);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен обновить цель списания и должен вернуть 403 статус")
	void shouldNotUpdateChargeTargetAndReturn403() throws Exception {
		ChargeTargetDto dummyChargeTargetDto = DummyDataHelper.getDummyChargeTargetDto(CHARGE_TARGET_ID);
		Gson gson = new Gson();
		String chargeTargetJson = gson.toJson(dummyChargeTargetDto);

		when(chargeTargetService.update(dummyChargeTargetDto)).thenReturn(dummyChargeTargetDto);

		mvc.perform(patch("/sub/v1/charge-target/{id}", CHARGE_TARGET_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(chargeTargetJson))
				.andExpect(status().isForbidden());

		verify(chargeTargetService, times(0)).update(dummyChargeTargetDto);
	}

	@Test
	@WithMockUser(roles = {"ADMIN"})
	@DisplayName("должен удалить цель списания и вернуть 200 статус")
	void shouldDeleteChargeTargetAndReturn200() throws Exception {
		doNothing().when(chargeTargetService).deleteById(CHARGE_TARGET_ID);

		mvc.perform(delete("/sub/v1/charge-target/{id}", CHARGE_TARGET_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(chargeTargetService, times(1)).deleteById(CHARGE_TARGET_ID);
	}

	@Test
	@WithMockUser(roles = {"USER"})
	@DisplayName("не должен удалить цель списания и должен вернуть 403 статус")
	void shouldNotDeleteChargeTargetAndReturn403() throws Exception {
		doNothing().when(chargeTargetService).deleteById(CHARGE_TARGET_ID);

		mvc.perform(delete("/sub/v1/charge-target/{id}", CHARGE_TARGET_ID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(chargeTargetService, times(0)).deleteById(CHARGE_TARGET_ID);
	}

}
